import {types} from '@/vuex/modules'

export class Token {
  constructor (other) {
    this.token = other.token
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
    this.clients = other.strings
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

export class CreateRoom {
  constructor (token, roomName) {
    this.token = token
    this.roomName = roomName
    this._type = 'actors.CreateRoom'
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
      store.commit(types.SET_TOKEN, packet)
      store.dispatch('gameListRoom', {socket})
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
    case 'actors.RoomCreationResult':
      store.commit(types.GAME_ROOM_CREATION_RESULT_OCCURRED, new RoomCreationResult(message))
      break
    // case 'actors.NotifyClientsChanged':
    //   const clientsChanged = new NotifyClientsChanged(message)
    //   break
    case 'actors.NameAssignResult':
      const nameAssignmentResult = new NameAssignResult(message)
      if (store.state.game.displayName.name === nameAssignmentResult.name) {
        store.commit(types.COMMIT_GAME_NAME, nameAssignmentResult)
      }
      break
    default:
      toastr('info', JSON.stringify(message), 'Un-parsed Socket Message:')
      console.log(JSON.stringify(message))
  }
}
