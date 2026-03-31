import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

const routes = [
  { path: '/', redirect: '/login' },
  { 
    path: '/login', 
    name: 'Login',
    component: () => import('../views/Login.vue') 
  },
  { 
    path: '/student', 
    name: 'Student',
    component: () => import('../views/Student.vue'),
    meta: { role: 'STUDENT' } // 统一大写标识
  },
  { 
    path: '/admin', 
    name: 'Admin',
    component: () => import('../views/Admin.vue'),
    meta: { role: 'ADMIN' }   // 统一大写标识
  }
];

const router = new VueRouter({
  mode: 'hash', // 或者 'history'
  routes
});

// 全局路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role'); // 这里存的是我们强转大写后的 'ADMIN' 或 'STUDENT'

  // 1. 如果去登录页，直接放行
  if (to.path === '/login') {
    return next();
  }

  // 2. 没登录则重定向到登录页
  if (!token) {
    return next('/login');
  }

  // 3. 权限校验：如果目标页面有角色要求，且与当前用户角色不符
  if (to.meta.role && to.meta.role !== role) {
    // 强制跳转到各自对应的首页，防止走错门
    const correctPath = role === 'ADMIN' ? '/admin' : '/student';
    // 只有当前不在正确路径时才跳转，避免死循环
    if (to.path !== correctPath) {
      return next(correctPath);
    }
  }

  next();
});

export default router;