import request from '@/utils/request';

/**
 * 用户相关接口
 */
// 登录
export const login = (data) => request.post('/users/public/login', data);

// 注册
export const register = (data) => request.post('/users/public', data);

// 获取个人详细信息 (用于侧边栏和设置页)
export const getUserInfo = (id) => request.get(`/users/private/${id}`);

// 绑定/更新宿舍信息
export const bindDorm = (id, data) => request.put(`/users/private/${id}/dorm`, data);


/**
 * 报修单相关接口
 */
// 提交报修单 (支持图片上传，接收 FormData)
export const createOrder = (id, formData) => {
  return request.post(`/orders/${id}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

// 获取当前学生的所有报修记录
export const getMyOrders = (id) => {
  // 核心点：必须 return 这个 request 请求，外部才能拿到 Promise 数据
  return request.get(`/orders/user/${id}`);
};

// 学生取消报修
export const cancelOrder = (orderNo) => {
  return request.put(`/orders/${orderNo}/status`);
};


/**
 * 管理员专用接口
 */
// 获取所有报修单
export const getAllOrders = () => request.get('/orders');

// 管理员更新报修单状态/指派
export const updateStatus = (orderNo, data) => request.put(`/orders/${orderNo}`, data);

// 删除报修单
export const deleteOrder = (orderNo) => request.delete(`/orders/${orderNo}`);