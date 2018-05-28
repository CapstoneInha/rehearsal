<template>
  <div>
  <md-card-content>
    <div ref="peaks"></div>
    <audio ref="audio" :src="audioPath" preload="auto">
      <source type="audio/ogg">
    </audio>
    <md-card-actions>
      <md-button @click="play">Click Me to Play Sound</md-button>
    </md-card-actions>
  </md-card-content>
  </div>
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
        height: 200,
        zoomLevels: [8]
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
@import "../../../node_modules/vue-material/src/components/MdAnimation/variables";
@import "../../../node_modules/vue-material/src/theme/engine";

.card-expansion {
  height: 480px;
}

.md-card {
  width: 320px;
  margin: 4px;
  display: inline-block;
  vertical-align: top;
}

.md-content {
  width: 200px;
  height: 100%;
  justify-content: center;
  align-items: center;
  text-align: center;
  margin-top: 60px;
}

.md-layout-item {
  height: 40px;
  margin-top: 8px;
  margin-bottom: 8px;
  transition: .3s $md-transition-stand-timing;

  &:after {
    width: 100%;
    height: 100%;
    display: block;
  }
}

.md-field {
  max-width: 300px;
}

.md-table {
  width: 100%;
  height: 100%;
}
</style>
