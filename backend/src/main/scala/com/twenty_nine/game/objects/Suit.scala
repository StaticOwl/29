package com.twenty_nine.game.objects

import akka.http.javadsl.common.EntityStreamingSupport.json
import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonFormat}

sealed trait Suit{
  val name:String
}

case object Hearts extends Suit{
  override val name = "Hearts"
}

case object Clubs extends Suit{
  override val name: String = "Clubs"
}

case object Diamonds extends Suit{
  override val name: String = "Diamonds"
}

case object Spades extends Suit{
  override val name: String = "Suit"
}

object SuitJson extends DefaultJsonProtocol{
  implicit val suitFormat:RootJsonFormat[Suit] = new RootJsonFormat[Suit] {
    override def read(json: JsValue): Suit = Hearts //Dummy point. No need for read.

    override def write(obj: Suit): JsValue = JsObject(
      "SuitName" -> JsString(obj.name)
    )
  }
}