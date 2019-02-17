package actors

import akka.actor.{Actor, ActorRef}
import models.{Game, Player}

import scala.collection.mutable

class GameActor extends Actor {
  private val game = new Game()
  private val players: mutable.HashMap[String, PlayerWithActor] = collection.mutable.HashMap[String, PlayerWithActor]()

  val logger = play.api.Logger(getClass)

  override def receive: Receive = {
    case Some(GenerateToken(player, actor)) => {
      players += (player.token -> PlayerWithActor(player, actor))
      logger.debug("Generated token for player")
      requestName(player.token, actor)
    }
    case Some(PlayerJoined(name, token)) =>
      players(token).player.name = name
      logger.debug(s"$name Joined")
      notifyGameState()
    case None =>
      logger.debug("None received")
  }

  def notifyGameState(): Unit = {
    for ((_, player) <- players) {
      player.actor ! NotifyGameState(game.state)
    }
  }

  def requestName(token: String, actor: ActorRef): Unit = {
    actor ! RequestName(token)
  }
}

case class PlayerWithActor(player: Player, actor: ActorRef)