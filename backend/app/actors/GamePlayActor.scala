package actors

import akka.actor.{Actor, Props, Timers}
import models.{Game, Play, Player}
import play.api.libs.json._

import scala.util.Random

trait TurnPhase

case object PlaceArmies extends TurnPhase
case object Attack extends TurnPhase
case object Fortify extends TurnPhase

case class TerritoryReady(id: Int)

case object StartGamePlay

object SerializableTurnPhase {
  implicit object turnPhaseWrites extends Writes[TurnPhase] {
    def writes(phase: TurnPhase): JsValue = phase match {
      case PlaceArmies => Json.toJson("PlaceArmies")
      case Attack => Json.toJson("Attack")
      case Fortify => Json.toJson("Fortify")
    }
  }
}

object GamePlayActor {
  def props(players: Seq[Player], game: Game): Props = Props(new GamePlayActor(players, game))
}

class GamePlayActor(players: Seq[Player], game: Game) extends Actor with Timers {
  val logger = play.api.Logger(getClass)

  override def receive: Receive = {
    case StartGamePlay =>
      logger.info("Starting GamePlay Phase!")
      // Create timer for each territory
      game.state.map.territories foreach (territory => {
        timers.startPeriodicTimer(territory.id, TerritoryReady(territory.id), game.state.map.interval)
      })
    case PlaceArmy(token: String, territory: Int) =>
      logger.debug("Got PlaceArmy!")
      for {
        player <- players.find(p => p.client.forall(c => c.client.token == token))
      } yield handlePlaceArmy(player, territory)
    case MoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int) =>
      handleMoveArmy(armyCount, territoryFrom, territoryTo)
    case AttackTerritory(token: String, territoryFrom: Int, territoryTo: Int, armyCount: Int) =>
      for {
        player <- players.find(p => p.client.forall(c => c.client.token == token))
      } yield handleAttack(player, territoryFrom, territoryTo, armyCount)
    case TerritoryReady(territoryId: Int) => handleTerritoryReady(territoryId)
  }

  def handlePlaceArmy(player: Player, territoryId: Int): Unit = {
    logger.debug(s"Got a handlePlaceArmy with $player and $territoryId")
    // Check that territory is unclaimed or claimed by player
    val territory = game.state.map.territories(territoryId)
    if (territory.ownerToken == player.client.get.client.publicToken || territory.ownerToken == "") {
      if (player.unitCount > 0) {
        territory.ownerToken = player.client.get.client.publicToken
        territory.armies += 1
        player.unitCount -= 1

        notifyGameState()
      }
    }
  }

  def handleAttack(player: Player, territoryFromId: Int, territoryToId: Int, armyCount: Int): Unit = {
    val territoryFrom = game.state.map.territories(territoryFromId)
    val territoryTo = game.state.map.territories(territoryToId)
    val playerToken = player.client.get.client.publicToken
    val diceRandom = Stream.continually(Random.nextInt(5)+1)
    if (territoryFrom.ownerToken == playerToken && territoryTo.ownerToken != playerToken) {
      if (territoryFrom.neighbours.contains(territoryTo)) {
        val attackRoll = diceRandom.take(armyCount)
          .toList.sorted(Ordering[Int].reverse)
          .take(territoryTo.armies)
        val defenseRoll = diceRandom.take(attackRoll.size).toList
          .sorted(Ordering[Int].reverse)
        (attackRoll zip defenseRoll)
          .foreach(it =>
            if (it._1 > it._2) territoryTo.armies -= 1
            else territoryFrom.armies -= 1
          )
//        for {
//          player <- players.find(p => p.client.forall(c => c.client.publicToken == territoryTo.ownerToken))
//        } yield player.client.get.actor ! NotifyDefend(territoryToId)
        notifyGameState()
      }
    }
  }

  /**
    * Add to player's unit count
    * @param territoryId id of the territory that is ready
    */
  def handleTerritoryReady(territoryId: Int): Unit = {
    val ownerToken = game.state.map.territories(territoryId).ownerToken
    for {
      player <- players.find(p => p.client.forall(c => c.client.publicToken == ownerToken))
    } yield player.unitCount += 1
    notifyGameState()
  }

  def handleMoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int): Unit = {
    notifyGameState()
  }

  def notifyNewArmies(player: Player): Unit = {
    var newArmies: Integer = 0
    var territoryCount: Integer = 0

    game.state.map.territories foreach (territory => if (territory.ownerToken == player.client.get.client.publicToken) territoryCount += 1)

    //One army for every territories
    newArmies += territoryCount
    //TODO: Add continent based new armies, change armies you get per territory
    player.unitCount += newArmies
    player.client.get.actor ! NotifyNewArmies(newArmies.toString)
    notifyGameState()
  }

  def notifyGameState(): Unit = {
    game.players foreach (player => player.client.get.actor ! NotifyGameState(game.state))
  }
}
