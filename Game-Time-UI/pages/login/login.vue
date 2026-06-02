<template>
  <view class="login-page">
    <!-- 顶部装饰背景 -->
    <view class="top-bg">
      <view class="bg-circle bg-circle-1"></view>
      <view class="bg-circle bg-circle-2"></view>
      <view class="bg-circle bg-circle-3"></view>
    </view>

    <!-- Logo/标题区域 -->
    <view class="brand-area">
      <view class="brand-icon">🎮</view>
      <view class="brand-title">Game Time</view>
      <view class="brand-desc">游戏预约课表系统</view>
    </view>

    <!-- 登录卡片 -->
    <view class="login-card">
      <view class="card-title">欢迎回来</view>
      <view class="card-subtitle">请登录你的账号</view>

      <!-- 账号输入 -->
      <view class="input-group">
        <view class="input-icon">👤</view>
        <input
          class="input-field"
          v-model="username"
          placeholder="请输入账号"
          placeholder-class="placeholder-style"
          @focus="focusField = 'username'"
          @blur="focusField = ''"
        />
      </view>

      <!-- 密码输入 -->
      <view class="input-group">
        <view class="input-icon">🔒</view>
        <input
          class="input-field"
          v-model="password"
          placeholder="请输入密码"
          placeholder-class="placeholder-style"
          :password="!showPassword"
          @focus="focusField = 'password'"
          @blur="focusField = ''"
        />
        <view class="toggle-pwd" @click="showPassword = !showPassword">
          <text>{{ showPassword ? '🙈' : '👁️' }}</text>
        </view>
      </view>

      <!-- 记住密码 + 错误提示 -->
      <view class="options-row">
        <view class="remember" @click="isRemember = !isRemember">
          <view class="checkbox" :class="{ checked: isRemember }">
            <text v-if="isRemember" class="checkmark">✓</text>
          </view>
          <text class="remember-text">记住密码</text>
        </view>
        <text class="error-text" v-if="errorMsg">{{ errorMsg }}</text>
      </view>

      <!-- 登录按钮 -->
      <button class="login-btn" :class="{ loading: isLoading }" :disabled="isLoading" @click="login">
        <text class="btn-text" v-if="!isLoading">登 录</text>
        <text class="btn-loading" v-else>登录中...</text>
      </button>

      <!-- 注册入口 -->
      <view class="register-row">
        <text class="register-text">还没有账号？</text>
        <text class="register-link" @tap="goRegister">立即注册</text>
      </view>
    </view>
  </view>
</template>

<script>
import { loginAPI } from "@/api";

export default {
  data() {
    return {
      username: "",
      password: "",
      isRemember: false,
      showPassword: false,
      isLoading: false,
      errorMsg: "",
      focusField: ""
    };
  },
  onShow() {
    // 页面打开读取本地缓存（记住密码）
    let saved = uni.getStorageSync("savedAccount");
    if (saved) {
      this.username = saved.username || "";
      this.password = saved.password || "";
      this.isRemember = true;
    }
    this.errorMsg = "";
  },
  methods: {
    async login() {
      // 清空错误
      this.errorMsg = "";

      // 表单校验
      if (!this.username.trim()) {
        this.errorMsg = "请输入账号";
        return;
      }
      if (!this.password) {
        this.errorMsg = "请输入密码";
        return;
      }

      this.isLoading = true;
      try {
        const res = await loginAPI(this.username, this.password);
        // res 已经是 response.data (由拦截器处理)
        // 后端返回: { success: true, data: { userId, username, email, phone, avatar } }
        if (res && (res.success || res.code === 200)) {
          const userData = res.data || res;
          uni.showToast({ title: "登录成功", icon: "success" });

          // 保存登录态
          uni.setStorageSync("userInfo", {
            userId: userData.userId || userData.id,
            username: userData.username,
            email: userData.email || "",
            phone: userData.phone || "",
            avatar: userData.avatar || ""
          });

          // 记住密码
          if (this.isRemember) {
            uni.setStorageSync("savedAccount", {
              username: this.username,
              password: this.password
            });
          } else {
            uni.removeStorageSync("savedAccount");
          }

          // 延迟跳转，让 toast 展示
          setTimeout(() => {
            uni.switchTab({ url: "/pages/index/index" });
          }, 500);
        } else {
          this.errorMsg = res?.message || "账号或密码错误";
        }
      } catch (e) {
        const errMsg = e?.response?.data?.message || e?.message || "";
        this.errorMsg = errMsg || "网络请求失败，请检查网络连接";
      } finally {
        this.isLoading = false;
      }
    },
    goRegister() {
      uni.navigateTo({ url: "/pages/register/register" });
    }
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 60rpx;
}

/* ===== 顶部装饰背景 ===== */
.top-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 500rpx;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}
.bg-circle {
  position: absolute;
  border-radius: 50%;
}
.bg-circle-1 {
  width: 600rpx;
  height: 600rpx;
  background: linear-gradient(135deg, #007aff, #5856d6);
  top: -250rpx;
  right: -150rpx;
  opacity: 0.12;
}
.bg-circle-2 {
  width: 350rpx;
  height: 350rpx;
  background: linear-gradient(135deg, #34c759, #30d158);
  top: -80rpx;
  left: -100rpx;
  opacity: 0.08;
}
.bg-circle-3 {
  width: 200rpx;
  height: 200rpx;
  background: linear-gradient(135deg, #ff9500, #ffcc00);
  top: 150rpx;
  left: 55%;
  opacity: 0.06;
}

/* ===== 品牌区域 ===== */
.brand-area {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 0 40rpx;
}
.brand-icon {
  font-size: 80rpx;
  margin-bottom: 16rpx;
  animation: floatIcon 3s ease-in-out infinite;
}
@keyframes floatIcon {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12rpx); }
}
.brand-title {
  font-size: 44rpx;
  font-weight: 800;
  background: linear-gradient(135deg, #007aff, #5856d6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 2rpx;
}
.brand-desc {
  font-size: 24rpx;
  color: #8e98a5;
  margin-top: 8rpx;
}

/* ===== 登录卡片 ===== */
.login-card {
  position: relative;
  z-index: 1;
  background: #ffffff;
  border-radius: 32rpx;
  margin: 0 32rpx;
  padding: 48rpx 36rpx;
  box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.06);
}
.card-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8rpx;
}
.card-subtitle {
  font-size: 26rpx;
  color: #8e98a5;
  margin-bottom: 40rpx;
}

