import axios from "axios";
import API_BASE_URL from "./config";

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json"
  }
});

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 直接返回 data，页面使用更简洁
    return response.data;
  },
  (error) => {
    // 统一错误处理
    const message = error.response?.data?.message || "网络请求失败";
    console.error("API Error:", message);
    return Promise.reject(error);
  }
);

export default request;
