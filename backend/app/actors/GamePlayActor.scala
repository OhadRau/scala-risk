package actors

import akka.actor.{Actor, Props}
import models.{Game, Player}
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
    case StartGamePlay => notifyPlayerTurn()
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
        if (territory.ownerToken == player.client.get.client.token || territory.ownerToken == "") {
          territory.ownerToken = player.client.get.client.token
          territory.armies += 1
          player.unitCount -= 1

          turnOrder = nextTurns

          notifyGameState()
          notifyPlayerTurn()
        }
      case _ => ()
    }
  }

  def handleMoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int): Unit = {
    notifyGameState()
  }

  def notifyPlayerTurn(): Unit = {
    turnOrder.head match {
      case (player, phase) =>
        val playerToken = player.client.get.client.publicToken
        game.players foreach (player => player.client.get.actor ! NotifyTurnPhase(playerToken, phase))
    }
  }

  def notifyGameState(): Unit = {
    game.players foreach (player => player.client.get.actor ! NotifyGameState(game.state))
  }
}
