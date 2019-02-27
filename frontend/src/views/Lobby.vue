<template>
  <v-layout row wrap>
    <v-flex xs12 sm6 md4 lg3 xl2 pa-1>
      <RoomList/>
    </v-flex>
    <v-flex  xs12 sm6 md4 lg4 xl2 pa-1 v-if="joinedRoom.roomId !== null">
      <v-card>
        <v-toolbar>
          <v-toolbar-title>Players in Room: {{joinedRoom.name}}</v-toolbar-title>
          <v-spacer/>
        </v-toolbar>
        <PlayerList :players="currentRoomPlayerList" :key-token="gamePublicToken"/>
        <v-card-actions>
          <v-spacer/>
          <v-btn color="primary" v-if="showStartGame" :disabled="roomReady">Start Game</v-btn>
          <v-btn color="primary" v-if="!showStartGame">Ready</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
    <v-flex xs12 sm6 md4 lg4 xl2 pa-1>
      <v-card>
        <v-toolbar>
          <v-toolbar-title>Players</v-toolbar-title>
          <v-spacer/>
        </v-toolbar>
        <PlayerList :players="playerList" :key-token="gamePublicToken"/>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>

import RoomList from '@/components/Lobby/RoomList'
import PlayerList from '../components/Lobby/PlayerList'
import {mapGetters} from 'vuex'

export default {
  name: 'Lobby',
  beforeMount () {
    if (this.$store.state.game.displayName.name === null || this.$store.state.game.displayName.name.length === 0) {
      this.$router.replace({name: 'home'})
    }
  },
  methods: {
  },
  computed: {
    ...mapGetters([ 'gamePublicToken' ]),
    showStartGame () {
      // TODO: Optimization with object
      const currentRoom = this.$store.state.game.rooms.find((item) => { return this.joinedRoom.roomId === item.roomId })
      return currentRoom !== undefined && currentRoom.hostToken === this.$store.state.game.publicToken
    },
    roomReady () {
      return this.currentRoomPlayerList.length >= 3
    },
    joinedRoom () {
      return this.$store.state.game.joinedRoom
    },
    currentRoomPlayerList () {
      return this.joinedRoom.clientStatus
    },
    playerList () {
      return this.$store.state.game.players
    }
  },
  components: {PlayerList, RoomList}
}
</script>

<style scoped>

</style>
