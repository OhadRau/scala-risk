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

export class NameCheckResult {
  constructor (other) {
    this.available = other.available
    this.name = other.name
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
      store.commit('GAME_SET_TOKEN', packet)
      store.dispatch('gameListRoom', {socket})
      break
    case 'actors.Ping':
      socket.sendObj(new Pong(store.state.game.token))
      break
    case 'actors.NotifyRoomsChanged':
      const roomsChanged = new NotifyRoomsChanged(message)
      store.commit('GAME_ROOMS_CHANGED', roomsChanged)
      break
    case 'actors.NameCheckResult':
      const nameCheckResult = new NameCheckResult(message)
      if (store.state.game.displayName.name === nameCheckResult.name) {
        store.commit('VALIDATE_GAME_NAME', nameCheckResult.available ? 'true' : 'false')
      }
      break
    default:
      toastr('info', JSON.stringify(message), 'Un-parsed Socket Message:')
  }
}
