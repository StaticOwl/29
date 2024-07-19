package com.twenty_nine.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.twenty_nine.actor.GameSessionActor.Command

object GameManagerActor {
  sealed trait ManagerCommand
  case class CreateGame(gameName: String, cardBack: String, playerId: String, replyTo: ActorRef[String]) extends ManagerCommand
  case class GetGame(gameId: String, replyTo: ActorRef[Option[ActorRef[Command]]]) extends ManagerCommand

  def apply(): Behavior[ManagerCommand] = Behaviors.setup { context =>
    var games = Map.empty[String, ActorRef[Command]]

    val defaultGameSession = context.spawn(GameSessionActor("default", "default", "defaultBack"), s"game-default")
    games += ("default" -> defaultGameSession)

    Behaviors.receiveMessage {
      case CreateGame(gameName, cardBack, playerId, replyTo) =>
        val gameId = java.util.UUID.randomUUID().toString
        val gameSession = context.spawn(GameSessionActor(gameId, gameName, cardBack), s"game-$gameId")
        gameSession ! GameSessionActor.JoinGame(playerId, replyTo)
        games += (gameId -> gameSession)
        replyTo ! gameId
        Behaviors.same

      case GetGame(gameId, replyTo) =>
        replyTo ! games.get(gameId)
        Behaviors.same
    }
  }
}
