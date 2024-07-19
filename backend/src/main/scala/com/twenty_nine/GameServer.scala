package com.twenty_nine

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.event.Logging
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import com.twenty_nine.actor.GameManagerActor
import com.twenty_nine.routes.GameRoutes
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success}

object GameServer {
  implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "GameServer")
  private val log = Logging(system.toClassic, "GameServer")
  private def startHttpServer(routes: GameRoutes)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    val futureBinding = Http().newServerAt("0.0.0.0", 8080).bind(routes.route)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val rootBehavior: Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
      val gameManager = context.spawn(GameManagerActor(), "game-manager")
      val routes = new GameRoutes(gameManager)(context.system)

      startHttpServer(routes)(context.system)

      Behaviors.empty
    }

    val system = ActorSystem[Nothing](rootBehavior, "GameServer")
  }
}