package com.twenty_nine.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.twenty_nine.entities.CreateGameRequest
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

// JSON support for our case classes
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val createGameRequestFormat: RootJsonFormat[CreateGameRequest] = jsonFormat3(CreateGameRequest)
}
