<template>
  <view class="profile-page">
    <!-- 顶部背景 -->
    <view class="top-bg">
      <view class="bg-circle bg-circle-1"></view>
      <view class="bg-circle bg-circle-2"></view>
      <view class="bg-circle bg-circle-3"></view>
    </view>

    <!-- 用户卡片 -->
    <view class="user-card">
      <view class="avatar-wrap">
        <image
          class="avatar"
          :src="userInfo.avatar || 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22%3E%3Ccircle cx=%2250%22 cy=%2250%22 r=%2248%22 fill=%22%23e8ecf2%22/%3E%3Ctext x=%2250%22 y=%2260%22 text-anchor=%22middle%22 font-size=%2240%22 fill=%22%23999%22%3E👤%3C/text%3E%3C/svg%3E'"
          mode="aspectFill"
        />
      </view>
      <view class="user-name">{{ userInfo.username || '未登录' }}</view>
      <view class="user-meta" v-if="userInfo.userId">
        <text class="meta-item">ID: {{ userInfo.userId }}</text>
      </view>
    </view>

    <!-- 信息列表 -->
    <view class="info-section" v-if="userInfo.userId">
      <view class="section-title">个人信息</view>

      <view class="info-row">
        <view class="info-label">
          <text class="info-icon">👤</text>
          <text>用户名</text>
        </view>
        <text class="info-value">{{ userInfo.username }}</text>
      </view>

      <view class="info-row">
        <view class="info-label">
          <text class="info-icon">✉️</text>
          <text>邮箱</text>
        </view>
        <text class="info-value" :class="{ 'unbound': !userInfo.email }">
          {{ userInfo.email || '未绑定' }}
        </text>
      </view>

      <view class="info-row">
        <view class="info-label">
          <text class="info-icon">📞</text>
          <text>手机号</text>
        </view>
        <text class="info-value" :class="{ 'unbound': !userInfo.phone }">
          {{ userInfo.phone || '未绑定' }}
        </text>
      </view>

      <view class="info-row">
        <view class="info-label">
          <text class="info-icon">🖼️</text>
          <text>头像</text>
        </view>
        <text class="info-value" :class="{ 'unbound': !userInfo.avatar }">
          {{ userInfo.avatar ? '已设置' : '未设置' }}
        </text>
      </view>
    </view>

    <!-- 未登录状态 -->
    <view class="login-tip" v-else>
      <text class="tip-text">登录后查看个人信息</text>
      <button class="login-btn" @click="goLogin">前往登录</button>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section" v-if="userInfo.userId">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>

  </view>
</template>

<script>
import { getUserProfile } from "@/api";

export default {
  data() {
    return {
      userInfo: {}
    };
  },
  onShow() {
    this.loadUserInfo();
  },
  methods: {
    loadUserInfo() {
      // 先读取本地缓存
      const local = uni.getStorageSync("userInfo");
      if (local) {
        this.userInfo = { ...local };
      }

      // 已登录则从后端拉取完整信息
      if (local && local.userId) {
        this.fetchProfile();
      }
    },
    async fetchProfile() {
      try {
        const res = await getUserProfile();
        // res = { success: true, data: { userId, username, email, phone, avatar } }
        if (res?.success && res?.data) {
          const profile = res.data;
          const merged = {
            ...this.userInfo,
            ...profile,
            userId: this.userInfo.userId || profile.userId
          };
          this.userInfo = merged;
          // 更新本地缓存
          uni.setStorageSync("userInfo", merged);
        }
      } catch (e) {
        // 请求失败则使用本地缓存
      }
    },
    goLogin() {
      uni.navigateTo({ url: "/pages/login/login" });
    },
    handleLogout() {
      uni.showModal({
        title: "提示",
        content: "确定退出登录吗？",
        success: (res) => {
          if (res.confirm) {
            uni.removeStorageSync("userInfo");
            uni.removeStorageSync("savedAccount");
            this.userInfo = {};
            uni.showToast({ title: "已退出", icon: "success" });
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 0;
}

/* 顶部装饰背景 */
.top-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 300rpx;
  overflow: hidden;
  pointer-events: none;
}
.bg-circle {
  position: absolute;
  border-radius: 50%;
}
.bg-circle-1 {
  width: 500rpx;
  height: 500rpx;
  background: linear-gradient(135deg, #007aff, #5856d6);
  top: -200rpx;
  right: -100rpx;
  opacity: 0.12;
}
.bg-circle-2 {
  width: 300rpx;
  height: 300rpx;
  background: linear-gradient(135deg, #34c759, #30d158);
  top: -50rpx;
  left: -80rpx;
  opacity: 0.08;
}
.bg-circle-3 {
  width: 200rpx;
  height: 200rpx;
  background: linear-gradient(135deg, #ff9500, #ffcc00);
  top: 100rpx;
  left: 60%;
  opacity: 0.06;
}

/* 用户卡片 */
.user-card {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 40rpx 40rpx;
}
.avatar-wrap {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  overflow: hidden;
  border: 6rpx solid #fff;
  box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.1);
  margin-bottom: 24rpx;
  background: #f0f4f9;
}
.avatar {
  width: 100%;
  height: 100%;
}
.user-name {
  font-size: 36rpx;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8rpx;
}
.user-meta {
  display: flex;
  gap: 16rpx;
}
.meta-item {
  font-size: 24rpx;
  color: #8e98a5;
}

/* 信息区块 */
.info-section {
  background: #fff;
  border-radius: 24rpx;
  margin: 0 30rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.04);
}
.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 2rpx solid #f0f2f5;
}
.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22rpx 0;
  border-bottom: 1rpx solid #f5f6f8;
}
.info-row:last-child {
  border-bottom: none;
}
.info-label {
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 28rpx;
  color: #1a1a2e;
}
.info-icon {
  font-size: 30rpx;
}
.info-value {
  font-size: 26rpx;
  color: #6b7a8f;
}
.info-value.unbound {
  color: #c0c8d4;
  font-style: italic;
}

/* 未登录提示 */
.login-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 40rpx;
}
.tip-text {
  font-size: 28rpx;
  color: #8e98a5;
  margin-bottom: 30rpx;
}
.login-btn {
  width: 300rpx;
  height: 80rpx;
  line-height: 80rpx;
  background: linear-gradient(135deg, #007aff, #5856d6);
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
  box-shadow: 0 6rpx 24rpx rgba(0,122,255,0.3);
}

/* 退出登录 */
.logout-section {
  margin: 40rpx 30rpx 0;
}
.logout-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  background: #fff;
  color: #ff3b30;
  border-radius: 16rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: 2rpx solid #ff3b30;
  box-shadow: 0 4rpx 12rpx rgba(255,59,48,0.08);
}
.logout-btn:active {
  background: #fff5f5;
}
</style>
