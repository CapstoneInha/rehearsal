<template>
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
</template>

<script>
import moment from "moment"

export default {
  name: 'CreateAudio',
  data() {
    return {
      file: {
        data: null,
        name: null,
        size: null,
        createdAt: null
      }
    }
  },
  props: {
    project: null
  },
  methods: {
    onFileChange(event) {
      const file = event.target.files[0];
      console.log(file);
      this.file.name = file.name;
      this.file.size = file.size;
      this.file.createdAt = moment(Date.now()).format("YYYY-MM-DD HH:mm:ss");

      let reader = new FileReader();
      reader.onload = (e) => {
        this.file.data = e.target.result;
      };
      reader.readAsDataURL(file);
    },
    saveFile() {
      return this.$http.post(`/api/prod/projects/${this.project.id}/files`, this.file)
        .then((result) => {
          console.log(result);
        }, (error) => {
          console.error(error);
        })
        .then(() => {
          this.$emit('clickedAudioAdd', this.file)
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
</style>
