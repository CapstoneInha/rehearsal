<template>
  <div>
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
            <md-table-row slot="md-table-row" slot-scope="{ item }" @click.native="loadFild(item)">
              <md-table-cell md-label="Name" md-sort-by="name">{{ item.name }}</md-table-cell>
              <md-table-cell md-label="Date" md-sort-by="createdAt">{{ item.createdAt }}</md-table-cell>
              <md-table-cell md-label="Size" md-sort-by="size" md-numeric>{{ item.size }}</md-table-cell>
            </md-table-row>
          </md-table>

          <create-audio :project="project" @clickedAudioAdd="addAudioFile"></create-audio>
        </md-card-content>
  </div>
</template>

<script>
import CreateAudio from './CreateAudio.vue'

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
  name: 'AudioTable',
  data() {
    return {
      msg: 'Welcome to Your Vue.js App',
      search: '',
      searched: [],
      files: []
    }
  },
  components: {
    'CreateAudio': CreateAudio
  },
  props: {
    project: null
  },
  methods: {
    searchOnTable() {
      this.searched = searchByName(this.files, this.search)
    }, setRoute(id) {
      this.$router.push(id);
    },
    findAudioList: function (project) {
      return this.$http.get(`/api/prod/projects/${project.id}/files`)
        .then((result) => {
          this.searched = this.files = result.body;
        });
    },
    loadFild : function (file) {
      console.log(file);
      this.$emit('clickedAudio', `/file/www.rehearsal-api.com/2/${file.name}`);
    },
    addAudioFile: function (file) {
      this.files.push(file);
    }
  },
  watch: {
    project: function (newProject) {
      this.findAudioList(newProject)
    }
  }
}
</script>

<style lang="scss" scoped>

.md-field {
  max-width: 300px;
}

.md-table {
  width: 100%;
  height: 100%;
}
</style>
