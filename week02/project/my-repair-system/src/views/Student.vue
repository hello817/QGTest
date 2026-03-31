<template>
  <el-container class="main-holder">
    <el-aside width="280px" class="aside-bar">
      <div class="user-card">
        <el-avatar :size="70" icon="el-icon-user" style="background:#FF8C00"></el-avatar>
        <h3>{{ user.account || '加载中...' }}</h3>
        <el-tag type="warning" effect="dark" size="mini">STUDENT</el-tag>
        
        <div class="info-list">
          <p><i class="el-icon-office-building"></i> 楼栋: {{ user.building || '未填写' }}</p>
          <p><i class="el-icon-house"></i> 房间: {{ user.roomNumber || '未填写' }}</p>
        </div>
      </div>

      <el-menu :default-active="activeMenu" @select="handleMenuSelect" class="menu-list">
        <el-menu-item index="form">
          <i class="el-icon-edit-outline"></i>新建报修
        </el-menu-item>
        <el-menu-item index="list">
          <i class="el-icon-tickets"></i>报修记录
        </el-menu-item>
        <el-menu-item index="set">
          <i class="el-icon-setting"></i>个人设置 / 修改密码
        </el-menu-item>
        <el-menu-item index="exit">
          <i class="el-icon-right"></i>退出登录
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-main class="content-box">
      <transition name="fade-transform" mode="out-in">
        <order-form v-if="activeMenu === 'form'" @refresh="loadUser" />
        <order-list v-if="activeMenu === 'list'" />
        <user-settings v-if="activeMenu === 'set'" @refresh="loadUser" />
      </transition>
    </el-main>
  </el-container>
</template>

<script>
// 假设你已经封装好了 API，或者直接用 axios
import { getUserInfo } from '@/api/repair'; 
import OrderForm from './student/OrderForm.vue';
import OrderList from './student/OrderList.vue';
import UserSettings from './student/UserSettings.vue';

export default {
  name: 'StudentHome',
  components: { OrderForm, OrderList, UserSettings },
  data() {
    return {
      activeMenu: 'form',
      user: {}
    }
  },
  created() {
    this.loadUser();
  },
  methods: {
    // 加载用户信息
    async loadUser() {
      const userId = localStorage.getItem('userId');
      if (userId) {
        try {
          this.user = await getUserInfo(userId);
        } catch (error) {
          this.$message.error('获取用户信息失败');
        }
      }
    },
    // 处理菜单点击
    handleMenuSelect(val) {
      if (val === 'exit') {
        this.logout();
      } else {
        this.activeMenu = val;
      }
    },
    // 退出登录
    logout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        type: 'warning'
      }).then(() => {
        localStorage.clear();
        this.$router.push('/login');
        this.$message.success('已安全退出');
      }).catch(() => {});
    }
  }
}
</script>

<style scoped>
.main-holder { height: 100vh; background: #fdfdfd; }
.aside-bar { 
  background: #fff; 
  border-right: 1px solid #eee; 
  padding: 40px 20px; 
  display: flex; 
  flex-direction: column; 
  box-shadow: 2px 0 8px rgba(0,0,0,0.02);
}
.user-card { text-align: center; margin-bottom: 30px; }
.user-card h3 { margin: 15px 0 5px; color: #333; }
.info-list { 
  text-align: left; 
  margin-top: 25px; 
  font-size: 14px; 
  color: #666; 
  padding: 15px;
  background: #fcfcfc;
  border-radius: 8px;
}
.info-list p { margin: 8px 0; }
.info-list i { margin-right: 8px; color: #FF8C00; }
.menu-list { border: none; }
.content-box { background: #f9f9f9; padding: 30px; }

/* 简单的切换动画 */
.fade-transform-enter-active, .fade-transform-leave-active {
  transition: all .3s;
}
.fade-transform-enter {
  opacity: 0;
  transform: translateX(-10px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>