package com.twenty_nine.routes


import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.twenty_nine.actor.GameSessionActor
import com.twenty_nine.actor.GameSessionActor.{JoinGame, MakeMove}

import scala.concurrent.duration._

class GameRoutes(implicit system: ActorSystem[_]) {
  private implicit val timeout: Timeout = 3.seconds
  private val gameSession: ActorRef[GameSessionActor.Command] = system.systemActorOf(GameSessionActor(), "gameSession")

  val route: Route = {
    pathEndOrSingleSlash {
      get {
        complete(200, "Welcome to Twenty Nine")
      }
    } ~
    path("join") {
      parameter("playerId") { playerId =>
        get {
          val result = gameSession.ask(ref => JoinGame(playerId, ref))
          onComplete(result) {
            case scala.util.Success(msg) => complete(msg)
            case scala.util.Failure(_) => complete(StatusCodes.InternalServerError)
          }
        }
      }
    } ~
      path("move") {
        parameters("playerId", "move") { (playerId, move) =>
          get {
            val result = gameSession.ask(ref => MakeMove(playerId, move, ref))
            onComplete(result) {
              case scala.util.Success(msg) => complete(msg)
              case scala.util.Failure(_) => complete(StatusCodes.InternalServerError)
            }
          }
        }
      }
  }
}
