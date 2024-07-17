package com.twenty_nine.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.twenty_nine.actor.GameSessionActor.Command

object GameManagerActor {
  sealed trait ManagerCommand
  case class CreateGame(replyTo: ActorRef[String]) extends ManagerCommand
  case class GetGame(gameId: String, replyTo: ActorRef[Option[ActorRef[Command]]]) extends ManagerCommand

  def apply(): Behavior[ManagerCommand] = Behaviors.setup { context =>
    var games = Map.empty[String, ActorRef[Command]]

    Behaviors.receiveMessage {
      case CreateGame(replyTo) =>
        val gameId = java.util.UUID.randomUUID().toString
        val gameSession = context.spawn(GameSessionActor(gameId), s"game-$gameId")
        games += (gameId -> gameSession)
        replyTo ! gameId
        Behaviors.same

      case GetGame(gameId, replyTo) =>
        replyTo ! games.get(gameId)
        Behaviors.same
    }
  }
}