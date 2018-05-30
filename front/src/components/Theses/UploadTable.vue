<template>
  <md-card-content>
    <md-table v-model="files" md-card @md-selected="onSelect">
      <md-table-toolbar>
        <h1 class="md-title">Choice Audio File For Uploading</h1>
      </md-table-toolbar>

      <md-table-toolbar slot="md-table-alternate-header" slot-scope="{ count }">
        <div class="md-toolbar-section-start">{{ getAlternateLabel(count) }}</div>

        <div class="md-toolbar-section-end">
          <md-button class="md-icon-button">
            <md-icon>delete</md-icon>
          </md-button>
        </div>
      </md-table-toolbar>

      <md-table-row slot="md-table-row" slot-scope="{ item }" :md-disabled="item.name.includes('Stave')" md-selectable="multiple" md-auto-select>
        <md-table-cell md-label="Name" md-sort-by="name">{{ item.name }}</md-table-cell>
        <md-table-cell md-label="Email" md-sort-by="email">{{ item.createAt }}</md-table-cell>
        <md-table-cell md-label="Gender" md-sort-by="gender">{{ item.size }}</md-table-cell>
      </md-table-row>



    <form class="md-layout" @submit.prevent="saveFile" method="post">
      <md-field>
        <label for="file">AUDIO FILE</label>
        <md-file name="file" id="file" multiple accept="audio/*"
                 @change="onFileChange($event)"/>
      </md-field>
      <md-card-actions>
        <md-button type="submit" class="md-primary">UPLOAD</md-button>
      </md-card-actions>
    </form>
    </md-table>
  </md-card-content>

</template>

<script>
import moment from "moment"

export default {
  name: 'UploadTable',
  data: () => ({
    selected: [],
    files: []
  }),
  props: {
    project: null
  },
  methods: {
    onSelect (items) {
      this.selected = items
    },
    getAlternateLabel (count) {
      let plural = ''

      if (count > 1) {
        plural = 's'
      }

      return `${count} user${plural} selected`
    },
    onFileChange(event) {
      const loadingFile = event.target.files[0];
      console.log(loadingFile);
      let file = {}
      file.name = loadingFile.name;
      file.size = loadingFile.size;
      file.createdAt = moment(Date.now()).format("YYYY-MM-DD HH:mm:ss");

      let reader = new FileReader();
      reader.onload = (e) => {
        file.data = e.target.result;
        this.files.push(file)
      };
      reader.readAsDataURL(loadingFile);
    },
    saveFile() {
      return this.$http.post(`/api/prod/projects/${this.project.id}/files`, this.files)
        .then((result) => {
          console.log(result);
        }, (error) => {
          console.error(error);
        })
        .then(() => {
          this.$emit('clickedAudioAdd', this.files)
          this.files = []
        })
//        .then(this.clearForm)

    },
    clearForm() {
      return Promise.resolve(this.file = null);
    }
  }
}
</script>

<style lang="scss" scoped>
.md-table {
  margin-top: 16px;
  width: 700px;
}
</style>
