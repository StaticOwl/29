package com.twenty_nine.server.requests

final case class CreateGameRequest(gameName: String, cardBack: String, playerId: String)
