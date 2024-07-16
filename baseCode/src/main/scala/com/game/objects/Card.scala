package com.game.objects

import scala.util.Random
import scala.util.Random.shuffle

/**
 * @author staticowl
 */

sealed trait Suit

case object Hearts extends Suit

case object Clubs extends Suit

case object Diamonds extends Suit

case object Spades extends Suit

sealed trait Rank {
  def points: Int
}

case object Jack extends Rank {
  val points = 3
}

case object Nine extends Rank {
  val points = 2
}

case object Ace extends Rank {
  val points = 1
}

case object Ten extends Rank {
  val points = 1
}

case object King extends Rank {
  val points = 0
}

case object Queen extends Rank {
  val points = 0
}

case object Eight extends Rank {
  val points = 0
}

case object Seven extends Rank {
  val points = 0
}

case class Card(suit: Suit, rank: Rank){
  override def toString: String = s"${suit.toString}, ${rank.toString}"
}

object Card {
  private val deck: Seq[Card] = for {
    suit <- Seq(Hearts, Diamonds, Clubs, Spades)
    rank <- Seq(Seven, Eight, Nine, Ten, Jack, Queen, King, Ace)
  } yield Card(suit, rank)


  def printDeck(): Unit = {
    deck.foreach(println)
  }

  def getDeck(cut:Boolean=true, cutPoint:Option[Int] = None):Seq[Card] = {
    var newDeck = shuffle(deck)
    if(cut){
      val cutAt: Int = cutPoint.getOrElse(newDeck.length / Random.between(1, 8))
      println(cutAt)
      val (one, two) = newDeck.splitAt(cutAt)
      newDeck = two ++ one
    }
    newDeck
  }

  def getPoint(card: Card):Int = card.rank.points
}
