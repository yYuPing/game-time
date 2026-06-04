<template>
  <view v-if="visible" class="modal" @click.self="cancel">
    <view class="box">
      <view class="title">预约 {{ date }} {{ timeslot }}</view>

      <picker mode="selector" :range="gameNames" @change="onGameChange">
        <view class="picker">
          <text class="picker-label">选择游戏</text>
          <text class="picker-value">{{ selectedGame ? selectedGame.name : '请选择' }}</text>
        </view>
      </picker>

      <textarea class="note-input" v-model="note" placeholder="备注（可选）" />

      <view class="actions">
        <button class="btn-cancel" @click="cancel">取消</button>
        <button class="btn-submit" @click="submit">提交</button>
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
      games: [
        { id: 1, name: '王者荣耀' },
        { id: 2, name: '和平精英' },
        { id: 3, name: '原神' },
        { id: 4, name: '英雄联盟' },
        { id: 5, name: '永劫无间' }
      ],
      selectedIndex: null,
      note: ''
    };
  },
  computed: {
    gameNames() {
      return this.games.map(g => g.name);
    },
    selectedGame() {
      return this.selectedIndex !== null ? this.games[this.selectedIndex] : null;
    }
  },
  watch: {
    visible(val) {
      if (val) this.fetchGames();
    }
  },
  methods: {
    async fetchGames() {
      try {
        const res = await getGameList();
        if (Array.isArray(res) && res.length > 0) {
          this.games = res;
        }
      } catch (e) {
        // 使用默认列表
      }
    },
    onGameChange(e) {
      this.selectedIndex = e.detail.value;
    },
    cancel() {
      this.$emit('update:visible', false);
    },
    submit() {
      if (!this.selectedGame) {
        uni.showToast({ title: '请选择游戏', icon: 'none' });
        return;
      }
      this.$emit('submit', {
        gameId: this.selectedGame.id,
        gameName: this.selectedGame.name,
        note: this.note
      });
      this.$emit('update:visible', false);
      this.note = '';
      this.selectedIndex = null;
    }
  }
};
</script>

<style scoped>
.modal {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.box {
  width: 86%;
  background: #fff;
  padding: 40rpx;
  border-radius: 24rpx;
}

.title {
  font-size: 32rpx;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 32rpx;
  text-align: center;
}

.picker {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  background: #f7f8fa;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
}

.picker-label {
  font-size: 28rpx;
  color: #8e98a5;
}

.picker-value {
  font-size: 28rpx;
  color: #1a1a2e;
  font-weight: 500;
}

.note-input {
  width: 100%;
  height: 160rpx;
  padding: 24rpx;
  background: #f7f8fa;
  border-radius: 16rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.actions {
  display: flex;
  gap: 24rpx;
  margin-top: 32rpx;
}

.actions button {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
  padding: 0;
}

.btn-cancel {
  background: #f5f7fa;
  color: #6b7a8f;
}

.btn-submit {
  background: linear-gradient(135deg, #007aff, #5856d6);
  color: #fff;
  box-shadow: 0 4rpx 16rpx rgba(0, 122, 255, 0.3);
}
</style>
