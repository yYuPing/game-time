<template>
  <view class="grid">
    <!-- 日期表头 -->
    <view class="header-row">
      <view class="time-col"></view>
      <view
        class="day-col"
        v-for="(day, idx) in weekDays"
        :key="idx"
        :class="{ 'is-today': day.isToday }"
      >
        <text class="day-name">{{ day.name }}</text>
        <text class="day-date">{{ day.date }}</text>
      </view>
    </view>

    <!-- 时间段行 -->
    <view class="row" v-for="(timeslot, r) in timeslots" :key="r">
      <view class="time-col">{{ timeslot.label }}</view>
      <view
        class="cell"
        v-for="(day, c) in weekDays"
        :key="c"
        :class="{ 'is-today': day.isToday }"
        @click="cellClick(r, c)"
      >
        <view class="chips">
          <reservation-chip
            v-for="(res, i) in visibleChips(r, c)"
            :key="res.id"
            :text="res.gameName || res.username"
            :color="chipColor(res)"
          />
          <view v-if="moreCount(r, c) > 0" class="more">+{{ moreCount(r, c) }}</view>
          <view v-if="cellReservations(r, c).length === 0" class="empty">可预约</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import ReservationChip from './ReservationChip.vue';

export default {
  components: { ReservationChip },
  props: {
    weekStart: { type: String, required: true },
    reservations: { type: Array, default: () => [] }
  },
  data() {
    return {
      timeslots: [
        { key: '08-09', label: '08-09' },
        { key: '09-10', label: '09-10' },
        { key: '10-11', label: '10-11' },
        { key: '11-12', label: '11-12' },
        { key: '12-13', label: '12-13' },
        { key: '13-14', label: '13-14' },
        { key: '14-15', label: '14-15' },
        { key: '15-16', label: '15-16' },
        { key: '16-17', label: '16-17' },
        { key: '17-18', label: '17-18' },
        { key: '18-19', label: '18-19' },
        { key: '19-20', label: '19-20' },
        { key: '20-21', label: '20-21' },
        { key: '21-22', label: '21-22' }
      ]
    };
  },
  computed: {
    weekDays() {
      const names = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const todayStr = today.toISOString().slice(0, 10);

      return names.map((name, idx) => {
        const d = new Date(this.weekStart);
        d.setDate(d.getDate() + idx);
        const dateStr = d.toISOString().slice(0, 10);
        return {
          name,
          date: d.getDate(),
          dateStr,
          isToday: dateStr === todayStr
        };
      });
    }
  },
  methods: {
    cellReservations(row, col) {
      const date = this.weekDays[col].dateStr;
      const timeslot = this.timeslots[row].key;
      return this.reservations.filter(r => r.date === date && r.timeslot === timeslot);
    },
    visibleChips(row, col) {
      const list = this.cellReservations(row, col);
      return list.slice(0, 2);
    },
    moreCount(row, col) {
      const list = this.cellReservations(row, col);
      return Math.max(0, list.length - 2);
    },
    chipColor(res) {
      const seed = (res.gameId || res.username || '').toString();
      let hash = 0;
      for (let i = 0; i < seed.length; i++) {
        hash = seed.charCodeAt(i) + ((hash << 5) - hash);
      }
      const c = Math.abs(hash) % 16777215;
      return '#' + ('00000' + c.toString(16)).slice(-6);
    },
    cellClick(row, col) {
      const date = this.weekDays[col].dateStr;
      const timeslot = this.timeslots[row].key;
      this.$emit('cellClick', { date, timeslot });
    }
  }
};
</script>

<style scoped>
.grid {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.04);
}

/* 表头行 */
.header-row {
  display: flex;
  background: #f8f9fb;
  border-bottom: 2rpx solid #eee;
}

/* 时间列 */
.time-col {
  width: 120rpx;
  padding: 16rpx 8rpx;
  font-size: 22rpx;
  color: #8e98a5;
  text-align: center;
  flex-shrink: 0;
}

/* 日期列头 */
.day-col {
  flex: 1;
  padding: 16rpx 4rpx;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}
.day-col.is-today {
  background: rgba(0, 122, 255, 0.06);
}
.day-name {
  font-size: 22rpx;
  color: #8e98a5;
}
.day-date {
  font-size: 28rpx;
  font-weight: 700;
  color: #1a1a2e;
}
.day-col.is-today .day-date {
  color: #007aff;
  background: #007aff;
  color: #fff;
  width: 48rpx;
  height: 48rpx;
  line-height: 48rpx;
  border-radius: 50%;
}

/* 数据行 */
.row {
  display: flex;
  border-bottom: 2rpx solid #f5f5f5;
}
.row:last-child {
  border-bottom: none;
}

/* 单元格 */
.cell {
  flex: 1;
  min-height: 80rpx;
  border-left: 2rpx solid #f7f7f7;
  padding: 6rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cell.is-today {
  background: rgba(0, 122, 255, 0.03);
}

/* 预约标签容器 */
.chips {
  display: flex;
  flex-direction: column;
  width: 100%;
}

/* 更多提示 */
.more {
  font-size: 20rpx;
  color: #007aff;
  text-align: center;
  padding: 2rpx 0;
}

/* 空状态 */
.empty {
  font-size: 20rpx;
  color: #c0c8d4;
  text-align: center;
}
</style>
