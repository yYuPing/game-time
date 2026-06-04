<template>
  <view class="page">
    <!-- 顶部导航栏 -->
    <view class="header">
      <view class="header-top">
        <view class="header-left">
          <view class="app-logo">🎮</view>
          <view class="app-title">Game Time</view>
        </view>
        <view class="header-right">
          <!-- 未登录：显示登录按钮 -->
          <block v-if="!isLoggedIn">
            <view class="login-btn-small" @click="goLogin">
              <text class="login-btn-icon">👤</text>
              <text class="login-btn-text">登录</text>
            </view>
          </block>
          <!-- 已登录：显示用户信息 -->
          <block v-else>
            <view class="user-badge" @click="goProfile">
              <image
                class="user-avatar-mini"
                :src="userInfo.avatar || 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22%3E%3Ccircle cx=%2250%22 cy=%2250%22 r=%2248%22 fill=%22%23e8ecf2%22/%3E%3Ctext x=%2250%22 y=%2260%22 text-anchor=%22middle%22 font-size=%2240%22 fill=%22%23999%22%3E👤%3C/text%3E%3C/svg%3E'"
                mode="aspectFill"
              />
              <text class="user-name-mini">{{ userInfo.username || '用户' }}</text>
            </view>
          </block>
        </view>
      </view>

      <!-- 周导航 -->
      <view class="week-nav">
        <view class="nav-btn" @click="prevWeek">
          <text class="nav-arrow">‹</text>
        </view>
        <view class="week-title">
          <text class="week-main">{{ weekRangeText }}</text>
          <text class="week-sub">{{ weekDateRange }}</text>
        </view>
        <view class="nav-btn" @click="nextWeek">
          <text class="nav-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 内容区域 -->
    <view class="content-area">
      <ScheduleGrid
        :weekStart="weekStart"
        :reservations="reservations"
        @cellClick="onCellClick"
      />

      <ReservationListModal
        v-model:visible="listModalVisible"
        :date="selectedCell.date"
        :timeslot="selectedCell.timeslot"
        :reservations="selectedCellReservations"
        @create="openCreateFromList"
        @cancel="deleteReservation"
      />

      <ReservationModal
        v-model:visible="modalVisible"
        :date="selectedCell.date"
        :timeslot="selectedCell.timeslot"
        @submit="createReservation"
      />
    </view>
  </view>
</template>

<script>
import ScheduleGrid from './ScheduleGrid.vue';
import ReservationModal from './ReservationModal.vue';
import ReservationListModal from './ReservationListModal.vue';
import { getWeekSchedule, createReservation, deleteReservation } from "@/api";

