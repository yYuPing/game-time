<template>
  <view class="chip" :style="style">
    <text>{{ text }}</text>
  </view>
</template>

<script>
export default {
  props: { text: String, color: { type: String, default: '#eee' } },
  computed: {
    style() { return { backgroundColor: this.color, padding: '6rpx', borderRadius: '6rpx' }; }
  }
};
</script>

<style scoped>
.chip{ display:inline-block }
</style>
