<template>
  <v-list>
    <div v-if="players.length">
      <v-list-tile
        v-for="player in players"
        :key="player.publicToken"
        @click="() => {onClickPlayer(player.publicToken)}"
      >
        <v-list-tile-content>
          <v-list-tile-title v-text="player.name"></v-list-tile-title>
        </v-list-tile-content>
        <v-list-tile-action>
          <v-list-tile-action-text v-if="player.publicToken === keyToken">
            <v-chip color="warning">{{keyValue}}</v-chip>
          </v-list-tile-action-text>
          <v-list-tile-action-text v-else>
            <v-chip color="success" v-if="player.status.status === 'Ready'">Ready</v-chip>
            <v-chip color="red" v-else>Waiting</v-chip>
          </v-list-tile-action-text>
        </v-list-tile-action>
      </v-list-tile>
      <v-divider></v-divider>
      <v-subheader>{{players.length}} players(s) found</v-subheader>
    </div>
    <v-list-tile v-else>
      <v-list-tile-content>How can you be in an empty room?</v-list-tile-content>
    </v-list-tile>
  </v-list>
</template>

<script>
export default {
  name: 'RoomPlayerListing',
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
