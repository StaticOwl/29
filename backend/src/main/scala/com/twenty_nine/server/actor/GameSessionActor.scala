package com.twenty_nine.server.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.twenty_nine.game.MainGame
import com.twenty_nine.game.objects.Player
import com.twenty_nine.server.commands.Command

object GameSessionActor {
  def apply(gameId: String, gameName: String, cardBack: String): Behavior[Command] = Behaviors.setup { context =>
    context.log.info(s"Game session $gameId created with name $gameName and card back $cardBack")
    gameSession(gameId, gameName, cardBack, Set.empty)
  }
  private def gameSession(gameId: String, gameName: String, cardBack: String, players: Set[Player]): Behavior[Command] =
    Behaviors.receiveMessage {
      case JoinGame(playerId, replyTo) =>
        if (players.size < 4) {
          val newPlayer = Player(playerId)
          var updatedPlayers = players
          if (!updatedPlayers.contains(newPlayer)){
            updatedPlayers += newPlayer
          }
          replyTo ! s"Player $playerId joined game $gameId. ${4 - updatedPlayers.size} spots left."
          gameSession(gameId, gameName, cardBack, updatedPlayers)
        } else {
          replyTo ! s"Game $gameId is full."
          Behaviors.same
        }

      case GetGameState(replyTo) =>
        replyTo ! GameState(players)
        Behaviors.same

      case FillWithBot(replyTo) =>
        if (players.size < 4) {
          var updatedPlayers = players
          while(updatedPlayers.size < 4){
            val botId = s"Bot${4 - updatedPlayers.size}"
            updatedPlayers += Player(botId)
          }
          replyTo ! GameInfo(gameId = gameId,
            playerNames = updatedPlayers.map(_.playerName).toList,
            msg = s"Added ${updatedPlayers.count(p => p.playerName.contains("Bot"))} Bots")
          gameSession(gameId, gameName, cardBack, updatedPlayers)
        } else {
          replyTo ! GameInfo(gameId = gameId,
            playerNames = players.map(_.playerName).toList,
            msg = s"Already 4 players. No Bots added.")
          Behaviors.same
        }

      case StartGame(replyTo) =>
        if (players.size == 4) {
          replyTo ! s"Game $gameId started."
          new MainGame(gameId, players.toList).startGame()
          Behaviors.same
        } else {
          replyTo ! s"Cannot start game $gameId. Waiting for ${4 - players.size} more players."
        }
        Behaviors.same
    }
}

case class JoinGame(playerId: String, replyTo: ActorRef[String]) extends Command
case class GetGameState(replyTo: ActorRef[GameState]) extends Command
case class FillWithBot(replyTo: ActorRef[GameInfo]) extends Command
case class StartGame(replyTo: ActorRef[String]) extends Command

case class GameState(players: Set[Player])
case class GameInfo(gameId: String, playerNames: List[String], msg:String)
