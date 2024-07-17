package com.twenty_nine.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object GameSessionActor {
  sealed trait Command
  case class JoinGame(playerId: String, replyTo: ActorRef[String]) extends Command
  case class MakeMove(playerId: String, move: String, replyTo: ActorRef[String]) extends Command
  case class GetGameState(replyTo: ActorRef[GameState]) extends Command

  case class GameState(players: Set[String], moves: List[String])

  def apply(gameId: String): Behavior[Command] = Behaviors.setup { context =>
    context.log.info(s"Game session $gameId created")
    gameSession(gameId, Set.empty, List.empty)
  }

  private def gameSession(gameId: String, players: Set[String], moves: List[String]): Behavior[Command] =
    Behaviors.receiveMessage {
      case JoinGame(playerId, replyTo) =>
        if (players.size < 4) {
          val updatedPlayers = players + playerId
          replyTo ! s"Player $playerId joined game $gameId. ${4 - updatedPlayers.size} spots left."
          gameSession(gameId, updatedPlayers, moves)
        } else {
          replyTo ! s"Game $gameId is full."
          Behaviors.same
        }

      case MakeMove(playerId, move, replyTo) =>
        if (players.contains(playerId)) {
          val updatedMoves = s"$playerId: $move" :: moves
          replyTo ! s"Move '$move' accepted for player $playerId in game $gameId"
          gameSession(gameId, players, updatedMoves)
        } else {
          replyTo ! s"Player $playerId is not in game $gameId."
          Behaviors.same
        }

      case GetGameState(replyTo) =>
        replyTo ! GameState(players, moves)
        Behaviors.same
    }
}