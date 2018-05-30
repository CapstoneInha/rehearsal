<template>
  <md-card-content>
    <md-card-media>
      <div class='peaks' ref="peaks"></div>
      <audio ref="audio" :src="audioPath" preload="auto">
        <source type="audio/ogg">
      </audio>
    </md-card-media>
    <md-card-actions>
      <md-button @click="play">Click Me to Play Sound</md-button>
    </md-card-actions>
  </md-card-content>
</template>

<script>
import Peaks from 'peaks.js';


export default {
  name: 'AudioVisualizer',
  data() {
    return {
      peak: null
    }
  },
  props: {
    audioPath: null
  },
  methods: {
    play: function () {
      this.$refs.audio.play();
    },
    renderAudioGraph() {
      const option = {
        mediaElement: this.$refs.audio,
        container: this.$refs.peaks,
        audioContext: new AudioContext(),
        height: 300,
        zoomLevels: [512]
      };

      this.peak = Peaks.init(option);
    }
  },
  watch: {
    audioPath: function (newProject) {
      console.log("watch: " + newProject);
      this.renderAudioGraph(newProject)
    }
  }
}
</script>

<style lang="scss" scoped>
.peaks {
  width: 1000px;

}
</style>
