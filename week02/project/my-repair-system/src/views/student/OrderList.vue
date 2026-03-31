<template>
  <div class="order-list-container">
    <div class="header-section">
      <h3><i class="el-icon-tickets"></i> 我的報修記錄</h3>
      <el-button type="primary" size="small" icon="el-icon-refresh" @click="fetchList">刷新數據</el-button>
    </div>

    <div class="cards-wrapper" v-if="list.length > 0">
      <div class="order-card" v-for="item in list" :key="item.id">
        
        <div class="card-image">
          <img v-if="item.imageData" :src="'data:image/jpeg;base64,' + item.imageData" alt="報修圖片" />
          <div v-else class="no-image">
            <i class="el-icon-picture-outline"></i>
            <span>暫無現場照片</span>
          </div>
          <span :class="['status-badge', item.status ? item.status.toLowerCase() : '']">
            {{ item.statusText || item.status }}
          </span>
        </div>

        <div class="card-content">
          <div class="card-title">
            <el-tag size="mini" :type="priorityColor(item.priority)">{{ item.priorityText || '普通' }}</el-tag>
            <span class="fix-type">{{ item.fixType }}</span>
          </div>
          <p class="description">{{ item.description }}</p>
          
          <div class="card-footer">
            <span class="order-no"><i class="el-icon-document"></i> {{ item.orderNo }}</span>
            <span class="time">{{ formatDate(item.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <i class="el-icon-folder-opened"></i>
      <p>暫無報修記錄，生活如此美好～</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'OrderList',
  data() {
    return {
      list: []
    };
  },
  created() {
    this.fetchList();
  },
  methods: {
    async fetchList() {
      try {
        const userId = localStorage.getItem('userId');
        const token = localStorage.getItem('token');
        const response = await axios.get(`/orders/user/${userId}`, {
          headers: { 'Authorization': 'Bearer ' + token }
        });
        this.list = Array.isArray(response.data) ? response.data : [];
      } catch (error) {
        this.$message.error('獲取列表失敗');
      }
    },
    priorityColor(p) {
      if (!p) return 'info';
      const s = p.toLowerCase();
      const map = { 'high': 'danger', 'medium': 'warning', 'low': 'info' };
      return map[s] || 'info';
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return date.toLocaleString();
    }
  }
};
</script>

<style scoped>
.order-list-container {
  padding: 20px;
}
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header-section h3 {
  margin: 0;
  color: #303133;
}

/* 卡片網格佈局 */
.cards-wrapper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

/* 單個卡片樣式 */
.order-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}
.order-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.1);
}

/* 圖片區域 */
.card-image {
  height: 160px;
  background: #f5f7fa;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}
.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.no-image {
  color: #909399;
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 14px;
}
.no-image i {
  font-size: 30px;
  margin-bottom: 8px;
}

/* 懸浮標籤 */
.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 12px;
  color: #fff;
  background: #909399;
}
.status-badge.pending { background: #E6A23C; }
.status-badge.processing { background: #409EFF; }
.status-badge.compelited { background: #67C23A; }
.status-badge.cancelled { background: #F56C6C; }

/* 內容區域 */
.card-content {
  padding: 15px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}
.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.fix-type {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
}
.description {
  font-size: 14px;
  color: #606266;
  margin: 0 0 15px 0;
  line-height: 1.5;
  height: 42px; /* 限制兩行文字 */
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 底部 */
.card-footer {
  margin-top: auto;
  border-top: 1px solid #ebeef5;
  padding-top: 10px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}
.order-no {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 空數據狀態 */
.empty-state {
  text-align: center;
  color: #909399;
  padding: 50px 0;
}
.empty-state i {
  font-size: 50px;
  margin-bottom: 10px;
}
</style>