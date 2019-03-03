package actors

import akka.actor.{Actor, Props}
import models.{Game, Player}

object GameActor {
  def props(players: Seq[Player]): Props = Props(new GameActor(players))
}

class GameActor(players: Seq[Player]) extends Actor {
  val game: Game = Game(players).get
  val logger = play.api.Logger(getClass)

  game.players foreach (player => player.client.get.actor ! NotifyGameStarted(game.state))

  override def receive: Receive = {
    case msg: GameMsg =>
      game.getPlayerByToken(msg.token) match {
        case Some(player) =>
          implicit val implicitPlayer: Player = player
          msg match {
            case PlaceArmy(_: String, _: String) => handlePlaceArmy()
            case MoveArmy(_: String, _: String, armyCount: Int, territoryFrom: Int, territoryTo: Int) => handleMoveArmy(armyCount, territoryFrom, territoryTo)
          }
        case None => sender() ! Err("Invalid gameId")
      }
    case msg => logger.warn(s"GameActor received a message that wasn't a GameMsg: ${msg.toString}")
  }

  def handlePlaceArmy(): Unit = {

  }

  def handleMoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int): Unit = {

  }
}
