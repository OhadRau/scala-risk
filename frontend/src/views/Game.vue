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
                <v-list-tile>
                  Action:
                  <v-layout row wrap>
                    <v-flex xs3 v-for="(key, action) in gameActions" :key="key">
                      <v-btn flat small :color="action === currentAction ? 'success' : 'normal'" @click.native="currentAction = action">
                        {{getActionName(action)}} ({{getActionShortcut(action)}})
                      </v-btn>
                    </v-flex>
                  </v-layout>
                </v-list-tile>
              </v-list>
            </v-card-text>
          </v-card>
        </v-flex>
        <v-flex md4 pa-1>
          <v-card height="100%">
            <v-card-title>
              <div class="headline">Armies for Attacking</div>
            </v-card-title>
            <v-card-text>
              <v-radio-group v-model="attackGroup">
                <v-radio
                  v-for="n in 3"
                  :key="n"
                  :label="`${n} Dice`"
                  :value="n"
                ></v-radio>
              </v-radio-group>
            </v-card-text>
          </v-card>
        </v-flex>
        <v-flex md4 pa-1>
          <v-card height="100%">
            <v-card-title>
              <div class="headline">Armies for Defending</div>
            </v-card-title>
            <v-card-text>
              <v-radio-group v-model="defendGroup">
                <v-radio
                  v-for="n in 3"
                  :key="n"
                  :label="`${n} Dice`"
                  :value="n"
                ></v-radio>
              </v-radio-group>
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
                :color="playerColors[player.publicToken]"
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
import {gameActions, placeArmy, moveArmy, attack} from '@/models/game'

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
      gameActions: gameActions,
      lastSelected: -1,
      selected: -1,
      myTurn: true,
      attackGroup: 1,
      defendGroup: 1,
      colors: ['red', 'blue', 'black', 'green', 'orange', 'violet']
    }
  },
  computed: {
    ...mapGetters(['mapResource', 'getTurn', 'gamePublicToken', 'gamePhase', 'players', 'armies']),
    playerColors () {
      var colorMap = {}
      var players = this.$store.state.game.players
      for (var i = 0; i < players.length; i++) {
        colorMap[players[i].publicToken] = this.colors[i]
      }
      return colorMap
    }
  },
  methods: {
    territoryClicked (id) {
      this.selected = id
      var owner = this.selected === -1 ? -1 : this.$store.state.game.game.territories[id].ownerToken
      if (this.gamePhase === 'Setup' && this.currentAction === gameActions.PLACE_ARMY) {
        if (this.selected !== -1) {
          if (this.getTurn === this.gamePublicToken) {
            if (owner === this.gamePublicToken || owner === '') {
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
              if (owner === this.gamePublicToken || owner === '') {
                placeArmy(this.selected)
              } else {
                this.$toastr('warning', 'Cannot place army', 'This is not your territory')
              }
              this.selected = -1
            }
            break
          case gameActions.MOVE_ARMY:
            if (this.lastSelected !== -1 && this.selected !== -1) {
              moveArmy(this.lastSelected, this.selected)
              this.selected = -1
            }
            break
          case gameActions.ATTACK:
            if (this.selected !== -1) {
              if (owner === this.gamePublicToken || owner === '' || this.lastSelected !== -1) {
                if (this.lastSelected !== -1 && this.selected !== -1) {
                  if (owner !== this.$store.state.game.game.territories[this.lastSelected].ownerToken) {
                    if (this.$store.state.game.game.territories[this.lastSelected].armies <= 1) {
                      this.$toastr('warning', 'Cannot attack', 'Not enough armies')
                    } else {
                      var currArmy = this.$store.state.game.game.territories[this.lastSelected].armies
                      this.$toastr('info', 'Select territory to attack', 'Attack')
                      if (this.$store.state.game.game.territories[this.lastSelected].neighbours.includes(this.selected)) {
                        if (currArmy <= 3) {
                          if (this.attackGroup <= currArmy - 1) {
                            attack(this.lastSelected, this.selected, this.attackGroup)
                          } else {
                            this.$toastr('warning', 'Reduce number of dices')
                          }
                        } else {
                          attack(this.lastSelected, this.selected, this.attackGroup)
                        }
                      } else {
                        this.$toastr('warning', 'Cannot attack', 'Not an adjacent territory')
                      }
                    }
                  } else {
                    this.$toastr('warning', 'Cannot attack', 'Cannot attack own territory')
                  }
                  this.selected = -1
                } else if (this.lastSelected === -1) {
                  this.$toastr('info', 'Select territory to attack', 'Select adjacent territory to attack')
                }
              } else {
                this.$toastr('warning', 'Cannot place army', 'This is not your territory')
                this.selected = -1
              }
            }
            break
        }
        this.lastSelected = this.selected
      }
    },
    renderTerritory (territory, index) {
      var owner = this.$store.state.game.game.territories[index].ownerToken
      let htmlObject = document.createElement('div')
      htmlObject.innerHTML = territory
      htmlObject.getElementsByTagName('tspan')['0'].innerHTML = this.$store.state.game.game.territories[index].armies
      if (owner === '') {
        htmlObject.getElementsByTagName('tspan')['0'].setAttribute('style', 'fill:#d4aa00;stroke-width:0.26458332')
      } else {
        htmlObject.getElementsByTagName('tspan')['0'].setAttribute('style', 'fill:' + this.playerColors[owner] + ';stroke-width:0.26458332')
      }
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
    },
    getActionShortcut (action) {
      switch (action) {
        case gameActions.PLACE_ARMY:
          return 'Q'
        case gameActions.MOVE_ARMY:
          return 'W'
        case gameActions.ATTACK:
          return 'E'
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
