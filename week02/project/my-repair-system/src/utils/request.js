import axios from 'axios';
import { Message } from 'element-ui';

const service = axios.create({
  baseURL: '/',
  timeout: 5000
});

service.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers['Authorization'] = token;
  return config;
});

service.interceptors.response.use(
  res => res.data,
  err => {
    Message.error(err.response?.data?.message || '服务器错误');
    return Promise.reject(err);
  }
);

export default service;