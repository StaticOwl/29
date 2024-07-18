package com.twenty_nine.game.objects

import com.twenty_nine.game.exceptions.UnsupportedTypeException

import scala.collection.mutable.ListBuffer

case class Player(playerName:String) {
  private var hand:ListBuffer[Card] = new ListBuffer[Card]
  private var betScore:Int = 0
  private var pass:Boolean = false
  private var trump:Boolean = false

  def getHand:List[Card] = this.hand.toList
  def addToHand(card: Any) : Unit = {
    card match {
      case c: Card => this.hand += c
      case l: List[_] => l.foreach {
        case c: Card => this.hand += c
        case _ => throw new UnsupportedTypeException("Only List[Card] is supported.")
      }
      case s: Seq[_] => s.foreach {
        case c: Card => this.hand += c
        case _ => throw new UnsupportedTypeException("Only Seq[Card] is supported.")
      }
      case _ => throw new UnsupportedTypeException("Only Card, List[Card], and Seq[Card] are supported.")
    }
  }
  def setHand(cards:ListBuffer[Card]) : Unit = this.hand = cards
  def getCall:Int = this.betScore
  def setCall(bet:Int): Unit = this.betScore = bet
  def getPass:Boolean = this.pass
  def setPass(pass:Boolean): Unit = this.pass = pass

  def declare(bet:Option[Int] = None) : Unit ={
    bet match {
      case Some(value) => setCall(value)
      case None => setPass(true)
    }
  }
}
