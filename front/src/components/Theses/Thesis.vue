<template>
  <div>
        <md-card-content>
          <div class="md-layout md-gutter">
            <div class="md-layout-item md-small-size-100">
              <canvas ref="pdfRef"></canvas>
            </div>
          </div>
        </md-card-content>
  </div>
</template>

<script>
import PDFJS from 'pdfjs-dist';

PDFJS.workerSrc = require('pdfjs-dist/build/pdf.worker');

export default {
  name: 'Thesis',
  props: {
    project: null
  },
  data() {
    return {}
  },
  methods: {
    renderPdf(project) {
      console.log(1111)
        let loadingTask = PDFJS.getDocument(project.imagePath);
        let canvas = this.$refs.pdfRef;
        console.log(canvas)
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
        });
    }
  },
  watch: {
    project: function (newProject) {
      this.renderPdf(newProject)
    }
  }

}
</script>

<style lang="scss" scoped>
</style>
