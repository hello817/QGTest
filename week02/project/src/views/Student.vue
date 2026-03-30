<template>
  <div class="container">
    <el-header class="nav">
      <span class="logo">我的报修服务</span>
      <el-button type="text" @click="$router.push('/login')">退出</el-button>
    </el-header>

    <el-tabs v-model="activeTab" style="padding: 20px">
      <el-tab-pane label="发起报修" name="add">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card header="1. 完善宿舍信息">
              <el-form label-position="top">
                <el-form-item label="宿舍楼"><el-input v-model="dorm.building" /></el-form-item>
                <el-form-item label="房间号"><el-input v-model="dorm.roomNo" /></el-form-item>
                <el-button size="small" @click="saveDorm">保存信息</el-button>
              </el-form>
            </el-card>
          </el-col>
          <el-col :span="16">
            <el-card header="2. 填写报修单">
              <el-form :model="order" label-width="80px">
                <el-form-item label="报修类型"><el-input v-model="order.fixType" placeholder="例: 灯具/水管" /></el-form-item>
                <el-form-item label="优先级">
                  <el-select v-model="order.priority" style="width: 100%">
                    <el-option label="高" value="HIGH" />
                    <el-option label="中" value="MEDIUM" />
                    <el-option label="低" value="LOW" />
                  </el-select>
                </el-form-item>
                <el-form-item label="详情描述"><el-input type="textarea" v-model="order.description" /></el-form-item>
                <el-button type="primary" @click="postOrder">提交报修单</el-button>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="报修记录" name="list">
        <el-table :data="list" stripe>
          <el-table-column prop="orderNo" label="单号" />
          <el-table-column prop="fixType" label="类型" />
          <el-table-column prop="statusText" label="状态">
            <template slot-scope="s"><el-tag size="mini">{{ s.row.statusText }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作">
            <template slot-scope="s">
              <el-button v-if="s.row.status !== 'CANCELLED'" type="text" @click="cancel(s.row.orderNo)">取消</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import request from '@/utils/request';
export default {
  data() { return { 
    activeTab: 'add', 
    dorm: { building: '', roomNo: '' },
    order: { fixType: '', priority: 'MEDIUM', description: '' },
    list: [] 
  }},
  created() { this.loadList(); },
  methods: {
    async saveDorm() {
      await request.put(`/users/private/${localStorage.getItem('userId')}/dorm`, this.dorm);
      this.$message.success('更新成功');
    },
    async postOrder() {
      await request.post(`/orders/${localStorage.getItem('userId')}`, { order: this.order });
      this.$message.success('提交成功');
      this.activeTab = 'list';
      this.loadList();
    },
    async loadList() {
      this.list = await request.get(`/orders/user/${localStorage.getItem('userId')}`);
    },
    async cancel(no) {
      await request.put(`/orders/${no}/status`);
      this.loadList();
    }
  }
}
</script>
<style scoped> .nav { background: #fff; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #eee; } .logo { font-weight: bold; color: #FF8C00; } </style>