<template>
  <view v-if="visible" class="modal">
    <view class="box">
      <view class="title">预约详情 {{ date }} {{ timeslot }}</view>
      <view class="list">
        <view v-for="r in reservations" :key="r.id" class="item">
          <text class="user">{{ r.username }}</text>
          <text class="game">{{ r.gameName }}</text>
          <text class="note">{{ r.note }}</text>
          <button v-if="isOwner(r)" @click="onCancel(r.id)">取消</button>
        </view>
        <view v-if="reservations.length===0" class="empty">当前无预约</view>
      </view>
      <view class="actions">
        <button @click="onClose">关闭</button>
        <button @click="onCreate">新增预约</button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    visible: { type: Boolean, default: false },
    date: { type: String, default: '' },
    timeslot: { type: String, default: '' },
    reservations: { type: Array, default: () => [] }
  },
  methods: {
    onClose() { this.$emit('update:visible', false); },
    onCreate() { this.$emit('create'); },
    isOwner(r) { const u = uni.getStorageSync('userInfo'); return u && u.userId === r.userId; },
    onCancel(id) { this.$emit('cancel', id); }
  }
};
</script>

<style scoped>
.modal{ position:fixed; left:0; right:0; top:0; bottom:0; background: rgba(0,0,0,0.4); display:flex; justify-content:center; align-items:center }
.box{ width:86%; background:#fff; padding:24rpx; border-radius:12rpx }
.title{ font-weight:bold; margin-bottom:12rpx }
.list{ max-height:360rpx; overflow:auto }
.item{ display:flex; justify-content:space-between; align-items:center; padding:10rpx; border-bottom:1px solid #f0f0f0 }
.empty{ color:#999; padding:20rpx }
.actions{ display:flex; justify-content:space-between; margin-top:12rpx }
</style>
