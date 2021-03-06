package actors

import akka.actor.{Actor, Props, Timers}
import models.{Game, Player, Territory}
import play.api.libs.json._

import scala.util.Random

trait TurnPhase

case object PlaceArmies extends TurnPhase

case object Attack extends TurnPhase

case object Move extends TurnPhase

case class TerritoryReady(id: Int)

case object StartGamePlay

object SerializableTurnPhase {

  implicit object turnPhaseWrites extends Writes[TurnPhase] {
    def writes(phase: TurnPhase): JsValue = phase match {
      case PlaceArmies => Json.toJson("PlaceArmies")
      case Attack => Json.toJson("Attack")
      case Move => Json.toJson("MoveArmy")
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
    case MoveArmy(token: String, territoryFrom: Int, territoryTo: Int, armyCount: Int) =>
      for {
        player <- players.find(p => p.client.forall(c => c.client.token == token))
      } yield handleMoveArmy(player, armyCount, territoryFrom, territoryTo)
    case AttackTerritory(token: String, territoryFrom: Int, territoryTo: Int, armyCount: Int) =>
      for {
        player <- players.find(p => p.client.forall(c => c.client.token == token))
      } yield handleAttack(player, territoryFrom, territoryTo, armyCount)
    case TerritoryReady(territoryId: Int) => handleTerritoryReady(territoryId)
    case req: GameRequestInfo => notifyGameState()
  }

  def handlePlaceArmy(player: Player, territoryId: Int): Unit = {
    logger.debug(s"Got a handlePlaceArmy with $player and $territoryId")
    // Check that territory is unclaimed or claimed by player
    val publicToken = player.client.get.client.publicToken
    val territory = game.state.map.territories(territoryId)
    if (territory.ownerToken == publicToken || territory.ownerToken == "") {
      if (player.unitCount > 0) {
        territory.ownerToken = publicToken
        territory.armies += 1
        player.unitCount -= 1

        if (isWinner(publicToken)) {
          notifyGameEnd(publicToken)
        }
        notifyGameState()
      }
    }
  }

  def generateDiceRoll(count: Int): List[Int] = {
    val rand = new Random
    Stream.continually(rand.nextInt(5)).take(count).toList
  }

  def handleAttack(player: Player, territoryFromId: Int, territoryToId: Int, armyCount: Int): Unit = {
    val territoryFrom = game.state.map.territories(territoryFromId)
    val territoryTo = game.state.map.territories(territoryToId)
    val playerToken = player.client.get.client.publicToken

    if (territoryFrom.ownerToken == playerToken && territoryTo.ownerToken != playerToken) {
      if (territoryFrom.neighbours.contains(territoryTo)) {
        val attackRoll = generateDiceRoll(armyCount)
          .sorted(Ordering[Int].reverse)
          .take(territoryTo.armies)

        val defenseRoll = generateDiceRoll(Math.min(3, territoryTo.armies))
          .sorted(Ordering[Int].reverse).take(attackRoll.size)

        (attackRoll zip defenseRoll).foreach(it => {
          if (it._1 > it._2) {
            territoryTo.armies -= 1
          }
          else {
            territoryFrom.armies -= 1
          }
        }
        )

        handlePostBattle(territoryFrom, territoryTo)
        notifyGameState()
      }
    }
  }

  def handlePostBattle(territoryFrom: Territory, territoryTo: Territory): Unit = {
    if (territoryTo.armies == 0) {
      territoryTo.ownerToken = territoryFrom.ownerToken
      territoryTo.armies = 1
      territoryFrom.armies -= 1

      if (isWinner(territoryFrom.ownerToken)) {
        notifyGameEnd(territoryFrom.ownerToken)
      }
    }
  }

  def isWinner(token: String): Boolean = {
    game.state.map.territories.forall(_.ownerToken == token)
  }

  def notifyGameEnd(winnerToken: String): Unit = {
    game.players foreach (player => player.client.get.actor ! NotifyGameEnd(game.state, winnerToken))
  }

  /**
    * Add to player's unit count
    *
    * @param territoryId id of the territory that is ready
    */
  def handleTerritoryReady(territoryId: Int): Unit = {
    val ownerToken = game.state.map.territories(territoryId).ownerToken
    for {
      player <- players.find(p => p.client.forall(c => c.client.publicToken == ownerToken))
    } yield player.unitCount += 1
    notifyGameState()
  }

  def handleMoveArmy(player: Player, armyCount: Int, territoryFrom: Int, territoryTo: Int): Unit = {
    val playerToken = player.client.get.client.publicToken
    if (game.state.map.territories(territoryFrom).ownerToken == playerToken) {
       if (game.state.map.territories(territoryTo).ownerToken == playerToken || game.state.map.territories
       (territoryTo).ownerToken == "") {
         game.state.map.territories(territoryFrom).armies -= armyCount
         game.state.map.territories(territoryTo).armies += armyCount

         if (game.state.map.territories(territoryTo).ownerToken == "") {
           game.state.map.territories(territoryTo).ownerToken = playerToken
           if (isWinner(playerToken)) {
             notifyGameEnd(playerToken)
           }
         }
       }
    }
    notifyGameState()
  }

  def notifyNewArmies(player: Player): Unit = {
    var newArmies: Integer = 0
    var territoryCount: Integer = 0

    game.state.map.territories foreach (territory =>
      if (territory.ownerToken == player.client.get.client.publicToken) {
        territoryCount += 1
      })

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
