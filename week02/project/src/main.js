import Vue from 'vue';
import App from './App.vue';
import router from './router'; // ȷ�����Ѿ������� src/router/index.js
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

// ��ֹ����ʱ����������ʾ
Vue.config.productionTip = false;

// ȫ��ע�� ElementUI �����
Vue.use(ElementUI);

new Vue({
  router,
  render: h => h(App)
}).$mount('#app');