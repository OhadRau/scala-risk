<template>
  <v-layout row wrap>
    <v-flex xs12 sm6 md4>
      <v-card>
        <v-toolbar>
          <v-toolbar-title>Available Rooms</v-toolbar-title>
          <v-spacer/>
          <v-btn icon @click="roomCreationDialog = true">
            <v-icon large>add</v-icon>
          </v-btn>
        </v-toolbar>
        <v-list>
          <div v-if="rooms.length">
            <v-list-tile
              v-for="room in rooms"
              :key="room.id"
              @click=""
            >
              <v-list-tile-content>
                <v-list-tile-title v-text="room.name"></v-list-tile-title>
              </v-list-tile-content>
            </v-list-tile>
            <v-divider></v-divider>
            <v-subheader>{{rooms.length}} Rooms(s) found</v-subheader>
          </div>
          <v-list-tile v-else>
            <v-list-tile-content>No Rooms Yet. Create One!</v-list-tile-content>
          </v-list-tile>
        </v-list>
      </v-card>
    </v-flex>
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
  </v-layout>
</template>

<script>
import {types} from '@/vuex/modules'

export default {
  computed: {
    rooms () {
      return this.$store.state.game.rooms
    }
  },
  data () {
    return {
      roomCreationDialog: false,
      roomCreationName: '',
      roomCreationProgress: null
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
      }
    })
  },
  methods: {
    createRoom () {
      if (this.roomCreationProgress === null && this.roomCreationName.length) {
        this.roomCreationProgress = 'waiting'
        this.$store.dispatch('gameCreateRoom', {socket: this.$socket, name: this.roomCreationName})
      }
    }
  },
  name: 'Lobby'
}
</script>

<style scoped>

</style>
