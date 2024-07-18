package com.twenty_nine.routes

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.adapter._
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpOrigin
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.twenty_nine.actor.GameManagerActor.{CreateGame, GetGame}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.{cors => corsDirective}
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import ch.megard.akka.http.cors.scaladsl.model.HttpOriginMatcher
import com.twenty_nine.actor.{GameManagerActor, GameSessionActor}
import com.twenty_nine.entities.CreateGameRequest

import scala.concurrent.duration._
import scala.concurrent.Future

class GameRoutes(gameManager: ActorRef[GameManagerActor.ManagerCommand])(implicit system: ActorSystem[_]) extends JsonSupport {
  private implicit val timeout: Timeout = 3.seconds
  private val log = Logging(system.toClassic, classOf[GameRoutes])

  private val corsSettings = CorsSettings.defaultSettings.withAllowedOrigins(
    HttpOriginMatcher.Default(Seq(HttpOrigin("http://localhost:3000")))
  )

  val route: Route = corsDirective(corsSettings) {
    pathPrefix("game") {
      path("create") {
        post {
          entity(as[CreateGameRequest]) { request =>
            val futureGameId: Future[String] = gameManager.ask(ref => CreateGame(request.gameName, request.cardBack, request.playerId, ref))
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
        path("game-info"/ Segment) { gameId =>
          get {
            log.info("Calling Game Info Section")
            val futureGame: Future[Option[ActorRef[GameSessionActor.Command]]] = gameManager.ask(ref => GetGame(gameId, ref))
            onSuccess((futureGame)) {
              case Some(game) =>{
                val res = game.ask(ref => GameSessionActor.GetGameState(ref))
                onSuccess(res){ gameState =>
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
        path(Segment/ "fillWithBots") { gameId =>
          get {
            val futureGame = gameManager.ask(ref => GetGame(gameId, ref))
            onSuccess(futureGame){
              case Some(game) =>
                val result = game.ask(ref => GameSessionActor.FillWithBot(ref))
                onSuccess(result){
                  msg => complete(msg)
                }
              case  None => complete(StatusCodes.NotModified, s"Game $gameId not found")
            }
          }
        }
    }
  }
}
