import store from '@/vuex/store.js'
import {PlaceArmy} from '@/models/packets'
import Vue from '@/main.js'

export const gameActions = {
  PLACE_ARMY: 'PLACE_ARMY',
  MOVE_ARMY: 'MOVE_ARMY',
  ATTACK: 'ATTACK'
}

export function placeArmy (territoryId) {
  console.log(store.getters)
  console.log(territoryId)

  const x = store.getters.getTerritory(territoryId).ownerToken
  console.log(x)
  if (store.getters.getTerritory(territoryId).ownerToken === store.getters.gamePublicToken || store.getters.getTerritory(territoryId).ownerToken === '') {
    console.log('yay')
    // Check if has enough armies
    if (store.getters.armies > 0) {
      console.log('yay2')
      Vue.$socket.sendObj(new PlaceArmy(store.state.game.token, store.state.game.joinedRoom.roomId, territoryId))
    }
  }
}

export function moveArmy (from, to) {
  console.log(`moveArmy from ${from} to ${to}`)
}
