<template>
  <v-list>
    <div v-if="players.length">
      <v-list-tile
        v-for="player in players"
        :key="player.id"
        @click=""
      >
        <!--TODO: Click Handler-->
        <v-list-tile-content>
          <v-list-tile-title v-text="player.name"></v-list-tile-title>
        </v-list-tile-content>
      </v-list-tile>
      <v-divider></v-divider>
      <v-subheader>{{players.length}} players(s) found</v-subheader>
    </div>
    <v-list-tile v-else>
      <v-list-tile-content>No Rooms Yet. Create One!</v-list-tile-content>
    </v-list-tile>
  </v-list>
</template>

<script>
export default {
  name: 'PlayerList',
  props: {
    players: {
      type: Array,
      default () { return [] }
    }
  }
}
</script>

<style scoped>

</style>
