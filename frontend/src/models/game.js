import store from '@/vuex/store.js'
import {PlaceArmy, AttackTerritory, MoveArmy} from '@/models/packets'
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
    // Check if has enough armies
    if (store.getters.armies > 0) {
      console.log('Sending placeArmy object')
      Vue.$socket.sendObj(new PlaceArmy(store.state.game.token, store.state.game.joinedRoom.roomId, territoryId))
    }
  }
}

export function moveArmy (from, to, armyNum) {
  console.log(`Move ${armyNum} armies from ${from} to ${to}`)
  // console.log(store.state.game.token)
  // console.log(store.state.game.joinedRoom.roomId)
  Vue.$socket.sendObj(new MoveArmy(store.state.game.token, store.state.game.joinedRoom.roomId,
    parseInt(from), parseInt(to), parseInt(armyNum)))
}

export function attack (from, to, armyNum) {
  if (store.getters.getTerritory(from).neighbours.includes(to)) {
    // var aT = new AttackTerritory(store.state.game.token, store.state.game.joinedRoom.roomId, from, to)
    console.log(`attack territory ${to} from ${from} with ${armyNum} armies`)
    Vue.$socket.sendObj(new AttackTerritory(store.state.game.token, store.state.game.joinedRoom.roomId,
      from, to, armyNum))
  } else {
    console.log('territories need to be adjacent')
  }
}
