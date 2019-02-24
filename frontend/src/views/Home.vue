<template>
  <v-layout align-center justify-center>
    <v-flex xs12 sm8 md4>
      <v-card class="elevation-12">
        <v-toolbar dark color="primary">
          <v-toolbar-title>Join Game</v-toolbar-title>
          <v-spacer></v-spacer>
        </v-toolbar>
        <v-card-text>
          <v-form>
            <v-text-field browser-autocomplete="off"
                          prepend-icon="person"
                          v-model="name"
                          label="Enter Name"
                          type="text" color="amber darken-2"
                          :loading="gameToken === null"></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary">Join</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
import {mapGetters} from 'vuex'
export default {
  name: 'home',
  computed: {
    ...mapGetters(['gameToken'])
  },
  data () {
    return {
      name: ''
    }
  },
  components: {}
}
</script>
