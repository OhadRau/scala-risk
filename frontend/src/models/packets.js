export class Token {
  constructor (other) {
    this._type = 'actors.Token'
    this.token = other.token
  }
}

export class Ping {
  constructor (other) {
    this._type = 'actors.Ping'
    this.msg = other.msg
  }
}

export class Pong {
  constructor (token) {
    this.token = token
    this._type = 'actors.Pong'
  }
}

export function processMessage (store, socket, message) {
  switch (message._type) {
    case 'actors.Token':
      const packet = new Token(message)
      store.commit('GAME_SET_TOKEN', packet)
      break
    case 'actors.Ping':
      socket.sendObj(new Pong(store.state.game.token))
  }
}
