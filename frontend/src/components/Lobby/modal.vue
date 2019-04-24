<script>
export default {
  name: 'modal',
  data: function () {
    return {
      inputArmy: ''
    }
  },
  methods: {
    close () {
      this.$emit('close')
    },
    fortify () {
      this.inputArmy = document.getElementsByName('fortifyNum')['0'].value
      this.$emit('fortify', document.getElementsByName('fortifyNum')['0'].value)
    }
  }
}
</script>
<template>
  <transition name="modal-fade">
    <div class="modal-backdrop">
      <div class="modal"
           role="dialog"
           aria-labelledby="modalTitle"
           aria-describedby="modalDescription"
      >
        <header
          class="modal-header"
          id="modalTitle"
        >
          <slot name="header">
            How many armies would you like to move?
            <button
              type="button"
              class="btn-close"
              @click="close"
              aria-label="Close modal"
            >
              x
            </button>
          </slot>
        </header>
        <section
          class="modal-body"
          id="modalDescription"
        >
          <slot name="body">
            <label class="form-label">
              <textarea name="fortifyNum" v-model="body" rows="2"
                        cols="38" class="form-control"></textarea>
            </label>
          </slot>
        </section>
        <footer class="modal-footer">
          <slot name="footer">
            <button
              type="button"
              class="btn-green"
              @click="fortify"
              aria-label="Fortify"
            >
              Send Armies
            </button>
            <!--<button-->
              <!--type="button"-->
              <!--class="btn-green"-->
              <!--@click="close"-->
              <!--aria-label="Close modal"-->
            <!--&gt;-->
              <!--Close me!-->
            <!--</button>-->
          </slot>
        </footer>
      </div>
    </div>
  </transition>
</template>
<style>
  .modal-backdrop {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .modal {
    background: #241F1E;
    box-shadow: 2px 2px 20px 1px;
    overflow-x: auto;
    display: flex;
    flex-direction: column;
  }

  .modal-header,
  .modal-footer {
    padding: 15px;
    display: flex;
  }

  .modal-header {
    /*border-bottom: 1px solid #eeeeee;*/
    color: #4AAE9B;
    justify-content: space-between;
  }

  .modal-footer {
    /*border-top: 1px solid #eeeeee;*/
    color: #4AAE9B;
    justify-content: flex-end;
  }

  .modal-body {
    position: relative;
    color: #4AAE9B;
    padding: 0px 10px;
  }

  .btn-close {
    border: none;
    font-size: 20px;
    padding: 20px;
    cursor: pointer;
    font-weight: bold;
    color: #4AAE9B;
    background: transparent;
  }

  .btn-green {
    color: white;
    background: #4AAE9B;
    border: 1px solid #4AAE9B;
    border-radius: 2px;
  }
  textarea {
    border: 1px solid aqua;
    width:100%;
    height:100%;
    /*padding: 10px 10px;*/
  }
</style>
