const types = {
  SET_PAGE_TITLE: 'SET_PAGE_TITLE'
}

const state = {
  pageTitle () {
    return document.title
  }
}

const mutations = {
  [types.SET_PAGE_TITLE] (state, title) {
    document.title = title
  }
}

export default {
  state,
  mutations
}
