<template>
  <div class="setting-page">
    <transition name="el-zoom-in-center" mode="out-in">
      <el-card v-if="!success" key="form">
        <div slot="header" class="card-header">
          <span><i class="el-icon-setting"></i> 个人中心</span>
        </div>

        <div class="section-title">宿舍信息</div>
        <el-form label-width="90px" size="small">
          <el-form-item label="楼栋号">
            <el-input v-model="dorm.building" placeholder="例如：15号楼" />
          </el-form-item>
          <el-form-item label="房号">
            <el-input v-model="dorm.roomNo" placeholder="例如：502" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-check" @click="saveDorm">保存并更新宿舍</el-button>
          </el-form-item>
        </el-form>

        <el-divider></el-divider>

        <div class="section-title">安全设置 (修改密码)</div>
        <el-form label-width="90px" size="small">
          <el-form-item label="原密码">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="验证原密码" />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="设置新密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="warning" icon="el-icon-key" @click="savePassword">验证并修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card v-else key="done" class="anim-box">
        <el-result icon="success" :title="resultTitle" subTitle="操作已成功同步">
          <template slot="extra">
            <el-button type="primary" size="medium" @click="success = false">返回修改</el-button>
          </template>
        </el-result>
      </el-card>
    </transition>
  </div>
</template>

<script>
import { bindDorm } from '@/api/repair';
import axios from 'axios';

export default {
  data() {
    return {
      dorm: { building: '', roomNo: '' },
      pwdForm: {
        oldPassword: '',
        newPassword: ''
      },
      success: false,
      resultTitle: '信息已同步'
    }
  },
  methods: {
    async saveDorm() {
      if (!this.dorm.building || !this.dorm.roomNo) return this.$message.warning('请填写完整');
      try {
        await bindDorm(localStorage.getItem('userId'), this.dorm);
        this.resultTitle = '宿舍信息已更新';
        this.success = true;
        this.$emit('refresh'); 
      } catch (error) { this.$message.error('更新失败'); }
    },

    // 修改密码逻辑：JSON 包含 ID、旧密码、新密码
    async savePassword() {
      if (!this.pwdForm.oldPassword || !this.pwdForm.newPassword) {
        return this.$message.warning('请输入完整密码信息');
      }
      
      try {
        const userId = localStorage.getItem('userId');
        const token = localStorage.getItem('token');
        
        // 关键改动：构建包含 ID 的提交数据
        const submitData = {
          id: userId, // 显式传递用户ID
          oldPwd: this.pwdForm.oldPassword,
          newPwd: this.pwdForm.newPassword
        };

        // 发送 PUT 请求
        // 如果你的接口不需要在 URL 传 id，可以直接 axios.put('/users/password', submitData, ...)
        await axios.put(`/users/private/${userId}`, submitData, {
          headers: { 'Authorization': 'Bearer ' + token }
        });

        this.$message.success('密码修改成功，请重新登录');
        
        setTimeout(() => {
          localStorage.clear();
          this.$router.push('/login');
        }, 1500);
        
      } catch (error) {
        const msg = error.response?.data || '原密码错误或修改失败';
        this.$message.error(msg);
      }
    }
  }
}
</script>

<style scoped>
/* 样式同前，保持橙色侧边装饰 */
.setting-page { max-width: 600px; margin: 0 auto; }
.section-title {
  font-size: 14px; font-weight: bold; color: #666;
  margin-bottom: 20px; padding-left: 10px; border-left: 4px solid #FF8C00;
}
.anim-box { text-align: center; padding: 50px 0; border: none; }
</style>