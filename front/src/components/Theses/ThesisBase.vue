<template>
  <div class="md-scrollbar">
    <div class="md-layout md-gutter">
      <div class="md-layout-item">
        <Thesis :project="project"></Thesis>
      </div>

      <div class="md-layout-item">
        <audio-table :project="project" @clickedAudio="changeAudioPath"></audio-table>
      </div>
      <div class="md-layout-item">
        <audio-visualizer :audioPath="audioPath"></audio-visualizer>
      </div>
    </div>
    <div class="md-layout md-gutter">

    </div>
  </div>
</template>

<script>
import Thesis from './Thesis.vue'
import AudioTable from './AudioTable.vue'
import AudioVisualizer from './AudioVisualizer.vue'

export default {
  name: 'ThesisBase',
  components: {
    'Thesis': Thesis,
    'AudioTable': AudioTable,
    'AudioVisualizer': AudioVisualizer
  },
  data() {
    return {
      project: null,
      audioPath: null
    }
  },
  methods: {
    findProject() {
      return this.$http.get(`/api/prod/projects/${this.$route.params.id}`)
        .then((result) => {
          console.log(result);
          this.project = result.body;
        });
    },
    changeAudioPath(path) {
      console.log("base: " + path);
      this.audioPath = path;
    }
  },
  created() {
    this.findProject()
  }

}
</script>

<style lang="scss" scoped>
.md-layout-item {
  height: 40px;
  display: block;

  &:after {
    width: 100%;
    height: 100%;
  }
}
</style>
