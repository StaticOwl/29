package com.twenty_nine.responses

import com.twenty_nine.game.objects.Player

case class FillWithBotsResponse(gameId:String, players:List[String], msg:String)
