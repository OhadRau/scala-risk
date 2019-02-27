<template>
  <v-list>
    <div v-if="players.length">
      <v-list-tile
        v-for="player in players"
        :key="player.publicToken"
        @click="() => {onClickPlayer(player.publicToken)}"
      >
        <!--TODO: Click Handler-->
        <v-list-tile-content>
          <v-list-tile-title v-text="player.name"></v-list-tile-title>
          <v-list-tile-sub-title v-if="player.publicToken === keyToken">{{keyValue}}</v-list-tile-sub-title>
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
    },
    keyToken: {
      type: String,
      default () { return null }
    },
    onClickPlayer: {
      type: Function,
      default () { return () => {} }
    },
    keyValue: {
      type: String,
      default () { return '(Me)' }
    }
  }
}
</script>

<style scoped>

</style>
