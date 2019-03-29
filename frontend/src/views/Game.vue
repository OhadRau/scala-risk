<template>
  <v-layout column>
    <v-snackbar :value="getTurn === gamePublicToken && gamePhase === 'Setup'" top :timeout="0">
      <div class="headline pa-2">It is your turn to place an army.</div>
    </v-snackbar>
    <v-flex xs8 d-flex>
      <svg width="100%" viewBox="0 0 2000 700" @click="territoryClicked(-1)">
        <svg :viewBox="mapResource.viewBox">
          <g v-for="(territory, index) in mapResource.territories" :key="index"
             v-html="renderTerritory(territory, index)"
             @click.stop="territoryClicked(index)" :class="[{clicked: selected===index,
             lastClicked: lastSelected===index}, currentAction]">
          </g>
        </svg>
      </svg>
    </v-flex>
    <v-flex xs4 d-flex>
      <v-layout row fill-height>
<!--        <v-flex hidden-xs-only mdr pa-1>-->
<!--          <v-card height="100%">-->
<!--            <v-card-title>-->
<!--              <div class="headline">Map</div>-->
<!--            </v-card-title>-->
<!--          </v-card>-->
<!--        </v-flex>-->
        <v-flex sm12 md8 pa-1>
          <v-card height="100%">
            <v-card-title>
              <div class="headline">{{gamePhase}}</div>
            </v-card-title>
            <v-card-text>
              <v-list>
                <v-list-tile>Armies: {{armies}}</v-list-tile>
                <v-list-tile>Action: {{getActionName(currentAction)}}</v-list-tile>
              </v-list>
            </v-card-text>
          </v-card>
        </v-flex>
        <v-flex md4 pa-1>
          <v-card height="100%">
            <v-card-title>
              <div class="headline">Players</div>
            </v-card-title>
            <v-card-text>
              <v-list-tile
                v-for="[index, player] in players.entries()"
                :key="player.publicToken"
                :color="colors[index]"
              >
                <v-list-tile-content>
                  <v-list-tile-title v-text="player.name"></v-list-tile-title>
                </v-list-tile-content>
              </v-list-tile>
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
import {gameActions, placeArmy, moveArmy} from '@/models/game'

export default {
  name: 'Game',
  beforeMount () {
    if (this.$store.state.game.game.players.length === 0) {
      this.$router.replace({name: 'home'})
    }
  },
  data () {
    return {
      currentAction: gameActions.PLACE_ARMY,
      lastSelected: -1,
      selected: -1,
      myTurn: true,
      colors: ['red', 'blue', 'green']
    }
  },
  computed: {
    ...mapGetters(['mapResource', 'getTurn', 'gamePublicToken', 'gamePhase', 'players', 'armies'])
  },
  methods: {
    territoryClicked (id) {
      this.selected = id
      if (this.gamePhase === 'Setup' && this.currentAction === gameActions.PLACE_ARMY) {
        if (this.selected !== -1) {
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
      } else if (this.gamePhase === 'Realtime') {
        switch (this.currentAction) {
          case gameActions.PLACE_ARMY:
            if (this.selected !== -1) {
              placeArmy(this.selected)
              this.selected = -1
            }
            break
          case gameActions.MOVE_ARMY:
            if (this.lastSelected !== -1 && this.selected !== -1) {
              moveArmy(this.lastSelected, this.selected)
              this.selected = -1
            }
            break
        }
        this.lastSelected = this.selected
      }
    },
    renderTerritory (territory, index) {
      let htmlObject = document.createElement('div')
      htmlObject.innerHTML = territory
      htmlObject.getElementsByTagName('tspan')['0'].innerHTML = this.$store.state.game.game.territories[index].armies
      return htmlObject.firstChild.outerHTML
    },
    getActionName (action) {
      switch (action) {
        case gameActions.PLACE_ARMY:
          return 'Place'
        case gameActions.MOVE_ARMY:
          return 'Move'
        case gameActions.ATTACK:
          return 'Attack'
      }
    }
  },
  mounted () {
    this.$store.watch(
      (state) => state.game.game.phase,
      () => {
        if (this.gamePhase === 'Realtime') {
          this.$toastr('info', 'Realtime mode has started!')
        }
      }
    )
    window.addEventListener('keydown', e => {
      switch (e.code) {
        case 'KeyQ':
          console.log('Place Army')
          this.currentAction = gameActions.PLACE_ARMY
          break
        case 'KeyW':
          console.log('Move army')
          this.currentAction = gameActions.MOVE_ARMY
          break
        case 'KeyE':
          console.log('Attack')
          this.currentAction = gameActions.ATTACK
          break
      }
    })
  }
}
</script>

<style scoped>
  svg {
    overflow: hidden;
  }

  .PLACE_ARMY {
    stroke: white;
    stroke-width: 0;
  }

  .MOVE_ARMY {
    stroke: #FFFF8D;
    stroke-width: 0;
  }

  .ATTACK {
    stroke: #FF8A80;
    stroke-width: 0;
  }

  svg > g:hover {
    stroke-width: 0.5;
  }

  .clicked, .clicked:hover {
    stroke-width: 1.5;
  }

</style>
