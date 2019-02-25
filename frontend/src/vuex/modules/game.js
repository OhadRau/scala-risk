// import { Token } from '@/models/packets'

const types = {
  SET_TOKEN: 'GAME_SET_TOKEN'
}
const state = {
  token: null
}
const mutations = {
  [ types.SET_TOKEN ] (state, token) {
    state.token = token.token
  }
}
const getters = {
  gameToken (state) {
    return state.token
  }
}
const actions = {}

export default {
  state,
  getters,
  actions,
  mutations
}
