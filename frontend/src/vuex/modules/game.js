import {ListRoom, CheckName, AssignName, CreateRoom} from '@/models/packets'

export const types = {
  SET_TOKEN: 'GAME_SET_TOKEN',
  GAME_ROOMS_CHANGED: 'GAME_ROOMS_CHANGED',
  SET_GAME_NAME: 'SET_GAME_NAME',
  VALIDATE_GAME_NAME: 'VALIDATE_GAME_NAME',
  COMMIT_GAME_NAME: 'COMMIT_GAME_NAME',
  GAME_ROOM_CREATION_RESULT_OCCURRED: 'GAME_ROOM_CREATION_RESULT_OCCURRED',
  GAME_ROOM_CREATED: 'GAME_ROOM_CREATED',
  GAME_ROOM_JOIN: 'GAME_ROOM_JOIN'
}
const state = {
  token: null,
  displayName: {
    name: '',
    valid: null,
    committed: false
  },
  joinedRoom: null,
  rooms: [],
  players: []
}
const mutations = {
  [types.SET_TOKEN] (state, token) {
    state.token = token.token
  },
  [types.GAME_ROOMS_CHANGED] (state, roomChangePacket) {
    state.rooms = roomChangePacket.rooms
    if (this.joinedRoom !== null &&
      !state.rooms.some((item) => {
        return item.roomId === state.joinedRoom
      })) {
      state.joinedRoom = null
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
  [types.GAME_ROOM_JOIN] (state, roomJoin) {
    state.joinedRoom = roomJoin.roomId
  }
}
const getters = {
  gameToken (state) {
    return state.token
  },
  gameNameValid (state) {
    return state.displayName.valid
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
