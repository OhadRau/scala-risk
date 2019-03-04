<template>
  <v-layout column>
    <v-flex xs8 d-flex>
      <svg width="100%" viewBox="0 0 2000 700">
        <polygon @click="territoryClicked(0)" :class="{clicked:selected == 0}"
                 points="118.3980026,106.9002991 110.3418045,168.6753082 157.9750977,174.214798 161.7349091,125.6010056 142.8388977,123.868103 144.5113068,110.4550018 "/>
        <polygon @click="territoryClicked(1)" :class="{clicked:selected == 1}"
                 points="146.2432098,121.6489029 210.8184052,125.7578049 212.1533966,77.5830002 151.0596008,73.3442001 "/>
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
          </v-card>
        </v-flex>
      </v-layout>
    </v-flex>
  </v-layout>
</template>

<script>
export default {
  name: 'Game',
  beforeMount () {
    if (this.$store.state.game.game === null) {
      // this.$router.replace({name: 'home'})
    }
  },
  data () {
    return {
      selected: -1
    }
  },
  computed: {
  },
  methods: {
    territoryClicked (id) {
      this.selected = id
    }
  }
}
</script>

<style scoped>
  svg {
    overflow: hidden;
  }
  polygon:hover {
    stroke: pink;
    stroke-width: 0.5;
  }
  polygon.clicked {
    stroke: pink;
    stroke-width: 1;
  }
</style>
