import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('../views/Login.vue') },
  { path: '/student', component: () => import('../views/Student.vue') },
  { path: '/admin', component: () => import('../views/Admin.vue') }
];

export default new VueRouter({ routes });