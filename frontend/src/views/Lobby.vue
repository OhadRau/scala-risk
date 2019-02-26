<template>
  <v-layout row wrap>
    <v-flex xs12 sm6 md4 lg3 xl2>
      <RoomList/>
    </v-flex>
    <v-flex xs12 sm6 md4 lg4 xl2 v-if="true || joinedRoom !== null">
      <PlayerList/>
    </v-flex>
  </v-layout>
</template>

<script>

import RoomList from '@/components/Lobby/RoomList'
import PlayerList from '../components/Lobby/PlayerList'
export default {
  name: 'Lobby',
  computed: {
    joinedRoom () {
      return this.$store.state.game.joinedRoom
    }
  },
  components: {PlayerList, RoomList}
}
</script>

<style scoped>

</style>
