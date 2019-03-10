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

  logger.info(s"GAME STATE: \n\n${game.state.map}")
  val a = Json.toJson(game.state.map.territories.head)
  logger.info(s"Got Write[Territory]: ${a}")
  val b = Json.toJson(game.state.map.territories)
  logger.info(s"Got Write[Seq[Territory]]: ${b}")
  val c = Json.toJson(game.state.map)
  logger.info(s"Got Write[Map]: ${c}")
  val serialized = Json.toJson(game.state)
  logger.info(s"serialized: \n\n$serialized")

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
