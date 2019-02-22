package actors

import akka.actor.{Actor, Props}
import models.{Game, Player}


object GameActor {
  def props(players: Seq[Player]) = Props(new GameActor(players))
}

class GameActor(players: Seq[Player]) extends Actor {
  val game = Game(players)
  game.initGame

  override def receive: Receive = {
    case x => println(x)
  }
}
