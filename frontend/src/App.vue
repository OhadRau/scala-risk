<template>
  <v-app dark>
    <v-toolbar app>
      <v-toolbar-title>{{navTitle}}</v-toolbar-title>
    </v-toolbar>
    <v-content>
      <v-container fluid fill-height>
        <router-view></router-view>
      </v-container>
    </v-content>
  </v-app>
</template>

<style lang="scss">
</style>

<script>
import {mapGetters} from 'vuex'
import {processMessage} from './models/packets'

export default {
  name: 'AppRoot',
  computed: {
    ...mapGetters([
      'navTitle'
    ])
  },
  methods: {},
  created () {
    this.$store.subscribe((mutation, state) => {
      if (mutation.type === 'SOCKET_ONOPEN') {
        this.$toastr('success', 'Connected to Server!')
      } else if (mutation.type === 'SOCKET_ONMESSAGE') {
        processMessage(this.$store, this.$socket, this.$toastr, mutation.payload)
      }
    })
  }
}
</script>
