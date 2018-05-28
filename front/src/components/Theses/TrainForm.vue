<template>
  <div>
    <div v-if="project.state !== 'RUNNING'">
      <md-button class="md-primary md-raised" @click="active = true">Train Start</md-button>
    </div>
    <div v-else>
      <md-button  class="md-raised md-accent" disabled>Train RUNNING..</md-button>
    </div>
    <md-dialog-confirm
      :md-active.sync="active"
      md-title="Use Google's location service?"
      md-content="Let Google help apps determine location. <br> This means sending <strong>anonymous</strong> location data to Google, even when no apps are running."
      md-confirm-text="Agree"
      md-cancel-text="Disagree"
      @md-confirm="onConfirm" />

  </div>
</template>

<script>
export default {
  name: 'TrainForm',
  props: {
    project: null
  },
  data: () => ({
    active: false,
    value: null
  }),
  methods: {
    onConfirm () {
      let param = {};
      param.id= this.project.id;
      param.title= this.project.title;
      param.plot= this.project.plot;
      param.createAt= this.project.createdAt;

      param.state= "RUNNING";
      this.$http.put(`/api/prod/projects/${this.project.id}`, param)
        .then((result) => {
          this.project.state="RUNNING";
        });
    }
  }
}
</script>
