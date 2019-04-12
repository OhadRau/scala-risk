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

export function attack (from, to, armyNum) {
  if (store.getters.getTerritory(from).neighbours.includes(to)) {
    // var aT = new AttackTerritory(store.state.game.token, store.state.game.joinedRoom.roomId, from, to)
    console.log(`attack territory ${to} from ${from} with ${armyNum} armies`)
  } else {
    console.log('territories need to be adjacent')
  }
}
