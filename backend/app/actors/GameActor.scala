package actors

import akka.actor.{Actor, Props}
import models.{Game, Player}


object GameActor {
  def props(players: Seq[Player]) = Props(new GameActor(players))
}

class GameActor(players: Seq[Player]) extends Actor {
  val game = Game(players)
  game.initGame
  game.players foreach (player => player.client.get.actor ! NotifyGameStarted(game.state))

  override def receive: Receive = {
    case x => println(x)
  }
}
