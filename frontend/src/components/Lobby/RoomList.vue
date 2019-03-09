<template>
  <v-card>
    <v-toolbar>
      <v-toolbar-title>Available Rooms</v-toolbar-title>
      <v-spacer/>
      <v-btn icon @click="refreshRoomList">
        <v-icon medium>fa-sync {{syncSpin}}</v-icon>
      </v-btn>
      <v-btn icon @click="roomCreationDialog = true">
        <v-icon medium>fa-plus</v-icon>
      </v-btn>
    </v-toolbar>
    <v-list>
      <div v-if="rooms.length">
        <v-list-tile
          v-for="room in rooms"
          :key="room.id"
          @click="joinRoom(room)"
        >
          <v-list-tile-content>
            <v-list-tile-title v-text="room.name"></v-list-tile-title>
            <v-list-tile-sub-title v-text="playerCountString(room)"></v-list-tile-sub-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-divider></v-divider>
        <v-subheader>{{rooms.length}} Rooms(s) found</v-subheader>
      </div>
      <v-list-tile v-else>
        <v-list-tile-content>No Rooms Yet. Create One!</v-list-tile-content>
      </v-list-tile>
    </v-list>
    <!--Create Room Dialogue-->
    <v-dialog v-model="roomCreationDialog" max-width="650px">
      <v-card>
        <v-card-title>
          <span class="headline">Create Room</span>
        </v-card-title>
        <v-card-text>
          <v-container>
            <v-layout row v-if="roomCreationProgress !== 'waiting'">
              <v-flex grow>
                <v-form @submit.prevent="createRoom">
                  <v-text-field v-model="roomCreationName"
                                @change="roomCreationProgress = null"
                                :error="roomCreationProgress === false"></v-text-field>
                </v-form>
              </v-flex>
              <v-flex shrink>
                <v-btn @click="createRoom" :disabled="roomCreationProgress !== null">Add</v-btn>
              </v-flex>
            </v-layout>
            <v-layout v-else row justify-space-around>
              <v-flex shrink>
                <v-progress-circular
                  :size="100"
                  color="orange darken-3"
                  indeterminate
                ></v-progress-circular>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script>
import {types} from '@/vuex/modules'
import {JoinRoom} from '@/models/packets'
import { mapGetters } from 'vuex'
import {ListRoom} from '../../models/packets'

export default {
  computed: {
    ...mapGetters(['gameToken']),
    rooms () {
      return this.$store.state.game.rooms
    },
    syncSpin () {
      return this.sync ? 'fa-spin' : ''
    }
  },
  data () {
    return {
      roomCreationDialog: false,
      roomCreationName: '',
      roomCreationProgress: null,
      sync: false
    }
  },
  created () {
    this.$store.subscribe((mutation, state) => {
      if (mutation.type === types.GAME_ROOM_CREATION_RESULT_OCCURRED) {
        this.$toastr('error', mutation.payload.message, 'Failed to Create Room')
        this.roomCreationProgress = false
      } else if (mutation.type === types.GAME_ROOM_CREATED) {
        this.$toastr('success', 'Room Created!', 'Success')
        this.roomCreationProgress = null
        this.roomCreationDialog = false
        this.roomCreationName = null
      } else if (mutation.type === types.GAME_ROOMS_CHANGED) {
        if (this.sync) {
          this.sync = false
          this.$toastr('success', 'Room Listing is synced!', 'Success')
        }
      }
    })
  },
  methods: {
    refreshRoomList () {
      this.sync = true
      this.$socket.sendObj(new ListRoom(this.gameToken))
    },
    joinRoom (room) {
      this.$socket.sendObj(new JoinRoom(this.$store.state.game.token, room.roomId))
    },
    createRoom () {
      if (this.roomCreationProgress === null && this.roomCreationName.length) {
        this.roomCreationProgress = 'waiting'
        this.$store.dispatch('gameCreateRoom', {socket: this.$socket, name: this.roomCreationName})
      }
    },
    playerCountString (room) {
      return `Player Count: ${room.numClients}`
    }
  },
  name: 'RoomList'
}
</script>

<style scoped>

</style>
