<template>
  <v-layout align-center justify-center>
    <v-flex xs12 sm8 md4>
      <v-card class="elevation-12">
        <v-toolbar dark color="primary">
          <v-toolbar-title>Join Game</v-toolbar-title>
          <v-spacer></v-spacer>
        </v-toolbar>
        <v-card-text>
          <v-form v-on:submit.prevent="applyName">
            <v-text-field browser-autocomplete="off"
                          prepend-icon="person"
                          v-debounce:500ms="inputChanged"
                          label="Enter Name"
                          v-on:change="(value) => {this.$store.commit('SET_GAME_NAME', value)}"
                          type="text" color="amber darken-2"
                          :loading="gameToken === null"></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" :disabled="gameNameValid !== 'true' && timer === null" @click="applyName">Join</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
import {mapGetters} from 'vuex'
import {types} from '@/vuex/modules'

export default {
  name: 'home',
  computed: {
    ...mapGetters([ 'gameToken', 'gameNameValid' ])
  },
  data () {
    return {
      name: '',
      timer: null
    }
  },
  methods: {
    inputChanged (name) {
      this.$store.dispatch('gameVerifyName', {name, socket: this.$socket})
    },
    applyName () {
      const self = this
      if (this.timer) {
        clearTimeout(this.timer) // This really shouldn't happen. But just to be safe.
      }
      this.timer = setTimeout(() => {
        self.$toastr('error', 'Connection Timed out', 'Failed to set name')
        self.timer = null
      }, 3000)
      if (this.gameNameValid === 'true') {
        this.$store.dispatch('gameAssignName', {socket: this.$socket})
      } else {
        this.$toastr('warn',
          `Name ${this.$store.state.game.displayName.name} is invalid.`, 'Name Error')
      }
    }
  },
  created () {
    this.$store.subscribe((mutation, state) => {
      if (mutation.type === types.COMMIT_GAME_NAME) {
        if (this.timer) {
          clearTimeout(this.timer)
        }
        this.timer = null
        if (state.game.displayName.committed) {
          this.$toastr('success', `You are ${state.game.displayName.name} now`, 'Name Assigned!')
          this.$router.replace({name: 'Lobby'})
        } else {
          this.$toastr('error', mutation.payload.message, 'Failed to assign name')
        }
      }
    })
  },
  components: {}
}
</script>
