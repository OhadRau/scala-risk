package actors

import akka.actor.{Actor, Props}
import models.{Game, Player}
import play.api.libs.json.Json

sealed trait GameMsg

case class Test(hi: Int) extends GameMsg
case class PlaceArmy(token: String, territoryId: Int) extends GameMsg

case class MoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int) extends GameMsg

object SerializableGameMsg {
  implicit val placeArmyRead = Json.reads[PlaceArmy]
  implicit val moveArmyRead = Json.reads[MoveArmy]
  implicit val testRead = Json.reads[Test]
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
  var placeArmyOrder: Stream[Player] = Stream.continually(players.toStream).flatten.take((game.state.map.territories.size / players.size) * players.size)
  logger.info(s"size: ${placeArmyOrder.size}")
  notifyPlayerTurn()

  override def receive: Receive = {
    case PlaceArmy(token: String, territory: Int) =>
      for {
        player <- players.find(p => p.client.forall(c => c.client.token == token))
      } yield handlePlaceArmy(player, territory)
    case MoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int) =>
      handleMoveArmy(armyCount, territoryFrom, territoryTo)
  }

  def handlePlaceArmy(player: Player, territoryId: Int): Unit = {
    logger.info(placeArmyOrder.take(5).mkString)
    if (player == placeArmyOrder.head) {
      val territory = game.state.map.territories(territoryId)
      if (territory.ownerToken == player.client.get.client.token || territory.ownerToken == "") {
        territory.ownerToken = player.client.get.client.token
        territory.armies += 1
        placeArmyOrder = placeArmyOrder.tail
        notifyGameState()
        notifyPlayerTurn()
        if (placeArmyOrder.isEmpty) {
          notifyGameStart()
        }
      }
    }
  }

  def handleMoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int): Unit = {
    notifyGameState()
  }

  def notifyGameStart(): Unit = {
    game.players foreach (player => player.client.get.actor ! NotifyGameStart(game.state))
  }

  def notifyPlayerTurn(): Unit = {
    if (placeArmyOrder.nonEmpty) {
      game.players foreach (player => player.client.get.actor ! NotifyTurn(placeArmyOrder.head.client.get.client.publicToken))
    }
  }

  def notifyGameState(): Unit = {
    game.players foreach (player => player.client.get.actor ! NotifyGameState(game.state))
  }
}
