const files = require.context('.', false, /\.js$/)
const modules = {}
export let types = {}

files.keys().forEach((key) => {
  if (key === './index.js') return
  modules[key.replace(/(\.\/|\.js)/g, '')] = files(key).default
  if (files(key).types !== undefined && files(key).type !== null) {
    types = {...types, ...files(key).types}
  }
})

export default modules
