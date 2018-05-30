<template>
  <div class="md-scrollbar">
    <div class="md-layout md-gutter">
      <md-card >
        <Thesis :project="project"></Thesis>
      </md-card>


      <md-card >
        <audio-visualizer :audioPath="audioPath"></audio-visualizer>
      </md-card>
      <md-card >
        <audio-table :project="project" :files="files" @clickedAudio="changeAudioPath"></audio-table>
        <train-form :project="project"></train-form>
      </md-card>
      <md-card >
        <upload-table :project="project" @clickedAudioAdd="addAudioFile"></upload-table>
      </md-card>
    </div>
    <div class="md-layout md-gutter">

    </div>
  </div>
</template>

<script>
import Thesis from './Thesis.vue'
import AudioTable from './AudioTable.vue'
import AudioVisualizer from './AudioVisualizer.vue'
import TrainForm from './TrainForm.vue'
import UploadTable from './UploadTable.vue'


export default {
  name: 'ThesisBase',
  components: {
    'Thesis': Thesis,
    'AudioTable': AudioTable,
    'AudioVisualizer': AudioVisualizer,
    'TrainForm': TrainForm,
    'UploadTable': UploadTable
  },
  data() {
    return {
      project: null,
      audioPath: null,
      files: []
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
    },
    addAudioFile: function (files) {
      this.files = this.files.concat(files);
    }
  },
  created() {
    this.findProject()
  }

}
</script>

<style lang="scss" scoped>
.md-card {
  margin: 15px;
}
</style>
