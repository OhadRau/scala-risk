<template>
  <v-card>
    <v-toolbar>
      <v-toolbar-title>Room {{joinedRoom.name}}:</v-toolbar-title>
      <v-spacer/>
      <!--<v-btn icon>-->
      <!--<v-icon large>refresh</v-icon>-->
      <!--</v-btn>-->
    </v-toolbar>

    <RoomPlayerListing :players="currentRoomPlayerList" :key-token="hostPublicToken" key-value="👑Host"></RoomPlayerListing>
    <v-card-actions>
      <v-btn color="error" :disabled="leaving" @click="sendLeave">Leave</v-btn>
      <v-spacer/>
      <v-btn color="primary" v-if="showStartGame" :disabled="(!roomReady)" @click="startGame">Start
        Game
      </v-btn>
      <v-btn color="primary" v-if="(!showStartGame && !selfReady)" @click="sendReady">Ready</v-btn>
      <v-btn color="primary" v-if="(!showStartGame && selfReady)" @click="sendReady">unReady</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script>
import {StartGame, ClientReady} from '@/models/packets'
import {mapGetters} from 'vuex'
import {types} from '@/vuex/modules'
import {LeaveRoom} from '../../models/packets'
import RoomPlayerListing from './RoomPlayerListing'

export default {
  name: 'RoomDetail',
  components: {
    RoomPlayerListing
  },
  methods: {
    sendLeave () {
      this.leaving = true
      this.$socket.sendObj(new LeaveRoom(this.gameToken, this.gameRoomRoomId))
    },
    sendReady () {
      this.$socket.sendObj(
        new ClientReady(
          this.$store.state.game.token,
          this.$store.state.game.joinedRoom.roomId,
          !this.selfReady
        )
      )
    },
    startGame () {
      this.$socket.sendObj(new StartGame(this.$store.state.game.token, this.$store.state.game.joinedRoom.roomId))
    }
  },
  data () {
    return {
      leaving: false
    }
  },
  computed: {
    ...mapGetters(['gamePublicToken', 'gameToken', 'gameRoomRoomId']),
    showStartGame () {
      // TODO: Optimization with object
      // TODO: Check for readiness of client
      const currentRoom = this.$store.state.game.rooms.find((item) => {
        return this.joinedRoom.roomId === item.roomId
      })
      return currentRoom !== undefined && currentRoom.hostToken === this.$store.state.game.publicToken
    },
    selfReady () {
      if (this.$store.state.game.joinedRoom && this.$store.state.game.joinedRoom.roomId) {
        return this.$store.state.game.joinedRoom.clientStatus.some((val) => {
          return val.publicToken === this.gamePublicToken && val.status && val.status.status === 'Ready'
        })
      } else {
        return false
      }
    },
    hostPublicToken () {
      // TODO: Optimization with object
      const currentRoom = this.$store.state.game.rooms.find((item) => {
        return this.joinedRoom.roomId === item.roomId
      })
      return currentRoom ? currentRoom.hostToken : ''
    },
    roomReady () {
      return this.currentRoomPlayerList.length >= 3
    },
    joinedRoom () {
      return this.$store.state.game.joinedRoom
    },
    currentRoomPlayerList () {
      return this.joinedRoom.clientStatus
    }
  },
  created () {
    this.$store.subscribe((mutation, state) => {
      if (mutation.type === types.GAME_ROOM_LEAVE) {
        if (mutation.payload.roomLeft && this.leaving) {
          this.leaving = false
          this.$toastr('success', `You have left room ${state.game.joinedRoom.name}.`, 'Success')
        }
      }
    })
  }
}
</script>

<style scoped>

</style>
