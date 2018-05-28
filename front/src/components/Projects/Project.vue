<template>
  <div>
    <md-card v-for="project in projects" :key="project.id">
      <md-card-media-cover md-solid>
        <md-card-media>
          <canvas :ref="bindId(project.id)"></canvas>
        </md-card-media>
        <md-card-area>
          <md-card-header>
            <div class="md-title">{{ project.title }}</div>
            <div class="md-subhead">{{ project.createAt }}</div>
          </md-card-header>
          <md-card-expand>
            <md-card-actions md-alignment="space-between">
              <div>
                <md-button @click.native='setRoute("ThesisBase", project.id)'>Detail</md-button>
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
PDFJS.workerSrc = require('pdfjs-dist/build/pdf.worker');

export default {
  name: 'Project',
  props: {
    projects: []
  },
  data: function () {
    return {}
  },
  methods: {
    setRoute(path, id) {
      this.$router.push({name: path, params: {id: id}});
    }, renderPdf() {
      this.projects.forEach((it) => {
        let loadingTask = PDFJS.getDocument(it.imagePath);
        let canvas = this.$refs[this.bindId(it.id)][0];
        console.log(canvas)
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
        });
      })
    }, bindId (item) {
      return '#' + item;
    }
  },
  updated() {
    this.renderPdf();
  }
}
</script>

<style lang="scss" scoped>
.md-card {
  width: 320px;
  margin: 4px;
  display: inline-block;
  vertical-align: top;
}

</style>
