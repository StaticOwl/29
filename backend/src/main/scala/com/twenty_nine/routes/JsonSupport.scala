package com.twenty_nine.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.twenty_nine.requests.CreateGameRequest
import com.twenty_nine.responses.{CreateGameResponse, FillWithBotsResponse}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val createGameRequestFormat: RootJsonFormat[CreateGameRequest] = jsonFormat3(CreateGameRequest)
  implicit val createGameResponseFormat: RootJsonFormat[CreateGameResponse] = jsonFormat1(CreateGameResponse)
  implicit val fillWithBotsResponseFormat: RootJsonFormat[FillWithBotsResponse] = jsonFormat3(FillWithBotsResponse)
}
