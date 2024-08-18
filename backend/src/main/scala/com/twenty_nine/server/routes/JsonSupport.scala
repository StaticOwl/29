package com.twenty_nine.server.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.twenty_nine.server.requests.CreateGameRequest
import com.twenty_nine.server.responses.{CreateGameResponse, FillWithBotsResponse, GameStateResponse}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val createGameRequestFormat: RootJsonFormat[CreateGameRequest] = jsonFormat3(CreateGameRequest)
  implicit val createGameResponseFormat: RootJsonFormat[CreateGameResponse] = jsonFormat1(CreateGameResponse)
  implicit val fillWithBotsResponseFormat: RootJsonFormat[FillWithBotsResponse] = jsonFormat3(FillWithBotsResponse)
  implicit val gameStateResponseFormat: RootJsonFormat[GameStateResponse] = jsonFormat2(GameStateResponse)
}
