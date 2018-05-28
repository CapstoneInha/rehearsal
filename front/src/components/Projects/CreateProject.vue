<template>
  <div class="card-expansion">
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
</template>

<script>
export default {
  name: 'CreateProject',
  data: function () {
    return {
      showDialog: false,
      project: {
        id: null,
        title: null,
        plot: null,
        file: null,
        fileName: null
      }
    }
  },
  methods: {
    onFileChange(event) {
      const file = event.target.files[0];
      this.project.fileName = file.name;
      let reader = new FileReader();
      reader.onload = (e) => {
        this.project.file = e.target.result;
      };
      reader.readAsDataURL(file);
    }, saveProject() {
      console.log(this.project);
      this.$http.post("/api/prod/projects", this.project)
        .then((result) => {
          console.log(result);
        })
        .then(() => {
          this.$emit('clickedProjectAdd', this.project)
        })
    }, clearForm() {
      this.project.id = null;
      this.project.title = null;
      this.project.plot = null;
      this.project.file = null;
      this.project.fileName = null;
    }
  }
}
</script>

<style lang="scss" scoped>

.md-dialog {
  max-width: 768px;
}
</style>
