package com.twenty_nine.game.objects

import com.twenty_nine.game.exceptions.{CardNotFoundException, UnsupportedTypeException}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsBoolean, JsNumber, JsObject, JsString, JsValue, RootJsonFormat, enrichAny}

import scala.collection.mutable.ListBuffer

case class Player(playerName: String) {
  private var hand: ListBuffer[Card] = new ListBuffer[Card]
  private var betScore: Int = 0
  private var pass: Boolean = false
  private var trump: Either[Boolean, Card] = Left(false)

  def getHand: List[Card] = this.hand.toList

  def addToHand(card: Any): Unit = {
    card match {
      case c: Card => this.hand += c
      case l: List[_] => l.foreach {
        case c: Card => this.hand += c
        case _ => throw UnsupportedTypeException("Only List[Card] is supported.")
      }
      case s: Seq[_] => s.foreach {
        case c: Card => this.hand += c
        case _ => throw UnsupportedTypeException("Only Seq[Card] is supported.")
      }
      case _ => throw UnsupportedTypeException("Only Card, List[Card], and Seq[Card] are supported.")
    }
  }

  def setHand(cards: ListBuffer[Card]): Unit = this.hand = cards

  def getCall: Int = this.betScore

  def setCall(bet: Int): Unit = this.betScore = bet

  def getPass: Boolean = this.pass

  def setPass(pass: Boolean): Unit = this.pass = pass

  def declare(bet: Option[Int] = None): Unit = {
    bet match {
      case Some(value) => setCall(value)
      case None => setPass(true)
    }
  }

  def dealCard(card: Card): Unit = {
    if (this.hand.toList.contains(card)) {
      this.hand.remove(this.hand.indexOf(card))
    }
    else {
      throw CardNotFoundException(s"$card doesn't exist in ${this.playerName}'s hand")
    }
  }

  def setTrump(card: Either[Boolean, Card]): Unit = this.trump = card

  def getTrump: Option[Card] = {
    this.trump match {
      case Right(card: Card) => Some(card)
      case Left(_) => None
    }
  }
}

object Player extends DefaultJsonProtocol {
  implicit object PlayerJsonFormat extends RootJsonFormat[Player] {
    def write(player: Player): JsValue = JsObject(
      "playerName" -> JsString(player.playerName),
      "hand" -> JsArray(player.getHand.map(_.toJson).toVector),
      "betScore" -> JsNumber(player.getCall),
      "pass" -> JsBoolean(player.getPass),
      "trump" -> (player.getTrump match {
        case Some(card) => JsObject("card" -> card.toJson)
        case None => JsObject("boolean" -> JsBoolean(false))
      })
    )

    def read(value: JsValue): Player = Player.apply("Default")
  }
}
