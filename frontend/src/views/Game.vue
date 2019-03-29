<template>
  <v-layout column>
    <v-flex xs8 d-flex>
      <svg width="100%" viewBox="0 0 2000 700" @click="territoryClicked(-1)">
        <svg :viewBox="mapResource.viewBox">
        <!-- <circle v-for="(territory, index) in mapResource.territories" :key="index+5" :cx="0" :cy="50" :r="50"/> -->
          <g v-for="(territory, index) in mapResource.territories" :key="index" v-html="renderTerritory(territory, index)"
             @click.stop="territoryClicked(index)" :class="{clicked:selected === index}">
          </g>
        </svg>
        <!--<svg viewBox="0 0 111.50298 112.7476">-->
          <!--&lt;!&ndash;height="112.7476mm"&ndash;&gt;-->
          <!--&lt;!&ndash;width="111.50298mm">&ndash;&gt;-->
          <!--<g @click.stop="territoryClicked(0)" :class="{clicked:selected == 0}">-->
            <!--<g id="layer1" transform="translate(-3.0238111,-88.624414)" style="display:inline"-->
               <!--xmlns="http://www.w3.org/2000/svg" xmlns:svg="http://www.w3.org/2000/svg" xml-->
               <!--ns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:cc="http://creativecommons.org/ns#"-->
               <!--xmlns:dc="http://purl.org/dc/elements/1.1/">-->
              <!--<path style="fill:#3949ab;fill-opacity:1;"-->
                    <!--d="m 15.607728,104.12143 6.803573,17.76489 17.386907,6.42559 20.78869,-23.43452 -26.83631,2.64583 -13.229169,-18.898806 z"-->
                    <!--id="path4524"-->
              <!--/>-->
              <!--<path-->
                <!--style="fill:#33ffff;stroke:none;stroke-width:0.26458332px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1;fill-opacity:1"-->
                <!--d="m 29.86012,112.92558 3.212798,5.48066-->
           <!--6.425594,-1.88988 -0.188986,-4.34673 z" id="path4710"/>-->
            <!--</g>-->
          <!--</g>-->
          <!--&lt;!&ndash;<g&ndash;&gt;-->
          <!--&lt;!&ndash;style="display:inline"&ndash;&gt;-->
          <!--&lt;!&ndash;transform="translate(-3.0238111,-88.624414)"&ndash;&gt;-->
          <!--&lt;!&ndash;id="layer1" @click.stop="territoryClicked(0)" :class="{clicked:selected == 0}">&ndash;&gt;-->
          <!--&lt;!&ndash;<path&ndash;&gt;-->
          <!--&lt;!&ndash;id="path4524"&ndash;&gt;-->
          <!--&lt;!&ndash;d="m 15.607728,104.12143 6.803573,17.76489 17.386907,6.42559 20.78869,-23.43452 -26.83631,2.64583 -13.229169,-18.898806 z"&ndash;&gt;-->
          <!--&lt;!&ndash;style="fill:#3949ab;fill-opacity:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"/>&ndash;&gt;-->
          <!--&lt;!&ndash;</g>&ndash;&gt;-->
          <!--<g-->
            <!--transform="translate(-3.0238111,-1.624414)"-->
            <!--id="layer2" @click.stop="territoryClicked(1)" :class="{clicked:selected == 1}">-->
            <!--<path-->
              <!--id="path4522"-->
              <!--d="m 11.71726,19.500004 -8.3154759,30.61607 29.0124569,21.63243 4.595007,-6.40233 1.65503,-21.6367 -18.631542,-6.44459 z"-->
              <!--style="display:inline;fill:#e53935;fill-opacity:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"/>-->
          <!--</g>-->
          <!--<g-->
            <!--transform="translate(-3.0238111,-1.624414)"-->
            <!--id="layer3" @click.stop="territoryClicked(2)" :class="{clicked:selected == 2}">-->
            <!--<path-->
              <!--id="path4526"-->
              <!--d="m 41.730008,64.773294 2.4932,-22.594719 20.41072,-21.16667 45.735122,19.27679 -36.663692,9.07143 -17.38691,25.702379 z"-->
              <!--style="display:inline;fill:#5e35b1;fill-opacity:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"/>-->
          <!--</g>-->
          <!--<g-->
            <!--transform="translate(-3.0238111,-1.624414)"-->
            <!--id="layer4" @click.stop="territoryClicked(3)" :class="{clicked:selected == 3}">-->
            <!--<path-->
              <!--id="path4546"-->
              <!--d="m 3.0238111,54.651784 27.2142849,20.41072 -1.889882,6.80357 21.166664,29.860116 -40.4434489,2.64583 z"-->
              <!--style="display:inline;fill:#ffb300;fill-opacity:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"/>-->
          <!--</g>-->
          <!--<g @click.stop="territoryClicked(4)" :class="{clicked:selected == 4}">-->
            <!--<g-->
              <!--transform="translate(-3.0238111,-1.624414)"-->
              <!--id="layer5">-->
              <!--<path-->
                <!--id="path4544"-->
                <!--d="m 75.973208,52.761905 -19.27678,27.214289 -16.63095,-12.47321 -5.66965,12.47321 20.41072,30.994046 59.720242,-26.080356 z"-->
                <!--style="display:inline;fill:#43a047;fill-opacity:1;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1"/>-->
            <!--</g>-->
          <!--</g>-->
        <!--</svg>-->
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
            <v-card-text>
              Test
            </v-card-text>
            <v-card-text>
            You have {{armyCount}} armies remaining
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>
    </v-flex>
  </v-layout>
</template>

<script>
import {mapGetters} from 'vuex'
import {types} from '@/vuex/modules'
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
    ...mapGetters(['mapResource', 'getTurn', 'gamePublicToken']),
    armyCount () {
      var name = this.getClientName
      var playersWithCount = this.$store.state.game.game.players
      var count = 0
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
    },
    renderTerritory (territory, index) {
      var htmlObject = document.createElement('div')
      htmlObject.innerHTML = territory
      htmlObject.getElementsByTagName('tspan')['0'].innerHTML = this.$store.state.game.game.territories[index].armies
      return htmlObject.firstChild.outerHTML
    }
  },
  created () {
    this.$store.subscribe((mutation, state) => {
      if (mutation.type === types.NOTIFY_TURN) {
        if (state.game.turn === state.game.publicToken) {
          this.$toastr('info', '', 'It is your turn to ' + mutation.payload.turnPhase)
        } else {
          this.$toastr('info', '', 'Someone else\'s turn')
        }
      }
    })
  }
}
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
