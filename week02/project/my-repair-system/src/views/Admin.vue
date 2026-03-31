<template>
  <el-container class="main-holder">
    <el-aside width="280px" class="aside-bar">
      <div class="user-card">
        <el-avatar :size="70" icon="el-icon-s-custom" style="background:#409EFF"></el-avatar>
        <h3>管理员系统</h3>
        <el-tag type="success" effect="dark" size="mini">ADMINISTRATOR</el-tag>
        
        <div class="info-list">
          <p><i class="el-icon-monitor"></i> 系统状态: 运行中</p>
          <p><i class="el-icon-user"></i> 账号: {{ adminAccount }}</p>
        </div>
      </div>

      <el-menu :default-active="activeMenu" @select="handleMenuSelect" class="menu-list">
        <el-menu-item index="orders">
          <i class="el-icon-s-management"></i>报修订单管理
        </el-menu-item>
        <el-menu-item index="password">
          <i class="el-icon-key"></i>修改登录密码
        </el-menu-item>
        <el-menu-item index="exit">
          <i class="el-icon-right"></i>退出登录
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-main class="content-box">
      <transition name="fade-transform" mode="out-in">
        <div v-if="activeMenu === 'orders'" key="list">
          <el-card shadow="never" class="admin-card">
            <div slot="header" class="header-box">
              <span class="title">全校报修订单总览</span>
              <el-button size="small" icon="el-icon-refresh" type="primary" plain @click="fetchAllOrders">刷新数据</el-button>
            </div>

            <div class="table-wrapper">
              <table class="admin-table">
                <thead>
                  <tr>
                    <th width="140">订单号</th>
                    <th width="100">申请人ID</th>
                    <th width="120">维修类型</th>
                    <th>问题描述</th>
                    <th width="110">状态</th>
                    <th width="90">照片</th>
                    <th width="120">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="order in allOrders" :key="order.id">
                    <td class="bold">{{ order.orderNo }}</td>
                    <td><el-tag size="mini" type="info">{{ order.userId }}</el-tag></td>
                    <td><el-tag size="mini" effect="dark">{{ order.fixType }}</el-tag></td>
                    <td class="desc-text" :title="order.description">{{ order.description }}</td>
                    <td>
                      <span :class="['status-label', order.status ? order.status.toLowerCase() : '']">
                        {{ order.statusText || order.status }}
                      </span>
                    </td>
                    <td>
                      <el-popover v-if="order.imageData" placement="left" trigger="click">
                        <img :src="'data:image/jpeg;base64,' + order.imageData" class="preview-img" />
                        <el-button slot="reference" type="text" icon="el-icon-picture">查看</el-button>
                      </el-popover>
                      <span v-else class="no-data">无</span>
                    </td>
                    <td>
                      <el-button type="primary" size="mini" icon="el-icon-edit" circle @click="handleEdit(order)"></el-button>
                      <el-button type="danger" size="mini" icon="el-icon-delete" circle @click="handleDelete(order)"></el-button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-card>
        </div>

        <div v-if="activeMenu === 'password'" key="pwd">
          <el-card shadow="never" style="max-width: 500px; margin: 0 auto;">
            <div slot="header"><span>修改管理密码</span></div>
            <el-form label-width="100px" size="small">
              <el-form-item label="原密码">
                <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitChangePwd">验证并修改</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </transition>
    </el-main>

    <el-dialog title="处理报修单" :visible.sync="editDialogVisible" width="400px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="订单号"><el-input v-model="editForm.orderNo" disabled></el-input></el-form-item>
        <el-form-item label="处理进度">
          <el-select v-model="editForm.status" style="width:100%">
            <el-option label="待处理" value="PENDING"></el-option>
            <el-option label="维修中" value="PROCESSING"></el-option>
            <el-option label="已完成" value="COMPELITED"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatusChange">更新状态</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      activeMenu: 'orders',
      adminAccount: 'Admin',
      allOrders: [],
      editDialogVisible: false,
      editForm: { orderNo: '', status: '' },
      pwdForm: { oldPassword: '', newPassword: '' }
    };
  },
  created() { this.fetchAllOrders(); },
  methods: {
    handleMenuSelect(val) {
      if (val === 'exit') {
        this.handleLogout();
      } else {
        this.activeMenu = val;
      }
    },
    handleLogout() {
      this.$confirm('确定退出管理系统？', '提示', { type: 'warning' }).then(() => {
        localStorage.clear();
        this.$router.push('/login');
        this.$message.success('已退出');
      }).catch(() => {});
    },
    async submitChangePwd() {
      if(!this.pwdForm.oldPassword || !this.pwdForm.newPassword) {
        return this.$message.warning('请输入完整密码信息');
      }
      try {
        const userId = localStorage.getItem('userId');
        const token = localStorage.getItem('token');
        
        // 按照你的要求：JSON 包含 id, oldPassword, newPassword
        const submitData = {
          id: userId,
          oldPwd: this.pwdForm.oldPassword,
          newPwd: this.pwdForm.newPassword
        };

        await axios.put(`/users/private/${userId}`, submitData, {
          headers: { 'Authorization': 'Bearer ' + token }
        });
        
        this.$message.success('密码修改成功，请重新登录');
        localStorage.clear();
        this.$router.push('/login');
      } catch (err) { 
        this.$message.error(err.response?.data || '原密码校验失败'); 
      }
    },
    async fetchAllOrders() {
      try {
        const token = localStorage.getItem('token');
        const res = await axios.get('/orders', { headers: { 'Authorization': 'Bearer ' + token } });
        this.allOrders = Array.isArray(res.data) ? res.data : [];
      } catch (err) { this.$message.error('获取列表失败'); }
    },
    handleEdit(order) {
      this.editForm.orderNo = order.orderNo;
      this.editForm.status = order.status ? order.status.toUpperCase() : 'PENDING';
      this.editDialogVisible = true;
    },
    async submitStatusChange() {
      try {
        const token = localStorage.getItem('token');
        await axios.put(`/orders/${this.editForm.orderNo}`, { status: this.editForm.status }, {
          headers: { 'Authorization': 'Bearer ' + token }
        });
        this.$message.success('状态更新成功');
        this.editDialogVisible = false;
        this.fetchAllOrders();
      } catch (err) { this.$message.error('更新失败'); }
    },
    async handleDelete(order) {
      try {
        await this.$confirm('确定永久删除该报修单？', '警告', { type: 'error' });
        const token = localStorage.getItem('token');
        await axios.delete(`/orders/${order.orderNo}`, { headers: { 'Authorization': 'Bearer ' + token } });
        this.$message.success('删除成功');
        this.fetchAllOrders();
      } catch (err) {}
    }
  }
};
</script>

