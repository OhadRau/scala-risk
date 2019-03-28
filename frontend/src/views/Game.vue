<template>
  <v-layout column>
    <v-flex xs8 d-flex>
      <svg width="100%" viewBox="0 0 2000 700" @click="territoryClicked(-1)">
        <svg :viewBox="mapResource.viewBox">
          <g v-for="(territory, index) in mapResource.territories" :key="index"
             v-html="renderTerritory(territory, index)"
             @click.stop="territoryClicked(index)" :class="{clicked:selected === index}">
          </g>
        </svg>
      </svg>
    </v-flex>
    <v-flex xs4 d-flex>
      <v-layout row fill-height>
        <v-flex sm0 md3 pa-1>
          <v-card height="100%">
            <v-card-text>
              Map
            </v-card-text>
          </v-card>
        </v-flex>
        <v-flex sm12 md9 pa-1>
          <v-card height="100%">
            <v-card-title>
              {{gamePhase}}
            </v-card-title>
            <v-card-text>
              It is currently {{getTurn === gamePublicToken ? "" : "not "}} your turn.
              You have {{armyCount}} armies remaining.
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>
    </v-flex>
  </v-layout>
</template>

<script>
import {mapGetters} from 'vuex'
import {PlaceArmy} from '@/models/packets'

export default {
  name: 'Game',
  beforeMount () {
    if (this.$store.state.game.game.players.length === 0) {
      this.$router.replace({name: 'home'})
    }
  },
  data () {
    return {
      selected: -1
    }
  },
  computed: {
    ...mapGetters(['mapResource', 'getTurn', 'gamePublicToken', 'gamePhase']),
    armyCount () {
      const name = this.getClientName
      const playersWithCount = this.$store.state.game.game.players
      let count = 0
      for (var i = 0; i < playersWithCount.length; i++) {
        if (playersWithCount[i].name === name) {
          count = playersWithCount[i].unitCount
        }
      }
      return count
    },
    getClientName () {
      var name = ''
      var playersWithToken = this.$store.state.game.players
      for (var i = 0; i < playersWithToken.length; i++) {
        if (playersWithToken[i].publicToken === this.gamePublicToken) {
          name = playersWithToken[i].name
        }
      }
      return name
    }
  },
  methods: {
    territoryClicked (id) {
      this.selected = id
      if (id !== -1) {
        if (this.gamePhase === 'Setup') {
          if (this.getTurn === this.gamePublicToken) {
            if (this.$store.state.game.game.territories[id].ownerToken === this.gamePublicToken || this.$store.state.game.game.territories[id].ownerToken === '') {
              this.$socket.sendObj(new PlaceArmy(this.$store.state.game.token, this.$store.state.game.joinedRoom.roomId, id))
            } else {
              this.$toastr('warning', 'Cannot place army', 'This is not your territory')
            }
          } else {
            this.$toastr('warning', 'Cannot place army', 'This is not your turn')
          }
        }
      }
    },
    renderTerritory (territory, index) {
      var htmlObject = document.createElement('div')
      htmlObject.innerHTML = territory
      htmlObject.getElementsByTagName('tspan')['0'].innerHTML = this.$store.state.game.game.territories[index].armies
      return htmlObject.firstChild.outerHTML
    }
  },
  mounted () {
    this.$store.watch(
      (state) => state.game.turn,
      (newValue, oldValue) => {
        console.log(`gamePhase: ${this.gamePhase}, getTurn: ${this.getTurn}, newValue: ${newValue}, oldValue: ${oldValue}, gametoken: ${this.gamePublicToken}`)
        console.log(`${this.gamePhase === 'Setup'}, ${this.getTurn === this.gamePublicToken}`)
        if (this.gamePhase === 'Setup' && newValue !== null && newValue === this.gamePublicToken) {
          this.$toastr('info', '', 'It is your turn to place an army')
        }
      }
    )
  }
}
// created () {
//   this.$store.subscribe((mutation, state) => {
//     if (mutation.type === types.NOTIFY_TURN) {
//       console.log(`state.phase: ${state.game.game.phase}, turn: ${state.game.turn}, publicToken: ${state.game.publicToken}`)
//       console.log(`state.phase === Setup: ${state.game.game.phase === 'Setup'}, turn === publicToken: ${state.game.turn === state.game.publicToken}`)
//       if (state.game.game.phase === 'Setup' && state.game.turn === state.game.publicToken) {
//         this.$toastr('info', '', 'It is your turn to place an army')
//       }
//     }
//   })
// }
</script>

<style scoped>
  svg {
    overflow: hidden;
  }

  svg > g:hover {
    stroke: white;
    stroke-width: 0.5;
  }

  .clicked, .clicked:hover {
    stroke: white;
    stroke-width: 0.75;
  }
</style>
