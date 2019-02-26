<template>
  <v-layout row wrap>
    <v-flex xs12 sm6 md4 lg3 xl2>
      <RoomList/>
    </v-flex>
    <v-flex xs12 sm6 md4 lg4 xl2 v-if="joinedRoom !== null">

    </v-flex>
  </v-layout>
</template>

<script>

import RoomList from '@/components/Lobby/RoomList'
export default {
  name: 'Lobby',
  computed: {
    joinedRoom () {
      return this.$store.state.game.joinedRoom
    }
  },
  components: {RoomList}
}
</script>

<style scoped>

</style>
