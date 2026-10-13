import axios from 'axios'
import { showToast } from 'vant';

const service = axios.create({
  baseURL: '/api',
  timeout: 60000
});

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data;
    if (res.code !== 200) {
      showToast(res.message || '请求失败');
      return Promise.reject(new Error(res.message));
    }
    return res;
  },
  error => {
    console.log('err', error.message);
    if (error.code === 'ERR_NETWORK') {
      showToast('无法连接到服务器，请确认后端已启动');
    } else if (error.code === 'ECONNABORTED') {
      showToast('请求超时，请稍后重试');
    } else {
      showToast('网络错误，请稍后重试');
    }
    return Promise.reject(error);
  }
);

export default service;
