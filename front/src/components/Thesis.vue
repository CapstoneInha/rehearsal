<template>
  <div>
    <div class="md-layout md-gutter">
      <div class="md-layout-item md-large-size-33 md-medium-size-33 md-small-size-50 md-xsmall-size-100">
        <md-card-content>
          <div class="md-layout md-gutter">
            <div class="md-layout-item md-small-size-100">
              <canvas ref="pdfRef" id="pdfId">Open yourpdf.pdf with PDF.js</canvas>
            </div>
          </div>
        </md-card-content>
      </div>

      <div class="md-layout-item md-large-size-66 md-medium-size-66 md-small-size-100 md-xsmall-size-50">
        <md-card-content>
          <md-table v-model="searched" md-sort="name" md-sort-order="asc" md-card md-fixed-header>
            <md-table-toolbar>
              <div class="md-toolbar-section-start">
                <h1 class="md-title">Audio Files</h1>
              </div>
              <md-field md-clearable class="md-toolbar-section-end">
                <md-input placeholder="Search by file name..." ref="audioInput" v-model="search"
                          @input="searchOnTable"/>
              </md-field>
            </md-table-toolbar>
            <md-table-empty-state
              md-label="No audio file found"
              :md-description="`No file found for this '${search}' query. Try a different search term or create a new file.`">
            </md-table-empty-state>
            <md-table-row slot="md-table-row" slot-scope="{ item }" @click.native="downloads(item.id)">
              <md-table-cell md-label="Name" md-sort-by="name">{{ item.name }}</md-table-cell>
              <md-table-cell md-label="Date" md-sort-by="createdAt">{{ item.createdAt }}</md-table-cell>
              <md-table-cell md-label="Size" md-sort-by="size" md-numeric>{{ item.size }}</md-table-cell>
            </md-table-row>
          </md-table>
          <form class="md-layout" @submit.prevent="saveFile" method="post">
            <md-field>
              <label for="file">AUDIO FILE</label>
              <md-file name="file" id="file" accept="audio/*"
                       @change="onFileChange($event)"/>
            </md-field>
            <md-card-actions>
              <md-button type="submit" class="md-primary">UPLOAD</md-button>
            </md-card-actions>
          </form>
        </md-card-content>
      </div>

      <div class="md-layout-item md-large-size-100 md-medium-size-100 md-small-size-100 md-xsmall-size-100">
        <md-card-content>
          <div ref="peaksref"></div>
          <audio ref="audioref"
                 id="audioId" preload="auto">
            <source ref="audiosourceref" id="audiosourceId" type="audio/ogg">
          </audio>
          <script src="bower_components/requirejs/require.js" data-main="app.js"></script>
          <md-card-actions>
            <md-button @click="play">Click Me to Play Sound</md-button>
          </md-card-actions>
        </md-card-content>
      </div>
    </div>
  </div>
</template>

<script>
  import PDFJS from 'pdfjs-dist';
  import Peaks from 'peaks.js';
  import Constants from '../utils/Constants.json';

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
    name: 'Thesis',
    data() {
      return {
        msg: 'Welcome to Your Vue.js App',
        search: '',
        searched: [],
        files: [],
        project: null,
        file: {
          data: null,
          projectId: null,
          memberId: null,
          name: null,
          type: null
        },
        myAudioContext: null,
        peak: null,
      }
    },
    methods: {
      searchOnTable() {
        this.searched = searchByName(this.files, this.search)
      }, setRoute(id) {
        this.$router.push(id);
      }, onFileChange(event) {
        const file = event.target.files[0];
        this.file.name = file.name;
        this.file.type = file.type;
        this.file.memberId = this.project.memberId;
        this.file.projectId = this.project.id;
        let reader = new FileReader();
        reader.onload = (e) => {
          this.file.data = e.target.result;
        };
        reader.readAsDataURL(file);
      }, saveFile() {
        this.$http.post(Constants.API_DOMAIN + "/prod/users/2/files", this.file)
          .then((result) => {
            console.log(result);
          })
          .then(this.findLastFile)
          .then(() => {
            this.clearForm();
          });
      }, clearForm() {
        this.file.data = null;
        this.file.type = null;
        this.file.name = null;
        this.file.projectId = null;
        this.file.memberId = null;
      },
      findProject: function (id) {
        return this.$http.get(Constants.API_DOMAIN + '/prod/users/2/projects/' + id)
          .then((result) => {
            this.project = result.body;
          });
      },
      findAudioList: function () {
        return this.$http.get(Constants.API_DOMAIN + "/prod/users/2/projects/" + this.project.id + "/files")
          .then((result) => {
            this.files = JSON.parse(result.body);
            this.searched = JSON.parse(result.body);
          });
      },
      findLastFile: function () {
        return this.$http.get(Constants.API_DOMAIN + "/prod/users/2/projects/" + this.project.id + "/files/last")
          .then((result) => {
            console.log(JSON.parse(result.body));
            this.files.push(JSON.parse(result.body));
            this.searched.push(JSON.parse(result.body));
            console.log(this.searched);
          });
      }, renderPdf: function () {
        PDFJS.workerSrc = require('pdfjs-dist/build/pdf.worker');
        return this.$http.get(Constants.API_DOMAIN + "/prod/users/2/files/" + this.project.fileId)
          .then((result) => {
            let loadingTask = PDFJS.getDocument(result.body);
            let canvas = this.$refs.pdfRef;
            loadingTask.promise.then(function (pdfDocument) {
              return pdfDocument.getPage(2).then(function (pdfPage) {
                let viewport = pdfPage.getViewport(1.0);
                canvas.width = viewport.width;
                canvas.height = viewport.height;
                let ctx = canvas.getContext('2d');
                let renderTask = pdfPage.render({
                  canvasContext: ctx,
                  viewport: viewport
                });

                return renderTask.promise;
              });
            }).catch(function (reason) {
              console.error('Error: ' + reason);
            });
          });
      }, downloads: function (fileId) {
        return this.$http.get(Constants.API_DOMAIN + "/prod/users/2/files/" + fileId)
          .then((result) => {
            console.log(result);
            this.$refs.audioref.src = result.body;
          });
      }, defaultDownloads: function () {
        if (this.files.length > 0) {
          return this.$http.get(Constants.API_DOMAIN + "/prod/users/2/files/" + this.files[0].id)
            .then((result) => {
              console.log(result);
              this.$refs.audioref.src = result.body;
            });
        } else {
          this.$refs.audioref.src = Constants.S3_DOMAIN + "/audio/default/dd.m4a";
        }
      }, play: function () {
        this.$refs.audioref.play();
      }
    },
    mounted() {
      console.log(this.$route.params.id);
      this.findProject(this.$route.params.id)
        .then(() => {
          this.renderPdf();
          this.findAudioList()
            .then(this.defaultDownloads)
            .then(function () {
              this.myAudioContext = new AudioContext();
              const option = {
                mediaElement: this.$refs.audioref,
                container: this.$refs.peaksref,
                audioContext: this.myAudioContext,
                height: 200,
                zoomLevels: [8]
              };

              this.peak = Peaks.init(option);
            });
        });

    }
  }
</script>

<style lang="scss" scoped>
  @import "~vue-material/src/components/MdAnimation/variables";
  @import "~vue-material/src/theme/engine";

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
