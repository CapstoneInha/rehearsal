<template>
  <div class="md-layout md-alignment-center-center">
    <div class="md-layout-item md-medium-size-33 md-small-size-50 md-xsmall-size-100">
      <md-card-content>
        <div class="md-layout md-gutter">
          <div class="md-layout-item md-small-size-100">
            <div ref="peaksref"></div>

            <audio ref="audioref" id="audioId" preload="auto">
              <source
                src=""
                type="audio/ogg">
            </audio>
            <script src="bower_components/requirejs/require.js" data-main="app.js"></script>

            <form novalidate class="md-layout md-gutter" @submit.prevent="uploads()">
              <div class="md-layout-item md-small-size-100">
                <md-field>
                  <label>AUDIO FILE</label>
                  <md-file v-model="multiple" name="files" id="files" multiple/>
                </md-field>
              </div>
              <md-card-actions>
                <md-button type="submit" class="md-primary" :disabled="sending">UPLOAD</md-button>
                <md-button @click="play">Click Me to Play Sound</md-button>
              </md-card-actions>
            </form>
          </div>
        </div>
      </md-card-content>
    </div>

    <div class="md-layout-item md-medium-size-33 md-small-size-50 md-xsmall-size-50">
      <md-card-content>
        <md-table v-model="searched" md-sort="name" md-sort-order="asc" md-card md-fixed-header>
          <md-table-toolbar>
            <div class="md-toolbar-section-start">
              <h1 class="md-title">Audio Files</h1>
            </div>
            <md-field md-clearable class="md-toolbar-section-end">
              <md-input placeholder="Search by file name..." v-model="search" @input="searchOnTable"/>
            </md-field>
          </md-table-toolbar>
          <md-table-empty-state
            md-label="No users found"
            :md-description="`No file found for this '${search}' query. Try a different search term or create a new file.`">
          </md-table-empty-state>
          <md-table-row slot="md-table-row" slot-scope="{ item }" @click.native="downloads(item.id)">
            <md-table-cell md-label="Name" md-sort-by="name">{{ item.name }}</md-table-cell>
            <md-table-cell md-label="Date" md-sort-by="date">{{ item.date }}</md-table-cell>
            <md-table-cell md-label="Size" md-sort-by="size" md-numeric>{{ item.size }}</md-table-cell>
          </md-table-row>
        </md-table>
      </md-card-content>
    </div>
  </div>

</template>

<script>
  import Peaks from 'peaks.js';

  const AudioFileListURL = "";
  const toLower = text => {
    return text.toString().toLowerCase()
  };

  const searchByName = (items, term) => {
    if (term) {
      return items.filter(item => toLower(item.name).includes(toLower(term)))
    }

    return items
  };

  export default {
    name: 'Main',
    data() {
      return {
        msg: 'Welcome to Your Vue.js App',
        search: null,
        searched: [],
        users: [
          {
            id: 1,
            name: 'test',
            size: 'gsgs',
            date: 111
          }, {
            id: 2,
            name: 'test2',
            size: 'gsgs2',
            date: 111
          }, {
            id: 3,
            name: 'test3',
            size: 'gsgs3',
            date: 111
          }
        ],
        initial: 'vue-material-is-awesome.jpg',
        single: null,
        placeholder: null,
        disabled: null,
        multiple: null
      }
    },
    methods: {
      searchOnTable() {
        this.searched = searchByName(this.users, this.search)
      },
      downloads: function (id) {
        console.log(id);
        this.http.get(AudioFileListURL + '/' + id)
          .then((result) => {
            console.log(result);
          });
      },
      play: function (event) {
        this.$refs.audioref.play();
      },
      uploads: function (input) {
        this.http.post(AudioFileListURL)
          .then((result) => {
            console.log(result);
          });
      }

    },
    created() {
      this.searched = this.users;

      this.http.get(AudioFileListURL)
        .then((result) => {
          console.log(result);
        });
    },
    mounted() {
      const myAudioContext = new AudioContext();

      const option = {
        mediaElement: this.$refs.audioref,
        container: this.$refs.peaksref,
        audioContext: myAudioContext,
        height: 200,
        zoomLevels: [8]
      };

      var p = Peaks.init(option);
    }
  }
</script>

<style lang="scss" scoped>
  @import "~vue-material/src/components/MdAnimation/variables";
  @import "~vue-material/src/theme/engine";

  .md-content {
    width: 200px;
    height: 200px;
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
      /*background: md-get-palette-color(purple, 200);*/
    }
  }

  .md-field {
    max-width: 300px;
  }

  .md-table {
    height: 100%;
  }
</style>
