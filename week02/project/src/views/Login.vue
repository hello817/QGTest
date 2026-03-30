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
          <el-input v-model="form.account" placeholder="请输入学号/工号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item v-if="mode === 'reg'" label="身份">
          <el-radio-group v-model="form.role">
            <el-radio label="STUDENT">学生</el-radio>
            <el-radio label="ADMIN">管理员</el-radio>
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
  data() { return { mode: 'login', form: { account: '', password: '', role: 'STUDENT' } } },
  methods: {
    async submit() {
      const url = this.mode === 'login' ? '/users/public/login' : '/users/public';
      const res = await request.post(url, this.form);
      if (this.mode === 'login') {
        localStorage.setItem('token', res.token);
        localStorage.setItem('userId', res.id);
        this.$router.push(res.role === 'ADMIN' ? '/admin' : '/student');
      } else {
        this.$message.success('注册成功');
        this.mode = 'login';
      }
    }
  }
}
</script>

<style scoped>
.login-page { height: 100vh; display: flex; align-items: center; justify-content: center; background: #FFF5EB; }
.auth-card { width: 380px; border-radius: 12px; }
</style>