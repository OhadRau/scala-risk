<template>
  <v-layout row wrap>
    <v-flex xs12 sm6 md4>
      <RoomList/>
    </v-flex>
  </v-layout>
</template>

<script>

import RoomList from '@/components/Lobby/RoomList'
export default {
  name: 'Lobby',
  components: {RoomList}
}
</script>

<style scoped>

</style>
