<template>
  <div id="app">
    <md-toolbar class="md-primary" md-elevation="1">
      <h3 class="md-title" style="flex: 1">Rehearsal</h3>
      <md-button @click.native='setRoute("/")'>Home</md-button>
    </md-toolbar>
    <router-view/>
  </div>
</template>

<script>
  export default {
    name: 'App',
    data: () => ({
      menuVisible: false,
      received_messages: [],
      connected: false
    }),
    methods: {
      setRoute(id) {
        this.$router.push(id);
      }
    }
  }
</script>

<style lang="scss" scoped>
  @import "~vue-material/src/theme/engine";

  .md-toolbar + .md-toolbar {
    margin-top: 16px;
  }
</style>

