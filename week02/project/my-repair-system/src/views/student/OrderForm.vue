<template>
  <el-card shadow="never" header="发起维修申请" class="order-form-card">
    <el-form label-position="top" :model="form" ref="orderForm">
      <el-form-item label="维修类型" prop="fixType" required>
        <el-input v-model="form.fixType" placeholder="例如：空调不制冷" />
      </el-form-item>
      
      <el-form-item label="紧急程度" prop="priority">
        <el-select v-model="form.priority" style="width:100%">
          <el-option label="💥 紧急" value="HIGH" />
          <el-option label="📅 普通" value="MEDIUM" />
          <el-option label="📝 较低" value="LOW" />
        </el-select>
      </el-form-item>

      <el-form-item label="详细描述" prop="description" required>
        <el-input type="textarea" v-model="form.description" rows="4" placeholder="请详细描述故障情况，以便维修人员准备工具" />
      </el-form-item>

      <el-form-item label="现场照片 (可选，限1张，10MB内)">
        <el-upload
          action="#"
          list-type="picture-card"
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :limit="1"
          ref="uploadRef"
          :class="{ hide: fileList.length >= 1 }"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
      </el-form-item>

      <div class="form-footer">
        <el-button type="primary" @click="submit" style="width:200px" :loading="loading" icon="el-icon-check">确认提交报修</el-button>
        <el-button @click="resetForm" size="small">重置</el-button>
      </div>
    </el-form>
  </el-card>
</template>

<script>
// 这里直接引入原生 axios，绕过可能会吞数据的拦截器，保证上传成功
import axios from 'axios';

export default {
  data() {
    return {
      form: {
        fixType: '',
        priority: 'MEDIUM',
        description: ''
      },
      fileList: [], // 用于控制上传组件显示
      imgFile: null, // 真正的文件对象
      loading: false
    };
  },
  methods: {
    // 处理文件选择
    handleFileChange(file) {
      this.imgFile = file.raw;
      this.fileList.push(file);
      
      // 特判：防bug，限制大小
      if (file.size > 10 * 1024 * 1024) {
        this.$message.error('图片大小不得大于10MB');
        this.resetUpload();
      }
    },
    // 处理文件移除
    handleFileRemove() {
      this.resetUpload();
    },
    resetUpload() {
      this.imgFile = null;
      this.fileList = [];
      this.$refs.uploadRef.clearFiles();
    },
    resetForm() {
      this.$refs.orderForm.resetFields();
      this.resetUpload();
    },

    // 核心提交逻辑（手术级修改）
    async submit() {
      // 1. 基础校验
      if (!this.form.fixType || !this.form.description) {
        this.$message.warning('请填写完整的报修类型和描述');
        return;
      }

      this.loading = true;
      try {
        const userId = localStorage.getItem('userId');
        const token = localStorage.getItem('token');
        
        // 2. 创建 FormData
        const fd = new FormData();

        // --- ★★★ 核心修复点 ★★★ ---
        // 将 JSON 对象转为 Blob，并显式指定 Content-Type 为 'application/json'
        // 这样后端才能正确将该部分映射为 CreateOrderDto 对象
        const jsonBlob = new Blob([JSON.stringify(this.form)], { type: 'application/json' });
        fd.append('order', jsonBlob);
        
        // 3. 添加图片 (如果存在)
        if (this.imgFile) {
          fd.append('image', this.imgFile); // 名必须是 'image'，与后端 @RequestPart("image") 匹配
        }

        // 4. 使用原生 Axios 发起 Multipart 请求
        await axios.post(`/orders/${userId}`, fd, {
          headers: {
            'Authorization': 'Bearer ' + token,
            // 浏览器会自动设置 Content-Type 为 multipart/form-data 并带上 boundary
            // 所以这里不需要手动设置 Content-Type
          }
        });

        this.$message.success('报修单提交成功！');
        this.resetForm(); // 提交后重置表单

      } catch (error) {
        // 处理后端返回的“图片大于10MB”等 NOT_ACCEPTABLE 错误
        if (error.response && error.response.status === 406) {
           this.$message.error(error.response.data || '图片大小或格式不符');
        } else {
           this.$message.error('提交失败，请检查网络或后端服务');
        }
        console.error(error);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.order-form-card {
  margin-bottom: 20px;
}
.form-footer {
  margin-top: 30px;
  text-align: center;
}
/* 限制只传一张图后，隐藏加号 */
.hide >>> .el-upload--picture-card {
  display: none;
}
</style>