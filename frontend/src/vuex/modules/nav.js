const types = {
  SET_NAVBAR_TITLE: 'SET_NAVBAR_TITLE'
}
const state = {
  title: 'RISC!'
}
const mutations = {
  [types.SET_NAVBAR_TITLE] (state, title) {
    state.title = title
  }
}
const getters = {
  navTitle (state) {
    return state.title
  }
}
const actions = {}

export default {
  state,
  getters,
  actions,
  mutations
}
