import {types} from '@/vuex/modules'

export class Token {
  constructor (other) {
    this.token = other.token
    this.publicToken = other.publicToken
  }
}

export class Ping {
  constructor (other) {
    this.msg = other.msg
  }
}

export class ListRoom {
  constructor (token) {
    this.token = token
    this._type = 'actors.ListRoom'
  }
}

export class NotifyRoomsChanged {
  constructor (other) {
    this.rooms = other.rooms
  }
}

export class NotifyClientsChanged {
  constructor (other) {
    this.players = other.strings
  }
}

export class RoomStatusUpdate {
  constructor (other) {
    this.roomName = other.roomStatus.name
    this.roomId = other.roomStatus.roomId
    this.clientStatus = other.roomStatus.clientStatus
    this.playing = other.roomStatus.playing
  }
}

export class NotifyClientResumeStatus {
  constructor (other) {
    this.game = other.game === '' ? null : other.game
    this.name = other.name === '' ? null : other.name
    this.room = other.room === '' ? null : other.room
  }
}

export class NameAssignResult {
  constructor (other) {
    this.success = other.success
    this.name = other.name
    this.message = other.message
  }
}

export class NameCheckResult {
  constructor (other) {
    this.available = other.available
    this.name = other.name
  }
}

export class RoomCreationResult {
  constructor (other) {
    this.success = other.success
    this.message = other.message
  }
}

export class CreatedRoom {
  constructor (other) {
    this.token = other.token
  }
}

export class JoinedRoom {
  constructor (other) {
    this.roomId = other.token
    this.playerToken = other.playerToken
  }
}

export class GameState {
  constructor (other) {
    this.players = other.state.players
    this.map = other.state.map
  }
}

export class MapResource {
  constructor (other) {
    console.log(JSON.parse(JSON.stringify(other)))
    this.viewBox = other.resource.viewBox
    this.names = other.resource.names
    this.territories = other.resource.territories
    this.labels = other.resource.labels
    this.labelPaths = other.resource.labelPaths
  }
}

export class NotifyTurn {
  constructor (other) {
    console.log('NOTIFY_TURN CONSTRUCTED')
    console.log(JSON.parse(JSON.stringify(other)))
    this.publicToken = other.publicToken
    this.turnPhase = other.turnPhase
  }
}

export class NotifyGamePhaseStart {
  constructor (message) {
    this.phase = message.state.gamePhase
  }
}

export class NotifyGameEnd {
  constructor (other) {
    this.winner = other.winnerToken
  }
}

export class PlaceArmy {
  constructor (token, gameId, territoryId) {
    this.token = token
    this.gameId = gameId
    this.msg = {
      _type: 'actors.PlaceArmy',
      token: token,
      territoryId: territoryId
    }
    this._type = 'actors.ForwardToGame'
  }
}

export class AttackTerritory {
  constructor (token, gameId, fromTerritoryId, toTerritoryId, armyCount) {
    this.token = token
    this.gameId = gameId
    this.msg = {
      _type: 'actors.AttackTerritory',
      token: token,
      fromTerritoryId: fromTerritoryId,
      toTerritoryId: toTerritoryId,
      armyCount: armyCount
    }
    this._type = 'actors.ForwardToGame'
  }
}

export class MoveArmy {
  constructor (token, gameId, fromTerritoryId, toTerritoryId, armyCount) {
    this.token = token
    this.gameId = gameId
    this.msg = {
      _type: 'actors.MoveArmy',
      token: token,
      fromTerritoryId: fromTerritoryId,
      toTerritoryId: toTerritoryId,
      armyCount: armyCount
    }
    this._type = 'actors.ForwardToGame'
  }
}

export class Err {
  constructor (other) {
    this.message = other.msg
  }
}

export class NotifyRoomLeaveStatus {
  constructor (other) {
    this.roomLeft = other.roomLeft
  }
}

export class LeaveRoom {
  constructor (token, roomId) {
    this.token = token
    this.roomId = roomId
    this._type = 'actors.LeaveRoom'
  }
}

export class PlayAgain {
  constructor (token, roomId) {
    this.token = token
    this.roomId = roomId
    this._type = 'actors.PlayAgain'
  }
}

export class SetToken {
  constructor (oldToken, token) {
    this.oldToken = oldToken
    this.token = token
    this._type = 'actors.SetToken'
  }
}

export class ClientReady {
  constructor (token, roomId, ready = true) {
    this.token = token
    this.roomId = roomId
    this.ready = ready
    this._type = 'actors.ClientReady'
  }
}

