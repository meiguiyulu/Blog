import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Element from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css';
import Axios from 'axios'

import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

import './axios'


Vue.config.productionTip = false

Vue.use(Element)
Vue.prototype.$axios = Axios
Vue.use(mavonEditor)

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
