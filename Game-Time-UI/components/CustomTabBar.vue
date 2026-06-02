<template>
  <view class="custom-tabbar" @touchmove.prevent>
    <!-- 背景遮罩（防止点击穿透） -->
    <view class="tabbar-overlay"></view>
    <!-- 悬浮胶囊容器 -->
    <view class="tabbar-capsule">
      <view
        class="tab-item"
        :class="{ active: current === 0 }"
        @click="switchTab(0)"
      >
        <view class="tab-icon-wrap">
          <image
            class="tab-icon"
            :src="current === 0 ? '/static/tab_home_active.svg' : '/static/tab_home.svg'"
            mode="aspectFit"
          />
        </view>
        <text class="tab-label">首页</text>
      </view>

      <!-- 中间分隔线 -->
      <view class="tab-divider"></view>

      <view
        class="tab-item"
        :class="{ active: current === 1 }"
        @click="switchTab(1)"
      >
        <view class="tab-icon-wrap">
          <image
            class="tab-icon"
            :src="current === 1 ? '/static/tab_profile_active.svg' : '/static/tab_profile.svg'"
            mode="aspectFit"
          />
        </view>
        <text class="tab-label">我的</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: "CustomTabBar",
  data() {
    return {
      current: 0
    };
  },
  mounted() {
    this.updateCurrent();
  },
  // 监听页面显示时更新当前 tab
  onShow() {
    this.updateCurrent();
  },
  methods: {
    updateCurrent() {
      const pages = getCurrentPages();
      if (pages.length > 0) {
        const route = pages[pages.length - 1].route;
        if (route === 'pages/index/index') {
          this.current = 0;
        } else if (route === 'pages/profile/profile') {
          this.current = 1;
        }
      }
    },
    switchTab(index) {
      if (this.current === index) return;
      const paths = ['/pages/index/index', '/pages/profile/profile'];
      uni.switchTab({
        url: paths[index]
      });
      this.current = index;
    }
  }
};
</script>

<style scoped>
/* 整个底部容器 */
.custom-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 999;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  padding-bottom: calc(20rpx + constant(safe-area-inset-bottom));
  /* 关键：整个容器不阻挡点击，只有胶囊可点击 */
  pointer-events: none;
}

/* 遮罩层 - 防止页面内容与悬浮导航重叠区域误触 */
.tabbar-overlay {
  display: none;
}

/* 悬浮胶囊主体 */
.tabbar-capsule {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 60rpx;
  padding: 10rpx 40rpx;
  box-shadow:
    0 8rpx 40rpx rgba(0, 0, 0, 0.1),
    0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  border: 1rpx solid rgba(255, 255, 255, 0.8);
  pointer-events: auto; /* 胶囊本身可点击 */
}

/* 每个 Tab 项 */
.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12rpx 32rpx;
  transition: all 0.2s ease;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
  user-select: none;
}

.tab-item:active {
  transform: scale(0.94);
  opacity: 0.8;
}

/* 图标圆形背景 */
.tab-icon-wrap {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4rpx;
}

.tab-icon {
  width: 52rpx;
  height: 52rpx;
}

/* 标签文字 */
.tab-label {
  font-size: 20rpx;
  font-weight: 500;
  color: #999999;
  line-height: 1.2;
  transition: color 0.2s ease;
}

/* 选中状态 */
.tab-item.active .tab-label {
  color: #007aff;
  font-weight: 600;
}

/* 中间分隔线 */
.tab-divider {
  width: 2rpx;
  height: 36rpx;
  background: #e8ecf0;
  border-radius: 1rpx;
  margin: 0 8rpx;
}
</style>

