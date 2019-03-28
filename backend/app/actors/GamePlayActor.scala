package actors

import akka.actor.{Actor, Props}
import models.{Game, Play, Player}
import play.api.libs.json._

trait TurnPhase

case object PlaceArmies extends TurnPhase
case object Attack extends TurnPhase
case object Fortify extends TurnPhase

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

class GamePlayActor(players: Seq[Player], game: Game) extends Actor {
  game.state.gamePhase = Play
  val logger = play.api.Logger(getClass)

  // Turn order = (player1, PlaceArmies), (player1, Attack), (player1, Fortify), ...
  // Repeats infinitely for all players
  var turnOrder: Stream[(Player, TurnPhase)] =
    Stream
      .continually(
        players.toStream.flatMap(player => Stream(
          (player, PlaceArmies),
          (player, Attack),
          (player, Fortify)
        ))
      )
      .flatten

  override def receive: Receive = {
    case StartGamePlay => {
      logger.info("Starting GamePlay Phase!")
      notifyPlayerTurn()
    }
    case PlaceArmy(token: String, territory: Int) =>
      for {
        player <- players.find(p => p.client.forall(c => c.client.token == token))
      } yield handlePlaceArmy(player, territory)
    case MoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int) =>
      handleMoveArmy(armyCount, territoryFrom, territoryTo)
  }

  def handlePlaceArmy(player: Player, territoryId: Int): Unit = {
    turnOrder match {
      // Verify that only the player whose turn it is can place armies
      case (expectedPlayer, PlaceArmies) #:: nextTurns if expectedPlayer == player =>
        // Get the territory the user clicked on
        val territory = game.state.map.territories(territoryId)

        // If the territory is unclaimed or claimed by this player, this is a valid move
        if (territory.ownerToken == player.client.get.client.publicToken || territory.ownerToken == "") {
          territory.ownerToken = player.client.get.client.publicToken
          territory.armies += 1
          player.unitCount -= 1

          turnOrder = nextTurns

          notifyGameState()
          notifyPlayerTurn()
        }
      case (expectedPlayer, Attack)  #:: nextTurns if expectedPlayer == player =>
        logger.info("Attack")
      case _ =>
    }
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

  def notifyPlayerTurn(): Unit = {
    turnOrder.head match {
      case (player, phase) =>
        val playerToken = player.client.get.client.publicToken

        phase match {
          case PlaceArmies => notifyNewArmies(player)
        }

        game.players foreach (player => player.client.get.actor ! NotifyTurn(playerToken, phase))
    }
  }

  def notifyGameState(): Unit = {
    game.players foreach (player => player.client.get.actor ! NotifyGameState(game.state))
  }
}
