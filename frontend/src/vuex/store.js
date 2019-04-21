import Vue from 'vue'
import Vuex from 'vuex'

import * as plugins from './plugins'
import modules from './modules'

Vue.use(Vuex)

export default new Vuex.Store({
  modules,
  plugins: [plugins.authTokenPlugin],
  strict: process.env.NODE_ENV !== 'production'
})
