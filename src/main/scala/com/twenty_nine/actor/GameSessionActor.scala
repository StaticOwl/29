package com.twenty_nine.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object GameSessionActor {
  sealed trait Command
  case class JoinGame(playerId: String, replyTo: ActorRef[String]) extends Command
  case class MakeMove(playerId: String, move: String, replyTo: ActorRef[String]) extends Command

  def apply(): Behavior[Command] = Behaviors.setup { context =>
    var players = Set.empty[String]

    Behaviors.receiveMessage {
      case JoinGame(playerId, replyTo) =>
        if (players.size < 4) {
          players += playerId
          replyTo ! s"Player $playerId joined. ${4 - players.size} spots left."
        } else {
          replyTo ! "Game is full."
        }
        Behaviors.same

      case MakeMove(playerId, move, replyTo) =>
        if (players.contains(playerId)) {
          // Process the move
          replyTo ! s"Move '$move' accepted for player $playerId"
        } else {
          replyTo ! "Player not in this game."
        }
        Behaviors.same
    }
  }
}
