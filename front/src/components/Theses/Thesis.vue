<template>
  <md-card-content>
    <div class="md-layout md-gutter">
      <div class="md-layout-item md-small-size-100">
        <md-card-media>
          <canvas ref="pdfRef"></canvas>
        </md-card-media>
        <md-card-actions>
          <span>Page: <span id="page_num"></span> / <span id="page_count"></span></span>

          <md-button id="prev" class="md-primary" @click.native='onPrevPage'>Prev</md-button>
          <md-button id="next" class="md-primary" @click.native='onNextPage'>Next</md-button>
          &nbsp; &nbsp;
        </md-card-actions>
      </div>

    </div>
  </md-card-content>
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
    return {
      pdfDoc: null,
      pageNum: 1,
      pageRendering: false,
      pageNumPending: null,
      scale: 0.8
    }
  },
  methods: {
    renderPage(num) {
      this.pageRendering = true;
      this.pdfDoc.getPage(num)
        .then((page) => {
          console.log(page)

          let viewport = page.getViewport(this.scale);
          let canvas = this.$refs.pdfRef;
          let ctx = canvas.getContext('2d');
          canvas.height = viewport.height;
          canvas.width = viewport.width;

          // Render PDF page into canvas context
          let renderContext = {
            canvasContext: ctx,
            viewport: viewport
          };
          let renderTask = page.render(renderContext);
          // Wait for rendering to finish
          renderTask.promise.then(() => {
            this.pageRendering = false;
            if (this.pageNumPending !== null) {
              // New page rendering is pending
              renderPage(this.pageNumPending);
              this.pageNumPending = null;
            }
          });
        });

      // Update page counters
      document.getElementById('page_num').textContent = num;
    },
    renderPdf(project) {
      PDFJS.getDocument(project.imagePath)
        .then((pdfDoc_) => {
          this.pdfDoc = pdfDoc_;
          document.getElementById('page_count').textContent = this.pdfDoc.numPages;
          console.log(this.pageNum)
          this.renderPage(this.pageNum);
        });
    },
    queueRenderPage(num) {
      if (this.pageRendering) {
        this.pageNumPending = num;
      } else {
        this.renderPage(num);
      }
    },
    onPrevPage() {
      if (this.pageNum <= 1) {
        return;
      }
      this.pageNum--;
      this.queueRenderPage(this.pageNum);
    },
    onNextPage() {
      if (this.pageNum >= this.pdfDoc.numPages) {
        return;
      }
      this.pageNum++;
      this.queueRenderPage(this.pageNum);
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
