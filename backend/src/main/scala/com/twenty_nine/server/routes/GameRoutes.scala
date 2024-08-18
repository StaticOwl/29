package com.twenty_nine.server.routes

import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, ExceptionHandler, RejectionHandler, Route}
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.corsRejectionHandler
import com.twenty_nine.server.actor.{CreateGame, FillWithBot, GameManagerActor, GameSessionActor, GetGame, GetGameState, JoinGame}
import com.twenty_nine.server.commands.{Command, ManagerCommand}
import com.twenty_nine.server.requests.CreateGameRequest
import com.twenty_nine.server.responses.{CreateGameResponse, FillWithBotsResponse}

import scala.concurrent.Future
import scala.concurrent.duration._

class GameRoutes(gameManager: ActorRef[ManagerCommand])(implicit system: ActorSystem[_]) extends Directives with JsonSupport {
  import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
  private implicit val timeout: Timeout = 3.seconds
  private val log = Logging(system.toClassic, classOf[GameRoutes])

  private def fetchGame(gameId: String): Future[Option[ActorRef[Command]]] = {
    gameManager.ask(ref => GetGame(gameId, ref))
  }

  def route: Route = {
    val rejectionHandler = corsRejectionHandler.withFallback(RejectionHandler.default)

    val exceptionHandler = ExceptionHandler { case e: NoSuchElementException =>
      complete(StatusCodes.NotFound -> e.getMessage)
    }
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
                  complete(CreateGameResponse(gameId))
                }
              }
            }
          } ~
            path(Segment / "join") { gameId =>
              parameter("playerId") { playerId =>
                get {
                  val joinGame =fetchGame(gameId)
                  onSuccess(joinGame) {
                    case Some(game) =>
                      val result = game.ask(ref => JoinGame(playerId, ref))
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
                val gameInfo = fetchGame(gameId)
                onSuccess(gameInfo) {
                  case Some(game) =>
                    val res = game.ask(ref => GetGameState(ref))
                    onSuccess(res) { gameState =>
                      complete(gameState.toString)
                    }
                  case None => complete(StatusCodes.NotFound, s"Game $gameId not found")
                }
              }
            } ~
            path(Segment / "fillWithBots") { gameId =>
              post {
                val futureGame = fetchGame(gameId)
                onSuccess(futureGame) {
                  case Some(game) =>
                    val result = game.ask(ref => FillWithBot(ref))
                    onSuccess(result) {
                      res => complete(FillWithBotsResponse(res.gameId, res.playerNames, res.msg))
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
