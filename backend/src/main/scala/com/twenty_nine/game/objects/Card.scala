package com.twenty_nine.game.objects

import com.twenty_nine.game.exceptions.NoPointAssignedException
import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, RootJsonFormat}

import scala.util.Random
import scala.util.Random.shuffle

/**
 * @author staticowl
 */

case class Card(suit: Suit, rank: Rank){
  val hierarchy: List[Rank] = List(Jack, Nine, Ace, Ten, King, Queen, Eight, Seven)
  override def toString: String = s"${suit.toString}, ${rank.toString}"
}

object Card extends DefaultJsonProtocol{
  private val playDeck: Seq[Card] = for {
    suit <- Seq(Hearts, Diamonds, Clubs, Spades)
    rank <- Seq(Seven, Eight, Nine, Ten, Jack, Queen, King, Ace)
  } yield Card(suit, rank)

  private val trumpDeck:Seq[Card] = for {
    suit <- Seq(Hearts, Diamonds, Clubs, Spades)
    rank <- Seq(Two, Three, Four, Five, Six)
  } yield Card(suit, rank)

  def printDeck(): Unit = {
    playDeck.foreach(println)
  }

  def getDeck(cut:Boolean=true, cutPoint:Option[Int] = None):Seq[Card] = {
    var newDeck = shuffle(playDeck)
    if(cut){
      val cutAt: Int = cutPoint.getOrElse(newDeck.length / Random.between(1, 8))
      println(cutAt)
      val (one, two) = newDeck.splitAt(cutAt)
      newDeck = two ++ one
    }
    newDeck
  }

  def getPoint(card: Card):Int = {
    card.rank.points match {
      case Some(p) => p
      case None => throw NoPointAssignedException("Card has no points assigned.")
    }
  }

  def getTrumpDeck: Seq[Card] = this.trumpDeck

  implicit val cardJsonFormat:RootJsonFormat[Card] = new RootJsonFormat[Card] {
    override def read(json: JsValue): Card = Card.playDeck.head

    override def write(obj: Card): JsValue = JsObject(
      "Card" -> JsString(obj.toString),
    )
  }
}

