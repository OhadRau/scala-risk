package actors

import akka.actor.{Actor, Props}
import models.{Game, Player}

object GameSetupActor {
  def props(players: Seq[Player], game: Game): Props = Props(new GameSetupActor(players, game))
}

class GameSetupActor(players: Seq[Player], game: Game) extends Actor {
  val logger = play.api.Logger(getClass)

  game.players foreach (player => player.client.get.actor ! NotifyGameStarted(game.state))
  game.players foreach (player => player.client.get.actor ! SendMapResource(game.state.map.resource))

  // Each player gets N starting units based on Game.armyAllotmentSize
  var placeArmyOrder: Stream[Player] =
    Stream
      .continually(players.toStream)
      .flatten
      .take(game.state.players.size * game.armyAllotmentSize)
  logger.info(s"Number of army placement turns: ${placeArmyOrder.size}")
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
    placeArmyOrder match {
      // Verify that only the player whose turn it is can place armies
      case expectedPlayer #:: nextTurns if expectedPlayer == player =>
        // Get the territory the user clicked on
        val territory = game.state.map.territories(territoryId)

        // If the territory is unclaimed or claimed by this player, this is a valid move
        if (territory.ownerToken == player.client.get.client.publicToken || territory.ownerToken == "") {
          territory.ownerToken = player.client.get.client.publicToken
          territory.armies += 1

          player.unitCount -= 1

          placeArmyOrder = nextTurns

          notifyGameState()
          notifyPlayerTurn()
        }
      case Stream.Empty =>
        // No more armies left to place, so start the main game
        startGamePlay()
    }
  }

  def handleMoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int): Unit = {
    notifyGameState()
  }

  def startGamePlay(): Unit = {
    game.state.gamePhase = models.Play
    game.players foreach (player => player.client.get.actor ! NotifyGamePhaseStart(game.state))
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
