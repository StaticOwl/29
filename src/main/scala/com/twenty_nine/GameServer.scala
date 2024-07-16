package com.twenty_nine

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.twenty_nine.routes.GameRoutes

import scala.util.{Failure, Success}

object GameServer {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "GameServer")
    implicit val executionContext = system.executionContext

    val routes = new GameRoutes()
    val port = sys.env.getOrElse("PORT", "8080").toInt
    val bindingFuture = Http().newServerAt("0.0.0.0", port).bind(routes.route)

    bindingFuture.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}
