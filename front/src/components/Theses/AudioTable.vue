<template>
  <div>
        <md-card-content>
          <md-table v-model="searched" md-sort="name" md-sort-order="asc" md-card md-fixed-header>
            <md-table-toolbar>
              <div class="md-toolbar-section-start">
                <h1 class="md-title">Audio Files &nbsp;<md-switch v-model="resultType" value="'COMPLETE_AUDIO'">Completed</md-switch></h1>

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
            <md-table-row slot="md-table-row" slot-scope="{ item }" @click.native="loadFild(item.name)">
              <md-table-cell md-label="Name" md-sort-by="name">{{ item.name }}</md-table-cell>
              <md-table-cell md-label="Date" md-sort-by="createdAt">{{ item.createdAt }}</md-table-cell>
              <md-table-cell md-label="Size" md-sort-by="size" md-numeric>{{ item.size }}</md-table-cell>
            </md-table-row>
          </md-table>

        </md-card-content>
  </div>
</template>

<script>
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
      resultType: "'CREATE_AUDIO'"
    }
  },
  props: {
    project: null,
    files: []
  },
  methods: {
    searchOnTable() {
      this.searched = searchByName(this.files, this.search)
    }, setRoute(id) {
      this.$router.push(id);
    },
    findAudioList: function (resultType) {
      return this.$http.get(`/api/prod/projects/${this.project.id}/files`, {params:  {resultType: resultType}})
        .then((result) => {
          this.searched = result.body;
          console.log(result.body);
          let fileName = 'default.m4a';
          if(this.searched.length > 0) {
            fileName = this.searched[0].name;
          }

          this.loadFild(fileName);
        });
    },
    loadFild : function (file) {
      this.$emit('clickedAudio', `/file/2/${file}`);
    }
  },
  watch: {
    project: function () {
      return this.findAudioList(this.resultType)
    },
    files: function (newFiles) {
      newFiles.forEach((it) => {
        this.searched.push(it);
      })
      console.log(this.searched);
      console.log(this.files);
    },
    resultType: function (newType) {
      console.log(newType||"'CREATE_AUDIO'")
      return this.findAudioList(newType||"'CREATE_AUDIO'")
    }
  }
}
</script>

<style lang="scss" scoped>

.md-field {
  max-width: 300px;
}

.md-table {
  width: 700px;
}
</style>
