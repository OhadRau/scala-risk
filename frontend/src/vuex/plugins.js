import {types} from './modules'

function sessionStoragePlugin (mutation, preprocess) {
  const deserialize = JSON.parse
  const serialize = JSON.stringify
  return (store) => {
    const raw = window.sessionStorage.getItem(mutation)
    if (raw) {
      try {
        const saved = deserialize(raw)
        store.commit(mutation, saved)
      } catch (e) {
        console.error(e)
      }
    }

    store.subscribe(({type, payload}, state) => {
      if (type === mutation && state.game.attemptedAuthentication) {
        window.sessionStorage.setItem(type, serialize(payload))
      }
    })
  }
}

export const authTokenPlugin = sessionStoragePlugin(types.SET_TOKEN)
