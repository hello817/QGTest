<template>
  <div class="container">
    <el-header class="nav">
      <span class="logo">管理后台 - 全部报修单</span>
      <el-button type="text" @click="$router.push('/login')">退出</el-button>
    </el-header>

    <div style="padding: 20px">
      <div style="margin-bottom: 20px">
        <el-radio-group v-model="filterStatus" size="small" @change="loadAll">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="PENDING">待处理</el-radio-button>
          <el-radio-button label="PROCESSING">处理中</el-radio-button>
          <el-radio-button label="COMPELITED">已完成</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="orders" border>
        <el-table-column prop="orderNo" label="单号" width="150" />
        <el-table-column label="报修人">
          <template slot-scope="s">{{ s.row.student.account }} ({{ s.row.student.building }}-{{ s.row.student.roomNumber }})</template>
        </el-table-column>
        <el-table-column prop="fixType" label="类型" />
        <el-table-column prop="priorityText" label="优先级" />
        <el-table-column label="状态">
          <template slot-scope="s"><el-tag>{{ s.row.statusText }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="s">
            <el-button type="text" @click="openUpdate(s.row)">修改状态</el-button>
            <el-button type="text" style="color: red" @click="del(s.row.orderNo)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog title="更新进度" :visible.sync="showUpdate" width="300px">
      <el-select v-model="newStatus" style="width: 100%">
        <el-option label="待处理" value="PENDING" />
        <el-option label="处理中" value="PROCESSING" />
        <el-option label="已完成" value="COMPELITED" />
      </el-select>
      <span slot="footer">
        <el-button @click="update">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';
export default {
  data() { return { orders: [], filterStatus: '', showUpdate: false, currNo: '', newStatus: '' }},
  created() { this.loadAll(); },
  methods: {
    async loadAll() {
      const data = await request.get('/orders');
      this.orders = this.filterStatus ? data.filter(o => o.status === this.filterStatus) : data;
    },
    openUpdate(row) { this.currNo = row.orderNo; this.newStatus = row.status; this.showUpdate = true; },
    async update() {
      await request.put(`/orders/${this.currNo}`, { status: this.newStatus });
      this.showUpdate = false;
      this.loadAll();
    },
    async del(no) {
      await request.delete(`/orders/${no}`);
      this.loadAll();
    }
  }
}
</script>
<style scoped> .nav { background: #fff; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #eee; } .logo { font-weight: bold; color: #FF8C00; } </style>