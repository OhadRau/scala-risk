<template>
  <v-app dark>
    <v-toolbar app>
      <v-toolbar-title>{{navTitle}}</v-toolbar-title>
    </v-toolbar>
    <v-content>
      <v-container fluid fill-height>
        <router-view></router-view>
      </v-container>
    </v-content>
    <v-footer app
              height="auto"
              class="grey--text">
      <v-layout
        justify-center
        row
        wrap
      >
        <v-flex
          py-3
          text-xs-center
          xs12
        >
          © Copyright 2019 Dhruva Bansal, Michael Lidong Chen, Yaotian Feng, Hemang Rajvanshy, Ohad Shai Rau, Chun Man Oswin So
        </v-flex>
      </v-layout>
    </v-footer>
  </v-app>
</template>

<style lang="scss">
</style>

<script>
import {mapGetters} from 'vuex'

export default {
  name: 'AppRoot',
  computed: {
    ...mapGetters([
      'navTitle'
    ])
  }
}
</script>