/* ===== 输入框 ===== */
.input-group {
  display: flex;
  align-items: center;
  background: #f7f8fa;
  border-radius: 16rpx;
  padding: 0 24rpx;
  margin-bottom: 24rpx;
  border: 2rpx solid transparent;
  transition: border-color 0.2s, background 0.2s;
}
.input-group:focus-within {
  border-color: #007aff;
  background: #f0f4ff;
}
.input-icon {
  font-size: 32rpx;
  margin-right: 16rpx;
  flex-shrink: 0;
}
.input-field {
  flex: 1;
  height: 88rpx;
  font-size: 28rpx;
  color: #1a1a2e;
  background: transparent;
  border: none;
  outline: none;
}
.placeholder-style {
  color: #c0c8d4;
  font-size: 28rpx;
}
.toggle-pwd {
  flex-shrink: 0;
  padding: 12rpx;
  font-size: 30rpx;
}

/* ===== 选项行 ===== */
.options-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
  min-height: 48rpx;
}
.remember {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.checkbox {
  width: 36rpx;
  height: 36rpx;
  border-radius: 8rpx;
  border: 2rpx solid #d0d5dd;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}
.checkbox.checked {
  background: #007aff;
  border-color: #007aff;
}
.checkmark {
  color: #fff;
  font-size: 24rpx;
  font-weight: bold;
}
.remember-text {
  font-size: 26rpx;
  color: #6b7a8f;
}
.error-text {
  font-size: 24rpx;
  color: #ff3b30;
  max-width: 50%;
  text-align: right;
}

/* ===== 登录按钮 ===== */
.login-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: linear-gradient(135deg, #007aff, #5856d6);
  color: #fff;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: 700;
  letter-spacing: 4rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 122, 255, 0.3);
  transition: all 0.2s;
  border: none;
  padding: 0;
}
.login-btn:active {
  transform: scale(0.97);
  opacity: 0.9;
}
.login-btn.loading {
  background: linear-gradient(135deg, #80b8ff, #a09be0);
  box-shadow: none;
}
.login-btn[disabled] {
  opacity: 1;
}
.btn-text {
  color: #fff;
}
.btn-loading {
  color: rgba(255,255,255,0.85);
  font-size: 28rpx;
}

/* ===== 注册入口 ===== */
.register-row {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 36rpx;
  gap: 8rpx;
}
.register-text {
  font-size: 26rpx;
  color: #8e98a5;
}
.register-link {
  font-size: 26rpx;
  color: #007aff;
  font-weight: 600;
  text-decoration: underline;
  text-underline-offset: 4rpx;
}
.register-link:active {
  opacity: 0.7;
}
</style>
