package com.twenty_nine.routes

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler, Route}
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.corsRejectionHandler
import com.twenty_nine.actor.GameManagerActor.{CreateGame, GetGame}
import com.twenty_nine.actor.{GameManagerActor, GameSessionActor}
import com.twenty_nine.entities.CreateGameRequest

import scala.concurrent.Future
import scala.concurrent.duration._

class GameRoutes(gameManager: ActorRef[GameManagerActor.ManagerCommand])(implicit system: ActorSystem[_]) extends JsonSupport {
  private implicit val timeout: Timeout = 3.seconds
  private val log = Logging(system.toClassic, classOf[GameRoutes])

  def route: Route = {

    // Your CORS settings are loaded from `application.conf`

    val rejectionHandler = corsRejectionHandler.withFallback(RejectionHandler.default)

    val exceptionHandler = ExceptionHandler { case e: NoSuchElementException =>
      complete(StatusCodes.NotFound -> e.getMessage)
    }

    // Combining the two handlers only for convenience
    val handleErrors = handleRejections(rejectionHandler) & handleExceptions(exceptionHandler)

    handleErrors{
      cors() {
        pathPrefix("game") {
          path("create") {
            post {
              entity(as[CreateGameRequest]) { request =>
                val futureGameId: Future[String] = gameManager.ask(ref => CreateGame(request.gameName, request.cardBack, request.playerId, ref))
                log.info("Checking if conf is loading")
                onSuccess(futureGameId) { gameId =>
                  complete(s"New game created with ID: $gameId")
                }
              }
            }
          } ~
            path(Segment / "join") { gameId =>
              parameter("playerId") { playerId =>
                get {
                  val futureGame: Future[Option[ActorRef[GameSessionActor.Command]]] = gameManager.ask(ref => GetGame(gameId, ref))
                  onSuccess(futureGame) {
                    case Some(game) =>
                      val result = game.ask(ref => GameSessionActor.JoinGame(playerId, ref))
                      onSuccess(result) { msg =>
                        complete(msg)
                      }
                    case None =>
                      complete(StatusCodes.NotFound, s"Game $gameId not found")
                  }
                }
              }
            } ~
            path("game-info" / Segment) { gameId =>
              get {
                log.info("Calling Game Info Section")
                val futureGame: Future[Option[ActorRef[GameSessionActor.Command]]] = gameManager.ask(ref => GetGame(gameId, ref))
                onSuccess((futureGame)) {
                  case Some(game) => {
                    val res = game.ask(ref => GameSessionActor.GetGameState(ref))
                    onSuccess(res) { gameState =>
                      complete(gameState.toString)
                    }
                  }
                  case None => complete(StatusCodes.NotFound, s"Game $gameId not found")
                }
              }
            } ~
            path(Segment / "move") { gameId =>
              parameters("playerId", "move") { (playerId, move) =>
                get {
                  val futureGame: Future[Option[ActorRef[GameSessionActor.Command]]] = gameManager.ask(ref => GetGame(gameId, ref))
                  onSuccess(futureGame) {
                    case Some(game) =>
                      val result = game.ask(ref => GameSessionActor.MakeMove(playerId, move, ref))
                      onSuccess(result) { msg =>
                        complete(msg)
                      }
                    case None =>
                      complete(StatusCodes.NotFound, s"Game $gameId not found")
                  }
                }
              }
            } ~
            path(Segment / "fillWithBots") { gameId =>
              get {
                val futureGame = gameManager.ask(ref => GetGame(gameId, ref))
                onSuccess(futureGame) {
                  case Some(game) =>
                    val result = game.ask(ref => GameSessionActor.FillWithBot(ref))
                    onSuccess(result) {
                      msg => complete(msg)
                    }
                  case None => complete(StatusCodes.NotModified, s"Game $gameId not found")
                }
              }
            }
        }
      }
    }
  }
}
