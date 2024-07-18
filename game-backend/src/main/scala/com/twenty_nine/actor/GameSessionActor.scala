package com.twenty_nine.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.twenty_nine.game.objects.Player

object GameSessionActor {
  sealed trait Command
  case class JoinGame(playerId: String, replyTo: ActorRef[String]) extends Command
  case class MakeMove(playerId: String, move: String, replyTo: ActorRef[String]) extends Command
  case class GetGameState(replyTo: ActorRef[GameState]) extends Command
  case class FillWithBot(replyTo: ActorRef[String]) extends Command
  case class StartGame(replyTo: ActorRef[String]) extends Command

  case class GameState(players: Set[Player], moves: List[String])

  def apply(gameId: String, gameName: String, cardBack: String): Behavior[Command] = Behaviors.setup { context =>
    context.log.info(s"Game session $gameId created with name $gameName and card back $cardBack")
    gameSession(gameId, gameName, cardBack, Set.empty, List.empty)
  }


  private def gameSession(gameId: String, gameName: String, cardBack: String, players: Set[Player], moves: List[String]): Behavior[Command] =
    Behaviors.receiveMessage {
      case JoinGame(playerId, replyTo) =>
        if (players.size < 4) {
          val newPlayer = Player(playerId)
          var updatedPlayers = players
          if (!updatedPlayers.contains(newPlayer)){
            updatedPlayers += newPlayer
          }
          replyTo ! s"Player $playerId joined game $gameId. ${4 - updatedPlayers.size} spots left."
          gameSession(gameId, gameName, cardBack, updatedPlayers, moves)
        } else {
          replyTo ! s"Game $gameId is full."
          Behaviors.same
        }

      case MakeMove(playerId, move, replyTo) =>
        if (players.exists(_.playerName == playerId)) {
          val updatedMoves = s"$playerId: $move" :: moves
          replyTo ! s"Move '$move' accepted for player $playerId in game $gameId"
          gameSession(gameId, gameName, cardBack, players, updatedMoves)
        } else {
          replyTo ! s"Player $playerId is not in game $gameId."
          Behaviors.same
        }

      case GetGameState(replyTo) =>
        replyTo ! GameState(players, moves)
        Behaviors.same

      case FillWithBot(replyTo) =>
        if (players.size < 4) {
          val responseString = new StringBuilder()
          var updatedPlayers = players
          while(updatedPlayers.size < 4){
            val botId = s"Bot${4 - updatedPlayers.size}"
            updatedPlayers += Player(botId)
            responseString ++= s"Bot $botId added to game $gameId.\n"
          }
          replyTo ! responseString.toString()
          gameSession(gameId, gameName, cardBack, updatedPlayers, moves)
        } else {
          replyTo ! s"Game $gameId is full."
          Behaviors.same
        }

      case StartGame(replyTo) =>
        if (players.size == 4) {
          replyTo ! s"Game $gameId started."
          // TODO:Implement game start logic here
        } else {
          replyTo ! s"Cannot start game $gameId. Waiting for ${4 - players.size} more players."
        }
        Behaviors.same
    }
}
