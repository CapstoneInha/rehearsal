<template>
  <div id="app">
    <md-toolbar class="md-primary" md-elevation="1">
      <h3 class="md-title" style="flex: 1">Rehearsal</h3>
      <md-button to="/">Home</md-button>
    </md-toolbar>
    <router-view/>
  </div>
</template>

<script>
  export default {
    name: 'App',
    methods: {
    }
  }
</script>

<style lang="scss" scoped>
  @import "~vue-material/src/theme/engine";

  .md-toolbar + .md-toolbar {
    margin-top: 16px;
  }
</style>