export class StartGame {
  constructor (token, roomId) {
    this.token = token
    this.roomId = roomId
    this._type = 'actors.StartGame'
  }
}

export class CreateRoom {
  constructor (token, roomName) {
    this.token = token
    this.roomName = roomName
    this._type = 'actors.CreateRoom'
  }
}

export class JoinRoom {
  constructor (token, roomId) {
    this.token = token
    this.roomId = roomId
    this._type = 'actors.JoinRoom'
  }
}

export class CheckName {
  constructor (token, name) {
    this.token = token
    this.name = name
    this._type = 'actors.CheckName'
  }
}

export class AssignName {
  constructor (token, name) {
    this.token = token
    this.name = name
    this._type = 'actors.AssignName'
  }
}

export class Pong {
  constructor (token) {
    this.token = token
    this._type = 'actors.Pong'
  }
}

export function processMessage (store, socket, toastr, message) {
  switch (message._type) {
    case 'actors.Token':
      const packet = new Token(message)
      if (!store.state.game.attemptedAuthentication) {
        store.commit(types.GAME_SEND_AUTHENTICATE)
        const oldToken = store.state.game.token
        socket.sendObj(new SetToken(oldToken || '', packet.token))
      }
      store.commit(types.SET_TOKEN, packet)
      break
    case 'actors.Ping':
      socket.sendObj(new Pong(store.state.game.token))
      break
    case 'actors.NotifyRoomsChanged':
      const roomsChanged = new NotifyRoomsChanged(message)
      store.commit(types.GAME_ROOMS_CHANGED, roomsChanged)
      break
    case 'actors.NameCheckResult':
      const nameCheckResult = new NameCheckResult(message)
      if (store.state.game.displayName.name === nameCheckResult.name) {
        store.commit(types.VALIDATE_GAME_NAME, nameCheckResult.available ? 'true' : 'false')
      }
      break
    case 'actors.CreatedRoom':
      store.commit(types.GAME_ROOM_CREATED, new CreatedRoom(message))
      break
    case 'actors.JoinedRoom':
      store.commit(types.GAME_ROOM_JOIN, new JoinedRoom(message))
      break
    case 'actors.RoomCreationResult':
      store.commit(types.GAME_ROOM_CREATION_RESULT_OCCURRED, new RoomCreationResult(message))
      break
    case 'actors.NotifyClientsChanged':
      store.commit(types.GAME_PLAYER_LIST_CHANGED, new NotifyClientsChanged(message))
      break
    case 'actors.NotifyRoomStatus':
      store.commit(types.GAME_ROOM_STATUS_CHANGED, new RoomStatusUpdate(message))
      break
    case 'actors.NameAssignResult':
      const nameAssignmentResult = new NameAssignResult(message)
      if (store.state.game.displayName.name === nameAssignmentResult.name) {
        store.dispatch('gameListRoom', {socket})
        store.commit(types.COMMIT_GAME_NAME, nameAssignmentResult)
      }
      break
    case 'actors.NotifyGameStarted':
      store.commit(types.GAME_STARTED, new GameState(message))
      break
    case 'actors.NotifyGameState':
      store.commit(types.GAME_STATE, new GameState(message))
      break
    case 'actors.NotifyGamePhaseStart':
      store.commit(types.NOTIFY_GAME_PHASE_START, new NotifyGamePhaseStart(message))
      break
    case 'actors.SendMapResource':
      store.commit(types.MAP_RESOURCE, new MapResource(message))
      break
    case 'actors.NotifyTurn':
      store.commit(types.NOTIFY_TURN, new NotifyTurn(message))
      break
    case 'actors.NotifyNewArmies':
      toastr('info', '', 'You got ' + message.newArmies + ' new armies.')
      break
    case 'actors.Err':
      toastr('error', new Err(message).message, 'Error from Server')
      break
    case 'actors.NotifyClientResumeStatus':
      store.commit(types.GAME_RESUME, new NotifyClientResumeStatus(message))
      break
    case 'actors.NotifyRoomLeaveStatus':
      store.commit(types.GAME_ROOM_LEAVE, new NotifyRoomLeaveStatus(message))
      break
    case 'actors.NotifyGameEnd':
      store.commit(types.NOTIFY_GAME_END, new NotifyGameEnd(message))
      break
    default:
      toastr('info', JSON.stringify(message), 'Un-parsed Socket Message:')
      console.log(JSON.stringify(message))
  }
}
