<template>
  <view v-if="visible" class="modal">
    <view class="box">
      <view class="title">预约 {{ date }} {{ timeslot }}</view>
      <picker mode="selector" :range="games" @change="onGameChange">
        <view class="picker">选择游戏: {{ selectedGame?.name || '请选择' }}</view>
      </picker>
      <textarea v-model="note" placeholder="备注（可选）"></textarea>
      <view class="actions">
        <button @click="cancel">取消</button>
        <button @click="submit">提交</button>
      </view>
    </view>
  </view>
</template>

<script>
import { getGameList } from "@/api";

export default {
  props: {
    visible: { type: Boolean, default: false },
    date: { type: String, default: '' },
    timeslot: { type: String, default: '' }
  },
  data() {
    return {
      games: [ { name: '游戏A', id: 'g1' }, { name: '游戏B', id: 'g2' } ],
      selectedIndex: null,
      note: ''
    };
  },
  computed: {
    selectedGame() { return this.games[this.selectedIndex]; }
  },
  onLoad() {
    this.fetchGames();
  },
  methods: {
    async fetchGames() {
      try {
        const res = await getGameList();
        if (Array.isArray(res)) this.games = res;
      } catch (e) {
        // 兜底示例
        this.games = [{ name: '游戏A', id: 'g1' }, { name: '游戏B', id: 'g2' }];
      }
    },
    onGameChange(e) { this.selectedIndex = e.detail.value; },
    cancel() { this.$emit('update:visible', false); },
    submit() {
      if (!this.selectedGame) { uni.showToast({ title: '请选择游戏', icon: 'none' }); return; }
      this.$emit('submit', { gameId: this.selectedGame.id, gameName: this.selectedGame.name, note: this.note });
      this.$emit('update:visible', false);
      this.note = '';
      this.selectedIndex = null;
    }
  }
}
</script>

<style scoped>
.modal{ position:fixed; left:0; right:0; top:0; bottom:0; background: rgba(0,0,0,0.4); display:flex; justify-content:center; align-items:center }
.box{ width:86%; background:#fff; padding:24rpx; border-radius:12rpx }
.title{ font-weight:bold; margin-bottom:12rpx }
.picker{ padding:12rpx; border:1px solid #eee; margin-bottom:12rpx }
.actions{ display:flex; justify-content:space-between; margin-top:12rpx }
</style>
