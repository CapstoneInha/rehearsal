import Vue from 'vue'
import Router from 'vue-router'
import App from './App.vue'
import Base from '@/components/Base'
import VueMaterial from 'vue-material'
import 'vue-material/dist/vue-material.min.css'
import 'vue-material/dist/theme/default-dark.css'
import {MdButton, MdContent, MdTabs} from 'vue-material/dist/components'

Vue.use(Router);
Vue.use(MdButton);
Vue.use(MdContent);
Vue.use(MdTabs);
Vue.use(VueMaterial);
Vue.config.productionTip = false;

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '**',
      name: 'Base',
      component: Base
    }
  ]
});

new Vue({
  el: '#app',
  router,
  components: {App},
  template: '<App/>'
});
