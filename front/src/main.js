import Vue from 'vue'
import Router from 'vue-router'
import App from './App.vue'
import Project from './components/Projects/ProjectBase.vue'
import Thesis from './components/Theses/ThesisBase.vue'
import VueMaterial from 'vue-material'
import 'vue-material/dist/vue-material.min.css'
import 'vue-material/dist/theme/default-dark.css'
import VueResource from 'vue-resource';
import {MdButton, MdContent, MdTabs} from 'vue-material/dist/components'
import MenuIcon from "vue-material-design-icons/menu.vue"

Vue.use(Router);
Vue.use(MenuIcon);
Vue.use(MdButton);
Vue.use(MdContent);
Vue.use(MdTabs);
Vue.use(VueMaterial);
Vue.use(VueResource);

Vue.config.productionTip = false;

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '*',
      name: 'ProjectBase',
      component: Project
    },
    {
      path: '/thesis/:id',
      name: 'ThesisBase',
      component: Thesis
    }
  ]
});


new Vue({
  el: '#app',
  router,
  components: {App},
  template: '<App/>'
});
