import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './vuex/store'
import Vuetify from 'vuetify'
import colors from 'vuetify/es5/util/colors'
import VueNativeSock from 'vue-native-websocket'
import 'vuetify/dist/vuetify.min.css'
import 'material-design-icons-iconfont/dist/material-design-icons.css'
// Notification Setup
// Include mini-toaster (or any other UI-notification library)
import VueToastr from '@deveodk/vue-toastr'
import '@deveodk/vue-toastr/dist/@deveodk/vue-toastr.css'

Vue.use(VueToastr, {
  defaultPosition: 'toast-top-right',
  defaultType: 'info',
  defaultTimeout: 9999
})
Vue.use(VueNativeSock, 'ws://localhost:9000/ws', {store, format: 'json'})
Vue.use(Vuetify, {
  theme: {
    primary: colors.indigo.darken2, // #E53935
    secondary: colors.indigo.lighten2, // #FFCDD2
    accent: colors.indigo.base // #3F51B5
  }
})

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
