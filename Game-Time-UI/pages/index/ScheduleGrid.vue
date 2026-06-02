<template>
  <view class="grid">
    <view class="header-row">
      <view class="time-col"></view>
      <view class="day-col" v-for="(d, idx) in 7" :key="idx">周{{ idx+1 }}</view>
    </view>
    <view class="row" v-for="(timeslot, r) in timeslots" :key="r">
      <view class="time-col">{{ timeslot.label }}</view>
      <view class="cell" v-for="(d, c) in 7" :key="c" @click="cellClick(r,c)">
        <view class="chips">
          <reservation-chip
            v-for="(res, i) in visibleChips(r,c)"
            :key="res.id"
            :text="res.gameName || res.username"
            :color="chipColor(res)"
          />
          <view v-if="moreCount(r,c)>0" class="more">+{{ moreCount(r,c) }}</view>
          <view v-if="cellReservations(r,c).length===0" class="empty">可预约</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import ReservationChip from './ReservationChip.vue';

export default {
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
  methods: {
    cellReservations(row, col) {
      // 计算该单元格对应日期和时段，返回匹配的预约
      const date = this.getDateForCol(col);
      const timeslot = this.timeslots[row].key;
      return this.reservations.filter(r => r.date === date && r.timeslot === timeslot);
    },
    visibleChips(row, col) {
      const list = this.cellReservations(row, col);
      return list.slice(0,2);
    },
    moreCount(row, col) {
      const list = this.cellReservations(row, col);
      return Math.max(0, list.length - 2);
    },
    chipColor(res) {
      // 根据 gameId 或 username 生成固定颜色
      const seed = (res.gameId || res.username || '').toString();
      let hash = 0; for (let i=0;i<seed.length;i++){ hash = seed.charCodeAt(i) + ((hash<<5)-hash); }
      const c = Math.abs(hash) % 16777215; return '#'+('00000'+c.toString(16)).slice(-6);
    },
    getDateForCol(col) {
      const d = new Date(this.weekStart);
      d.setDate(d.getDate() + col);
      return d.toISOString().slice(0,10);
    },
    cellClick(row, col) {
      const date = this.getDateForCol(col);
      const timeslot = this.timeslots[row].key;
      this.$emit('cellClick', { date, timeslot });
    }
  }
};
</script>

<style scoped>
.grid { border:1px solid #eee }
.header-row{ display:flex }
.time-col{ width:140rpx; padding:10rpx }
.day-col{ flex:1; padding:10rpx; text-align:center }
.row{ display:flex; border-top:1px solid #f0f0f0 }
.cell{ flex:1; min-height:80rpx; border-left:1px solid #f7f7f7; padding:6rpx }
.chips{ display:flex; flex-direction:column }
.chip{ padding:6rpx; border-radius:6rpx; margin-bottom:4rpx; font-size:24rpx }
.empty{ color:#999 }
</style>
