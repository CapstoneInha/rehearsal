<template>
  <div class="card-expansion">
    <create-project @clickedProjectAdd="addProject"></create-project>
    <project :projects="projects"></project>
  </div>
</template>

<script>
import Project from './Project.vue'
import CreateProject from './CreateProject.vue'

export default {
  name: 'ProjectBase',
  components: {
    'CreateProject': CreateProject,
    'Project': Project
  },
  data: function () {
    return {
      projects: [],
      memberId: 2
    }
  },
  methods: {
    findProjects() {
      return this.$http.get(`/api/prod/members/${this.memberId}/projects`)
        .then((result) => {
          console.log(result);
          this.projects = result.body;
        }, (error) => {
          console.error(error);
        });
    },
    addProject(project) {
      this.projects.push(project);
    }
  },
  created: function () {
    this.findProjects()
  }
}
</script>

<style lang="scss" scoped>
.card-expansion {
  height: 480px;
}

</style>
