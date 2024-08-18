package com.game.controller

import com.game.exceptions.{BadDealException, NotEnoughPlayersException}
import com.game.objects.{Card, Player}

import scala.collection.mutable.ListBuffer
import scala.io.StdIn
import scala.util.Random

/**
 * @author staticowl
 */

class GameController {

  private var players: List[Player] = List[Player]()

  def getPlayers: List[Player] = players

  def setPlayers(): Unit = {
    val newPlayers: ListBuffer[Player] = new ListBuffer[Player]()
    var input: String = "Temp"
    var i: Int = 1
    println("Type skip to fill with bots")
    while (input.nonEmpty && newPlayers.length < 4) {
      println(s"Enter Name of Player $i")
      input = StdIn.readLine()
      if (input.nonEmpty) newPlayers += Player.init(input) else println("Filling Rest of the players with Bots")
      i += 1
    }
    if (newPlayers.length < 1) {
      throw new NotEnoughPlayersException("At least one player is needed. Please retry..")
    }
    else {
      while (newPlayers.length < 4) {
        newPlayers += Player.init(Random.alphanumeric.take(5).mkString)
      }
    }
    players = newPlayers.toList
  }

  def dealCards(cutPoint: Option[Int] = None): Unit = {
    var deck = Card.getDeck(cutPoint = cutPoint)
//    players.foreach(player => {
//      player.addToHand(deck.head)
//      deck = deck.tail
//    })
    deck = dealFour(deck)
//    showCard(players)
//    bet(players)
    print("Waiting")
    StdIn.readLine()
    print("Wait Over")
    deck = dealFour(deck)
    if (deck.nonEmpty) throw new BadDealException("The Deck is not empty after full deal", deck)
  }

  private def dealFour(deck: Seq[Card]): Seq[Card] = {
    var tempDeck = deck.toList
    players.foreach(player => {
      val (hand, rest) = tempDeck.splitAt(4)
      player.addToHand(hand)
      tempDeck = rest
    })
    tempDeck
  }
  private def bet(players: List[Player]): Unit = {
    val minBet = 16
    val maxBet = 28
    //TODO: Reorder players before betting to get the first call
    var currBet = minBet
    var parties = ListBuffer(players.head, players(1))
    var firstBet = true
    while (parties.length == 2) {
      val currentPlayer = parties.head
      parties = parties.tail
      println(s"Place bet for ${currentPlayer.getName}")
      val bet = StdIn.readInt().ensuring(f => f >= minBet && f <= maxBet)
      if ((bet > currBet && firstBet) || (bet >= currBet && !firstBet)) {
        currentPlayer.declare(Some(bet))
        currBet = bet
      }
      else {
        currentPlayer.declare(None)
      }
      firstBet = false
      if (!currentPlayer.getPass) {
        parties += currentPlayer
      }
      else {
        parties += players.filter(!_.getPass).head
      }
    }
  }
}
