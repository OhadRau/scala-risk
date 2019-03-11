package actors

import akka.actor.{Actor, Props}
import models.{Game, Player}
import play.api.libs.json.Json

sealed trait GameMsg

case class PlaceArmy(publicToken: String, territory: Int)
case class MoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int) extends GameMsg

object SerializableGameMsg {
  implicit val placeArmyRead = Json.reads[PlaceArmy]
  implicit val moveArmyRead = Json.reads[MoveArmy]
  implicit val gameMsgRead = Json.reads[GameMsg]
}

object GameActor {
  def props(players: Seq[Player]): Props = Props(new GameActor(players))
}

class GameActor(players: Seq[Player]) extends Actor {
  val logger = play.api.Logger(getClass)
  val game: Game = Game(players) match {
    case Right(createdGame) =>
      logger.info("GOT GAME")
      createdGame
    case Left(errorMsg) =>
      logger.error(s"Error creating game: $errorMsg")
      throw new RuntimeException("Something went wrong lmao")
  }

  val a = Json.toJson(game.state.map.territories.head)
  val b = Json.toJson(game.state.map.territories)
  val c = Json.toJson(game.state.map)
  val serialized = Json.toJson(game.state)

  logger.debug(s"There are ${game.players.size} players in this game")
  game.players foreach (player => player.client.get.actor ! NotifyGameStarted(game.state))
  game.players foreach (player => player.client.get.actor ! SendMapResource(game.state.map.resource))

  override def receive: Receive = {
    case PlaceArmy(publicToken: String, territory: Int) =>
      for {
        player <- players.find(p => p.client.forall(c => c.client.publicToken == publicToken))
      } yield handlePlaceArmy(player, territory)
    case MoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int) =>
      handleMoveArmy(armyCount, territoryFrom, territoryTo)
}

  def handlePlaceArmy(player: Player, territory: Int): Unit = {
    notifyGameState()
  }

  def handleMoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int): Unit = {
    notifyGameState()
  }

  def notifyGameState(): Unit = {
    game.players foreach(player => player.client.get.actor ! NotifyGameState(game.state))
  }
}
