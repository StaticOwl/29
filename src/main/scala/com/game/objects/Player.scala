package com.game.objects

import com.game.exceptions.UnsupportedTypeException

import scala.collection.mutable.ListBuffer
import scala.swing.{BoxPanel, Component, Label, MainFrame, Orientation, Panel, ScrollPane, TextArea}

/**
 * @author staticowl
 */

class Player(){
  private var playerName:String = ""
  private var hand:ListBuffer[Card] = new ListBuffer[Card]
  private var betScore:Int = 0
  private var pass:Boolean = false
  private var trump:Boolean = false
  private val textArea:TextArea = new TextArea{ editable = false}

  def getName:String = this.playerName
  def setName(playerName: String): Unit = this.playerName = playerName
  def getHand:List[Card] = this.hand.toList
  def addToHand(card: Any) : Unit = {
    card match {
      case c : Card => this.hand += c
      case l : List[Card] => l.foreach(c => this.hand += c)
      case s : Seq[Card] => s.foreach(c => this.hand += c)
      case _ => throw new UnsupportedTypeException("Only Card, List[Card] and Seq[Card] is supported.")
    }
    textArea.text = hand.toList.mkString("", "\n", "")
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

  def playerPanel:Panel =new BoxPanel(Orientation.Vertical){
    contents += new Label(playerName)
    contents += new ScrollPane(textArea)
  }

}

object Player{
  def init(name: String): Player = {
    val player = new Player()
    player.setName(name)
    player
  }
}