<style scoped>
.main-holder { height: 100vh; background: #fdfdfd; }
.aside-bar { background: #fff; border-right: 1px solid #eee; padding: 40px 20px; display: flex; flex-direction: column; }
.user-card { text-align: center; margin-bottom: 30px; }
.user-card h3 { margin: 15px 0 5px; color: #333; }
.info-list { 
  text-align: left; margin-top: 25px; font-size: 13px; color: #666; 
  padding: 15px; background: #f8fbff; border-radius: 8px;
}
.info-list p { margin: 8px 0; }
.info-list i { margin-right: 8px; color: #409EFF; }
.menu-list { border: none; }

.content-box { background: #f9f9f9; padding: 30px; }
.admin-card { border: none; border-radius: 8px; }
.header-box { display: flex; justify-content: space-between; align-items: center; }
.title { font-weight: bold; color: #409EFF; }

.table-wrapper { margin-top: 10px; }
.admin-table { width: 100%; border-collapse: collapse; background: #fff; }
.admin-table th, .admin-table td { padding: 12px; border-bottom: 1px solid #f0f0f0; text-align: left; font-size: 13px; }
.admin-table th { background: #f5f7fa; color: #909399; }

.status-label { padding: 2px 8px; border-radius: 4px; font-size: 11px; }
.status-label.pending { background: #fdf6ec; color: #e6a23c; border: 1px solid #f5dab1; }
.status-label.processing { background: #ecf5ff; color: #409eff; border: 1px solid #d9ecff; }
.status-label.compelited { background: #f0f9eb; color: #67c23a; border: 1px solid #e1f3d8; }

.desc-text { color: #666; max-width: 250px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.preview-img { max-width: 400px; border-radius: 4px; }
.bold { font-weight: bold; color: #303133; }
.no-data { color: #ccc; }

/* 切换动画 */
.fade-transform-enter-active, .fade-transform-leave-active { transition: all .3s; }
.fade-transform-enter { opacity: 0; transform: translateX(-10px); }
.fade-transform-leave-to { opacity: 0; transform: translateX(10px); }
</style>