package actors

import akka.actor.{Actor, Props}
import models.{Game, Player}
import play.api.libs.json.Json

object GameActor {
  def props(players: Seq[Player]): Props = Props(new GameActor(players))
}

class GameActor(players: Seq[Player]) extends Actor {
  val logger = play.api.Logger(getClass)
  val game: Game = Game(players) match {
    case Some(createdGame) =>
      logger.info("GOT GAME")
      createdGame
    case None =>
      logger.error("Error creating game wtf")
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
    notifyGameState()
  }

  def handleMoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int): Unit = {
    notifyGameState()
  }

  def notifyGameState(): Unit = {
    game.players foreach(player => player.client.get.actor ! NotifyGameState(game.state))
  }
}
