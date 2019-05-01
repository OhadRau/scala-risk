import {ListRoom, CheckName, AssignName, CreateRoom} from '@/models/packets'

export const types = {
  SET_TOKEN: 'GAME_SET_TOKEN',
  GAME_SEND_AUTHENTICATE: 'GAME_SEND_AUTHENTICATE',
  GAME_ROOMS_CHANGED: 'GAME_ROOMS_CHANGED',
  SET_GAME_NAME: 'SET_GAME_NAME',
  VALIDATE_GAME_NAME: 'VALIDATE_GAME_NAME',
  COMMIT_GAME_NAME: 'COMMIT_GAME_NAME',
  GAME_RESUME: 'GAME_RESUME',
  GAME_ROOM_LEAVE: 'GAME_ROOM_LEAVE',
  GAME_ROOM_CREATION_RESULT_OCCURRED: 'GAME_ROOM_CREATION_RESULT_OCCURRED',
  GAME_ROOM_CREATED: 'GAME_ROOM_CREATED',
  GAME_ROOM_JOIN: 'GAME_ROOM_JOIN',
  GAME_ROOM_STATUS_CHANGED: 'GAME_ROOM_STATUS_CHANGED',
  GAME_PLAYER_LIST_CHANGED: 'GAME_PLAYER_LIST_CHANGED',
  GAME_STARTED: 'GAME_STARTED',
  GAME_STATE: 'GAME_STATE',
  MAP_RESOURCE: 'MAP_RESOURCE',
  NOTIFY_TURN: 'NOTIFY_TURN',
  NOTIFY_GAME_PHASE_START: 'NOTIFY_GAME_PHASE_START',
  NOTIFY_GAME_END: 'NOTIFY_GAME_END'
}
const state = {
  token: null,
  attemptedAuthentication: false,
  publicToken: null,
  displayName: {
    name: '',
    valid: null,
    committed: false
  },
  joinedRoom: {
    name: '',
    roomId: null,
    hostToken: '',
    playing: false,
    clientStatus: []
  },
  rooms: [],
  players: [{
    name: null,
    publicToken: null
  }],
  game: {
    started: false,
    map: {
      viewBox: null,
      style: null,
      names: [],
      territories: [],
      labels: [],
      labelPaths: []
    },
    phase: 'Setup',
    players: [],
    territories: [{
      armies: 0,
      ownerToken: null,
      neighbours: [],
      id: 0
    }]
  },
  turn: null,
  turnPhase: null,
  winner: null
}
const mutations = {
  [types.GAME_SEND_AUTHENTICATE] (state) {
    state.attemptedAuthentication = true
  },
  [types.SET_TOKEN] (state, token) {
    state.token = token.token
    state.publicToken = token.publicToken
  },
  [types.GAME_ROOM_LEAVE] (state, payload) {
    if (payload.roomLeft) {
      state.joinedRoom = {
        name: '',
        roomId: null,
        hostToken: '',
        clientStatus: []
      }
    }
  },
  [types.GAME_ROOMS_CHANGED] (state, roomChangePacket) {
    state.rooms = roomChangePacket.rooms
    // TODO: Implement Room Kicking
    // if (this.joinedRoom !== null &&
    //   !state.rooms.some((item) => {
    //     return item.roomId === state.joinedRoom.roomId
    //   })) {
    //   state.joinedRoom = null
    // }
  },
  [types.GAME_RESUME] (state, resumeNotification) {
    if (resumeNotification.name) {
      state.displayName.name = resumeNotification.name
    }
    if (resumeNotification.room) {
      state.joinedRoom.roomId = resumeNotification.room
    }
  },
  [types.SET_GAME_NAME] (state, name) {
    state.displayName.name = name
    if (name !== state.displayName.name) {
      state.displayName.valid = null
    }
  },
  [types.VALIDATE_GAME_NAME] (state, result) {
    state.displayName.valid = result
  },
  [types.COMMIT_GAME_NAME] (state, commitResult) {
    state.displayName.committed = commitResult.success
  },
  [types.GAME_ROOM_CREATION_RESULT_OCCURRED] (state, result) {
  },
  [types.GAME_ROOM_CREATED] (state, result) {
  },
  [types.NOTIFY_TURN] (state, turn) {
    console.log('NOTIFY_TURN COMMITED')
    state.turn = turn.publicToken
    state.turnPhase = turn.turnPhase
  },
  [types.GAME_ROOM_STATUS_CHANGED] (state, updatePacket) {
    if (state.joinedRoom.roomId === updatePacket.roomId) {
      state.joinedRoom.name = updatePacket.roomName
      state.joinedRoom.clientStatus = updatePacket.clientStatus
    }
  },
  [types.GAME_ROOM_JOIN] (state, roomJoin) {
    if (roomJoin.playerToken === state.publicToken) {
      state.joinedRoom.roomId = roomJoin.roomId
    }
  },
  [types.GAME_PLAYER_LIST_CHANGED] (state, change) {
    state.players = change.players
  },
  [types.GAME_STARTED] (state, change) {
    state.game = {
      started: true,
      map: {
        viewBox: undefined,
        style: undefined,
        names: undefined,
        territories: undefined,
        labels: undefined,
        labelPaths: undefined
      },
      phase: 'Setup',
      players: change.players,
      territories: change.map.territories
    }
  },
  [types.GAME_STATE] (state, change) {
    state.game = {
      ...state.game,
      players: change.players,
      territories: change.map.territories
    }
  },
  [types.MAP_RESOURCE] (state, change) {
    state.game = {
      ...state.game,
      map: {
        viewBox: change.viewBox,
        style: change.style,
        names: change.names,
        territories: change.territories,
        labels: change.labels,
        labelPaths: change.labelPaths
      }
    }
  },
  [types.NOTIFY_GAME_PHASE_START] (state, change) {
    state.game = {
      ...state.game,
      phase: 'Realtime'
    }
  },
  [types.NOTIFY_GAME_END] (state, change) {
    state.winner = change.winner
  }
}
const getters = {
  gameRoomRoomId (state) {
    return state.joinedRoom.roomId
  },
  gameToken (state) {
    return state.token
  },
  gamePublicToken (state) {
    return state.publicToken
  },
  gameNameValid (state) {
    return state.displayName.valid
  },
  mapResource (state) {
    return state.game.map
  },
  getTurn (state) {
    return state.turn
  },
  gamePhase (state) {
    return state.game.phase
  },
  armies: (state) => {
    return (state.game.players.find(p => p.name === state.displayName.name)) ? (state.game.players.find(p => p.name === state.displayName.name).unitCount) : 0
  },
  getTerritory: (state) => (territoryId) => {
    return state.game.territories[territoryId]
  },
  getLabel: (state) => (territoryId) => {
    return state.game.labels[territoryId]
  },
  players: (state) => {
    return state.players
  }

}
const actions = {
  gameListRoom ({commit, dispatch, state}, {socket}) {
    socket.sendObj(new ListRoom(state.token))
  },
  gameVerifyName ({commit, dispatch, state}, {name, socket}) {
    commit(types.SET_GAME_NAME, name)
    if (name.length > 0) {
      commit(types.VALIDATE_GAME_NAME, 'validating')
      socket.sendObj(new CheckName(state.token, name))
    }
  },
  gameAssignName ({commit, dispatch, state}, {socket}) {
    socket.sendObj(new AssignName(state.token, state.displayName.name))
  },
  gameCreateRoom ({commit, dispatch, state}, {socket, name}) {
    socket.sendObj(new CreateRoom(state.token, name))
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
