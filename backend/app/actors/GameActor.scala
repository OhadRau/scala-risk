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
      logger.info(game.getPlayerByToken(msg.token).toString)
      game.getPlayerByToken(msg.token) match {
        case Some(player) =>
          implicit val implicitPlayer: Player = player
          msg match {
            case Turns(_: String, _: String, playerName: String, territory: Int) => handleTurns(playerName, territory)
            case PlaceArmy(_: String, _: String, playerName: String, territory: Int) => handlePlaceArmy(playerName, territory)
            case MoveArmy(_: String, _: String, armyCount: Int, territoryFrom: Int, territoryTo: Int) => handleMoveArmy(armyCount, territoryFrom, territoryTo)
          }
        case None => sender() ! Err("Invalid gameId")
      }
    case msg => logger.warn(s"GameActor received a message that wasn't a GameMsg: ${msg.toString}")
  }

  def handleTurns(playerName: String, territory: Int): Unit = {
    val currTurn = game.state.players(0).name
    logger.info(s"$currTurn's TURN")
    if (currTurn == playerName && territory != -1) {
      if (game.state.map.territories(territory).ownerToken == playerName || game.state.map.territories(territory).ownerToken == "") {
        val playerObj = game.state.players.find(_.name == playerName)
        var numSkipped = 0
        if (playerObj.get.unitCount > 0) {
          handlePlaceArmy(playerName, territory)
          game.state.players = game.players.drop(1) ++ game.players.take(1)
          logger.info(game.state.players.toString())
        } else {
          while (playerObj.get.unitCount == 0 || numSkipped == game.state.players.length) {
            numSkipped += 1
            game.state.players = game.players.drop(1) ++ game.players.take(1)
            if (numSkipped == game.state.players.length) {
              //initialization done
            }
          }
        }
      }
    }
    notifyGameState()
    //    if (this.initialization) {
//      console.log('displaysName: ' + this.$store.state.game.displayName.name)
//      console.log('playerOrderName: ' + this.playerOrder[this.initTurn % this.playerOrder.length].name)
//      if (this.$store.state.game.displayName.name ===
//        this.playerOrder[this.initTurn % this.playerOrder.length].name) {
//        if (this.$store.state.game.game.territories[id].ownerToken === '' || this.$store.state.game.game.territories[id].ownerToken === this.$store.state.game.displayName) {
//          this.selected = id
//          console.log(this.$store.state.game.displayName)
//          this.$socket.sendObj(new PlaceArmy(this.$store.state.game, this.$store.state.game.displayName.token, this.$store.state.game.displayName.name, id))
//          var check = 0
//          this.initTurn += 1
//          while (this.playerOrder[this.initTurn % this.playerOrder.length].armies === 0) {
//            this.initTurn += 1
//            check += 0
//            if (check === this.playerOrder.length) {
//              this.initialization = false
//            }
//          }
//        }
//      }
//    }
  }
  def handlePlaceArmy(playerName: String, territory: Int): Unit = {
    //this.playerOrder[this.initTurn % this.playerOrder.length].armies -= 1
    //this.$store.state.game.game.territories[id].armies += 1
    game.state.map.territories(territory).ownerToken = playerName
    game.state.map.territories(territory).armies += 1
    logger.info(game.state.players.find(_.name == playerName).toString)
    var playerObj = game.state.players.find(_.name == playerName)
    playerObj.get.unitCount -= 1
    notifyGameState()
  }

  def handleMoveArmy(armyCount: Int, territoryFrom: Int, territoryTo: Int): Unit = {
    notifyGameState()
  }

  def notifyGameState(): Unit = {
    game.players foreach(player => player.client.get.actor ! NotifyGameState(game.state))
  }
}
