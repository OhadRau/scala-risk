package actors

import akka.actor.{Actor, Props}
import models.{Game, Player}

object GameActor {
  def props(players: Seq[Player]): Props = Props(new GameActor(players))
}

class GameActor(players: Seq[Player]) extends Actor {
  val game = Game(players)
  val logger = play.api.Logger(getClass)

  game.initGame
  game.players foreach (player => player.client.get.actor ! NotifyGameStarted(game.state))

  override def receive: Receive = {
    case x => logger.debug(x)
  }
}
