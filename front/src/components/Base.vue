<template>
  <div class="card-expansion">
    <div>
      <md-empty-state
        md-icon="devices_other"
        md-label="Create your new project"
        md-description="Creating project, you'll be able to upload your design and collaborate with people.">
        <md-dialog :md-active.sync="showDialog">
          <div>
            <form class="md-layout" @submit.prevent="saveProject" method="post">
              <md-card class="md-layout-item md-size-100 md-medium-size-100 md-small-size-100">
                <md-card-header>
                  <div class="md-title">Projects</div>
                </md-card-header>
                <md-card-content>
                  <div class="md-layout md-gutter">
                    <div class="md-layout-item md-size-100 md-medium-size-100 md-small-size-100">
                      <md-field>
                        <label for="title">Title</label>
                        <md-input name="title" id="title" autocomplete="given-name" v-model="project.title"/>
                      </md-field>
                    </div>
                    <div class="md-layout-item md-size-100 md-medium-size-100 md-small-size-100">
                      <md-field>
                        <label for="plot">Plot</label>
                        <md-input name="plot" id="plot" autocomplete="family-name" v-model="project.plot"/>
                      </md-field>
                    </div>
                    <div class="md-layout-item md-size-100 md-medium-size-100 md-small-size-100">
                      <md-field>
                        <label for="file">PDF FILE</label>
                        <md-file name="file" id="file" accept="pdf/*"
                                 @change="onFileChange($event)"/>
                      </md-field>
                    </div>
                  </div>
                </md-card-content>
                <md-card-actions>
                  <md-button type="submit" class="md-primary">Create project</md-button>
                </md-card-actions>
              </md-card>
              <md-snackbar :md-active.sync="userSaved">The project was saved with success!</md-snackbar>
            </form>
          </div>
        </md-dialog>
        <span v-if="value">Value: {{ value }}</span>
        <md-button class="md-primary md-raised" @click="showDialog = true">Create new project</md-button>
      </md-empty-state>
    </div>

    <md-card v-for="project in projects" :key="project.id">
      <md-card-media-cover md-solid>
        <md-card-media>
          <canvas :ref="bindId(project.id)">{{ project.title }}</canvas>
        </md-card-media>
        <md-card-area>
          <md-card-header>
            <div class="md-title">{{ project.title }}</div>
            <div class="md-subhead">{{ project.createAt }}</div>
          </md-card-header>
          <md-card-expand>
            <md-card-actions md-alignment="space-between">
              <div>
                <md-button @click.native='setRoute("Thesis", project.id)'>Detail</md-button>
              </div>
              <md-card-expand-trigger>
                <md-button>PLOT</md-button>
              </md-card-expand-trigger>
            </md-card-actions>
            <md-card-expand-content>
              <md-card-content>
                {{ project.plot }}
              </md-card-content>
            </md-card-expand-content>
          </md-card-expand>
        </md-card-area>
      </md-card-media-cover>
    </md-card>
  </div>
</template>

<script>
  import PDFJS from 'pdfjs-dist';
  import Constants from '../utils/Constants.json';
  PDFJS.workerSrc = require('pdfjs-dist/build/pdf.worker');

  export default {
    name: 'CardExpansion',
    data: function () {
      return {
        showDialog: false,
        project: {
          id: null,
          title: null,
          plot: null,
          file: null,
          fileName: null,
          mediaType: null
        },
        projects: [],
      }
    },
    methods: {
      setRoute(path, id) {
        this.$router.push({name: path, params: {id: id}});
      }, onFileChange(event) {
        const file = event.target.files[0];
        this.project.fileName = file.name;
        this.project.mediaType = file.type;
        let reader = new FileReader();
        reader.onload = (e) => {
          this.project.file = e.target.result;
        };
        reader.readAsDataURL(file);
      }, saveProject() {
        console.log(this.project);
        this.$http.post(Constants.API_DOMAIN + "/prod/users/2/projects", this.project)
          .then((result) => {
            console.log(result);
          })
          .then(this.findLastProject)
          .then(this.renderLastPdf);
      }, clearForm() {
        this.project.id = null;
        this.project.title = null;
        this.project.plot = null;
        this.project.file = null;
        this.project.fileName = null;
        this.project.mediaType = null;
      }, findProjects() {
        return this.$http.get(Constants.API_DOMAIN + "/prod/users/2/projects")
          .then((result) => {
            console.log(result.body);
            this.projects = result.body;
          });
      }, findLastProject: function () {
        return this.$http.get(Constants.API_DOMAIN + '/prod/users/2/projects/last')
          .then((result) => {
            console.log(result);
            this.project.id = result.body.id;
            this.projects.push(result.body);
          });
      }, renderLastPdf() {
        let loadingTask = PDFJS.getDocument(this.project.file);
        console.log(this.$refs[this.bindId(this.project.id)]);
        let canvas = this.$refs[this.bindId(this.project.id)][0];
        loadingTask.promise.then(function (pdfDocument) {
          return pdfDocument.getPage(1).then(function (pdfPage) {
            let viewport = pdfPage.getViewport(1.0);
            canvas.width = viewport.width;
            canvas.height = viewport.height;
            let ctx = canvas.getContext('2d');
            let renderTask = pdfPage.render({
              canvasContext: ctx,
              viewport: viewport
            });

            clearForm();
            return renderTask.promise;
          });

        }).catch(function (reason) {
          console.error('Error: ' + reason);
        });
      }, renderPdf() {
        return this.projects.forEach((it) => {
          this.$http.get(Constants.API_DOMAIN + "/prod/users/2/files/" + it.fileId)
            .then((result) => {
              let loadingTask = PDFJS.getDocument(result.body);
              let canvas = this.$refs[this.bindId(it.id)][0];
              loadingTask.promise.then(function (pdfDocument) {
                return pdfDocument.getPage(1).then(function (pdfPage) {
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
        });
      }, bindId: function (item) {
        return '#' + item;
      }
    },
    mounted() {
      this.findProjects()
        .then(this.renderPdf);
    },
  }
</script>

<style lang="scss" scoped>
  .card-expansion {
    height: 480px;
  }

  .md-card {
    width: 320px;
    margin: 4px;
    display: inline-block;
    vertical-align: top;
  }

  .md-dialog {
    max-width: 768px;
  }
</style>
