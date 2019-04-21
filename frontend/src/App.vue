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
import {types} from '@/vuex/modules'
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
      } else if (mutation.type === 'SOCKET_ONCLOSE') {
        this.$toastr('error', 'Remote closed socket unexpectedly')
      } else if (mutation.type === 'SOCKET_ONMESSAGE') {
        processMessage(this.$store, this.$socket, this.$toastr, mutation.payload)
      } else if (mutation.type === types.GAME_RESUME) {
        const notify = mutation.payload
        if (notify.name && !notify.game) {
          this.$router.replace({name: 'Lobby'})
        }
      } else if (mutation.type === types.GAME_STARTED) {
        this.$router.replace({name: 'Game'})
      }
    })
  }
}
</script>
