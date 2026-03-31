<template>
  <div class="login-page">
    <el-card class="auth-card">
      <h2 style="color: #FF8C00; text-align: center">宿舍报修系统</h2>
      <el-tabs v-model="mode" stretch>
        <el-tab-pane label="登录" name="login" />
        <el-tab-pane label="注册" name="reg" />
      </el-tabs>
      <el-form :model="form" label-position="top" style="margin-top: 20px">
        <el-form-item label="账号">
          <el-input v-model="form.account" placeholder="请输入学�?/工号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item v-if="mode === 'reg'" label="身份">
          <el-radio-group v-model="form.role">
            <el-radio label="STUDENT">学生</el-radio>
            <el-radio label="ADMIN">管理�?</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="submit">{{ mode === 'login' ? '进入系统' : '立即注册' }}</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request';

export default {
  data() {
    return {
      mode: 'login',
      form: {
        account: '',
        password: '',
        role: 'STUDENT' // 注册时使用的默认值
      }
    };
  },
  methods: {
    async submit() {
      try {
        // 登录时只发送账号密码，注册时发送整个 form
        const submitData = this.mode === 'login' 
          ? { account: this.form.account, password: this.form.password } 
          : this.form;

        const url = this.mode === 'login' ? '/users/public/login' : '/users/public';
        const res = await request.post(url, submitData);

        if (this.mode === 'login') {
          // --- 核心修复点：处理后端返回的小写 role ---
          // 将 "admin" 转为 "ADMIN"，将 "student" 转为 "STUDENT"
          const userRole = res.role ? res.role.toUpperCase() : 'STUDENT';

          localStorage.setItem('token', res.token);
          localStorage.setItem('userId', res.id);
          localStorage.setItem('role', userRole);

          this.$message.success('登录成功');

          // 根据转换后的角色进行跳转
          if (userRole === 'ADMIN') {
            this.$router.push('/admin').catch(err => {});
          } else {
            this.$router.push('/student').catch(err => {});
          }
        } else {
          this.$message.success('注册成功，请登录');
          this.mode = 'login';
        }
      } catch (error) {
        this.$message.error(error.response?.data || '操作失败');
      }
    }
  }
};
</script>

<style scoped>
.login-page { height: 100vh; display: flex; align-items: center; justify-content: center; background: #FFF5EB; }
.auth-card { width: 380px; border-radius: 12px; }
</style>