export default {
  components: { ScheduleGrid, ReservationModal, ReservationListModal },
  data() {
    return {
      weekStart: this.getStartOfWeek(new Date()),
      reservations: [],
      modalVisible: false,
      listModalVisible: false,
      selectedCell: { date: null, timeslot: null },
      userInfo: {},
      isLoggedIn: false
    };
  },
  computed: {
    weekRangeText() {
      const start = new Date(this.weekStart);
      const end = new Date(start);
      end.setDate(end.getDate() + 6);
      const year = start.getFullYear();
      const month = start.getMonth() + 1;
      // 计算是本月第几周
      const firstDay = new Date(year, start.getMonth(), 1);
      const firstMonday = new Date(firstDay);
      const dayOffset = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1;
      firstMonday.setDate(1 - dayOffset);
      const weekNum = Math.floor((start - firstMonday) / (7 * 24 * 60 * 60 * 1000)) + 1;
      return `${year}年${month}月 · 第${weekNum}周`;
    },
    weekDateRange() {
      const start = new Date(this.weekStart);
      const end = new Date(start);
      end.setDate(end.getDate() + 6);
      const fmt = (d) => `${d.getMonth() + 1}月${d.getDate()}日`;
      return `${fmt(start)} - ${fmt(end)}`;
    },
    selectedCellReservations() {
      if (!this.selectedCell.date || !this.selectedCell.timeslot) return [];
      return this.reservations.filter(r =>
        r.date === this.selectedCell.date && r.timeslot === this.selectedCell.timeslot
      );
    }
  },
  onShow() {
    this.checkLoginState();
    this.loadWeek();
  },
  methods: {
    checkLoginState() {
      const user = uni.getStorageSync("userInfo");
      if (user && user.userId) {
        this.userInfo = user;
        this.isLoggedIn = true;
      } else {
        this.userInfo = {};
        this.isLoggedIn = false;
      }
    },
    getStartOfWeek(date) {
      const d = new Date(date);
      const day = d.getDay();
      const diff = d.getDate() - day + (day === 0 ? -6 : 1);
      const monday = new Date(d.setDate(diff));
      monday.setHours(0,0,0,0);
      return monday.toISOString().slice(0,10);
    },
    prevWeek() {
      const d = new Date(this.weekStart);
      d.setDate(d.getDate() - 7);
      this.weekStart = this.getStartOfWeek(d);
      this.loadWeek();
    },
    nextWeek() {
      const d = new Date(this.weekStart);
      d.setDate(d.getDate() + 7);
      this.weekStart = this.getStartOfWeek(d);
      this.loadWeek();
    },
    async loadWeek() {
      try {
        const res = await getWeekSchedule(this.weekStart);
        if (Array.isArray(res)) {
          this.reservations = res;
        } else if (res && res.reservations) {
          this.reservations = res.reservations;
        } else {
          this.reservations = [];
        }
      } catch (e) {
        this.reservations = [];
      }
    },
    onCellClick(payload) {
      this.selectedCell = payload;
      this.listModalVisible = true;
    },
    openCreateFromList() {
      const user = uni.getStorageSync('userInfo');
      if (!user || !user.userId) {
        uni.showToast({ title: '请先登录', icon: 'none' });
        setTimeout(() => {
          uni.navigateTo({ url: '/pages/login/login' });
        }, 300);
        return;
      }
      this.listModalVisible = false;
      this.modalVisible = true;
    },
    async createReservation(data) {
      try {
        const res = await createReservation({
          date: this.selectedCell.date,
          timeslot: this.selectedCell.timeslot,
          gameId: data.gameId,
          gameName: data.gameName,
          note: data.note
        });
        if (res && (res.success || res.data)) {
          uni.showToast({ title: '预约成功', icon: 'success' });
          this.loadWeek();
        }
      } catch (e) {
        uni.showToast({ title: '预约失败', icon: 'none' });
      }
      this.modalVisible = false;
    },
    async deleteReservation(id) {
      try {
        await deleteReservation(id);
        uni.showToast({ title: '取消成功', icon: 'success' });
        this.loadWeek();
      } catch (e) {
        uni.showToast({ title: '取消失败', icon: 'none' });
      }
    },
    goLogin() {
      uni.navigateTo({ url: '/pages/login/login' });
    },
    goProfile() {
      uni.switchTab({ url: '/pages/profile/profile' });
    }
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 0;
}

/* ===== 头部导航 ===== */
.header {
  background: #ffffff;
  padding: 0 20rpx 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
  position: sticky;
  top: 0;
  z-index: 10;
}

/* 顶栏：logo + 用户 */
.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0 12rpx;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.app-logo {
  font-size: 40rpx;
}
.app-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #1a1a2e;
}

/* 未登录 - 小登录按钮 */
.login-btn-small {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: linear-gradient(135deg, #007aff, #5856d6);
  padding: 10rpx 24rpx;
  border-radius: 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0,122,255,0.25);
}
.login-btn-small:active {
  opacity: 0.85;
  transform: scale(0.96);
}
.login-btn-icon {
  font-size: 26rpx;
}
.login-btn-text {
  font-size: 26rpx;
  color: #fff;
  font-weight: 600;
}

/* 已登录 - 用户徽章 */
.user-badge {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 6rpx 18rpx 6rpx 6rpx;
  border-radius: 40rpx;
  background: #f0f4ff;
  border: 2rpx solid rgba(0,122,255,0.15);
}
.user-badge:active {
  background: #e4ecff;
}
.user-avatar-mini {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: #e8ecf2;
}
.user-name-mini {
  font-size: 26rpx;
  font-weight: 600;
  color: #007aff;
  max-width: 120rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* ===== 周导航 ===== */
.week-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12rpx;
  padding: 0 8rpx;
}
.nav-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #f5f7fa;
  transition: background 0.15s;
}
.nav-btn:active {
  background: #e8ecf2;
}
.nav-arrow {
  font-size: 36rpx;
  font-weight: 600;
  color: #007aff;
  line-height: 1;
}
.week-title {
  flex: 1;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}
.week-main {
  font-size: 30rpx;
  font-weight: 700;
  color: #1a1a2e;
}
.week-sub {
  font-size: 24rpx;
  color: #8e98a5;
}

/* ===== 内容区域 ===== */
.content-area {
  padding: 20rpx;
}
</style>
