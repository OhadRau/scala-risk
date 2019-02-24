const types = {
  SOCKET_ONOPEN: 'SOCKET_ONOPEN',
  SOCKET_ONCLOSE: 'SOCKET_ONCLOSE',
  SOCKET_ONERROR: 'SOCKET_ONERROR',
  SOCKET_ONMESSAGE: 'SOCKET_ONMESSAGE',
  SOCKET_RECONNECT: 'SOCKET_RECONNECT',
  SOCKET_RECONNECT_ERROR: 'SOCKET_RECONNECT_ERROR'
}
const state = {
  isConnected: false,
  message: '',
  reconnectError: false
}
const mutations = {
  [types.SOCKET_ONOPEN] (state, event) {
    this.$socket = event.currentTarget
    state.isConnected = true
  },
  [types.SOCKET_ONCLOSE] (state, event) {
    state.isConnected = false
  },
  SOCKET_ONMESSAGE (state, message) {
    state.message = message
  }
}
const getters = {}
const actions = {}

export default {
  state,
  getters,
  actions,
  mutations
}
