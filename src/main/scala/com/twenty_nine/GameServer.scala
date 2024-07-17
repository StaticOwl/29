package com.twenty_nine

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.twenty_nine.routes.GameRoutes

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object GameServer {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem[Any] = ActorSystem(Behaviors.empty, "GameServer")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val gameRoutes = new GameRoutes()
    val corsRoutes = cors() {
      gameRoutes.route
    }

    val port = sys.env.getOrElse("PORT", "8080").toInt
    val bindingFuture = Http().newServerAt("0.0.0.0", port).bind(corsRoutes)

    bindingFuture.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at https://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}