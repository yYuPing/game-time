<template>
  <view class="register-page">
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
      <view class="brand-desc">创建新账号</view>
    </view>

    <!-- 注册卡片 -->
    <view class="register-card">
      <view class="card-title">创建账号</view>
      <view class="card-subtitle">注册后即可预约游戏</view>

      <!-- 账号输入 -->
      <view class="input-group">
        <view class="input-icon">👤</view>
        <input
          class="input-field"
          v-model="username"
          placeholder="请设置账号"
          placeholder-class="placeholder-style"
          maxlength="20"
        />
      </view>

      <!-- 密码输入 -->
      <view class="input-group">
        <view class="input-icon">🔒</view>
        <input
          class="input-field"
          v-model="password"
          placeholder="请设置密码（至少6位）"
          placeholder-class="placeholder-style"
          :password="!showPassword"
          maxlength="32"
        />
        <view class="toggle-pwd" @click="showPassword = !showPassword">
          <text>{{ showPassword ? '🙈' : '👁️' }}</text>
        </view>
      </view>

      <!-- 确认密码 -->
      <view class="input-group">
        <view class="input-icon">🔐</view>
        <input
          class="input-field"
          v-model="confirmPassword"
          placeholder="请确认密码"
          placeholder-class="placeholder-style"
          :password="!showConfirmPwd"
          maxlength="32"
        />
        <view class="toggle-pwd" @click="showConfirmPwd = !showConfirmPwd">
          <text>{{ showConfirmPwd ? '🙈' : '👁️' }}</text>
        </view>
      </view>

      <!-- 邮箱输入 -->
      <view class="input-group">
        <view class="input-icon">✉️</view>
        <input
          class="input-field"
          v-model="email"
          placeholder="请输入邮箱（选填）"
          placeholder-class="placeholder-style"
          type="text"
          maxlength="50"
        />
      </view>

      <!-- 错误提示 -->
      <view class="error-text" v-if="errorMsg">{{ errorMsg }}</view>

      <!-- 注册按钮 -->
      <button
        class="register-btn"
        :class="{ loading: isLoading }"
        :disabled="isLoading"
        @click="handleRegister"
      >
        <text class="btn-text" v-if="!isLoading">注 册</text>
        <text class="btn-loading" v-else>注册中...</text>
      </button>

      <!-- 登录入口 -->
      <view class="login-row">
        <text class="login-text">已有账号？</text>
        <text class="login-link" @tap="goLogin">去登录</text>
      </view>
    </view>
  </view>
</template>

<script>
import { registerAPI } from "@/api";

export default {
  data() {
    return {
      username: "",
      password: "",
      confirmPassword: "",
      email: "",
      showPassword: false,
      showConfirmPwd: false,
      isLoading: false,
      errorMsg: ""
    };
  },
  onShow() {
    this.errorMsg = "";
  },
  methods: {
    async handleRegister() {
      this.errorMsg = "";

      // 表单校验
      if (!this.username.trim()) {
        this.errorMsg = "请输入账号";
        return;
      }
      if (this.username.trim().length < 2) {
        this.errorMsg = "账号至少2个字符";
        return;
      }
      if (!this.password) {
        this.errorMsg = "请输入密码";
        return;
      }
      if (this.password.length < 6) {
        this.errorMsg = "密码至少6位";
        return;
      }
      if (this.password !== this.confirmPassword) {
        this.errorMsg = "两次密码输入不一致";
        return;
      }
      if (this.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
        this.errorMsg = "邮箱格式不正确";
        return;
      }

      this.isLoading = true;
      try {
        const emailToSend = this.email.trim() || undefined;
        const res = await registerAPI(this.username, this.password, emailToSend);
        // res 已经是 response.data (由拦截器处理)
        if (res && (res.success || res.code === 200)) {
          uni.showToast({ title: "注册成功", icon: "success" });
          setTimeout(() => {
            // 注册成功，回退到登录页填好账号
            uni.navigateBack();
          }, 1500);
        } else {
          this.errorMsg = res?.message || "注册失败，请重试";
        }
      } catch (e) {
        const errMsg = e?.response?.data?.message || e?.message || "";
        this.errorMsg = errMsg || "网络请求失败，请检查网络连接";
      } finally {
        this.isLoading = false;
      }
    },
    goLogin() {
      uni.navigateBack();
    }
  }
};
</script>

<style scoped>
.register-page {
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
  background: linear-gradient(135deg, #34c759, #30d158);
  top: -250rpx;
  right: -150rpx;
  opacity: 0.10;
}
.bg-circle-2 {
  width: 350rpx;
  height: 350rpx;
  background: linear-gradient(135deg, #007aff, #5856d6);
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
  padding: 60rpx 0 40rpx;
}
.brand-icon {
  font-size: 72rpx;
  margin-bottom: 16rpx;
  animation: floatIcon 3s ease-in-out infinite;
}
@keyframes floatIcon {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12rpx); }
}
.brand-title {
  font-size: 40rpx;
  font-weight: 800;
  background: linear-gradient(135deg, #30d158, #34c759);
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

/* ===== 注册卡片 ===== */
.register-card {
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
  border-color: #34c759;
  background: #f0faf4;
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

/* ===== 错误提示 ===== */
.error-text {
  font-size: 24rpx;
  color: #ff3b30;
  margin-bottom: 8rpx;
  padding-left: 8rpx;
}

/* ===== 注册按钮 ===== */
.register-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: linear-gradient(135deg, #34c759, #30d158);
  color: #fff;
  border-radius: 48rpx;
  font-size: 32rpx;
  font-weight: 700;
  letter-spacing: 4rpx;
  box-shadow: 0 8rpx 32rpx rgba(52, 199, 89, 0.3);
  transition: all 0.2s;
  border: none;
  padding: 0;
  margin-top: 16rpx;
}
.register-btn:active {
  transform: scale(0.97);
  opacity: 0.9;
}
.register-btn.loading {
  background: linear-gradient(135deg, #8edca0, #8adba0);
  box-shadow: none;
}
.register-btn[disabled] {
  opacity: 1;
}
.btn-text {
  color: #fff;
}
.btn-loading {
  color: rgba(255,255,255,0.85);
  font-size: 28rpx;
}

/* ===== 登录入口 ===== */
.login-row {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 36rpx;
  gap: 8rpx;
}
.login-text {
  font-size: 26rpx;
  color: #8e98a5;
}
.login-link {
  font-size: 26rpx;
  color: #34c759;
  font-weight: 600;
  text-decoration: underline;
  text-underline-offset: 4rpx;
}
.login-link:active {
  opacity: 0.7;
}
</style>
