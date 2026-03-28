<template>
  <!-- 悬浮球模式（无图标，仅文字） -->
  <div v-if="minimized" class="floating-ball" @click="toggleMinimize">
    {{ store.isPlaying ? '⏸' : '▶' }}
  </div>

  <!-- 完整播放器模式 -->
  <div v-else-if="store.currentSong" class="global-player">
    <div class="player-header">
      <div class="song-info">
        <div class="title">{{ store.currentSong.songName }}</div>
        <div class="artist">{{ store.currentSong.artist || '未知歌手' }}</div>
        <div v-if="store.currentAnalysis" class="analysis-tags">
            <span class="style">{{ store.currentAnalysis.style }}</span>
            <span class="emotion">{{ store.currentAnalysis.emotion }}</span>
        </div>
      </div>
      <div class="header-actions">
        <el-button size="small" @click="toggleMinimize" title="最小化">—</el-button>
        <el-button size="small" @click="closePlayer" title="关闭">✕</el-button>
      </div>
    </div>

    <div class="player-controls">
      <div class="progress-bar">
        <span class="time">{{ formatTime(store.currentTime) }}</span>
        <el-slider
          :model-value="store.currentTime"
          :max="store.duration"
          @update:model-value="handleSeek"
          style="flex:1"
        />
        <span class="time">{{ formatTime(store.duration) }}</span>
      </div>

      <div class="action-bar">
        <el-button @click="togglePlay">{{ store.isPlaying ? '暂停' : '播放' }}</el-button>
        <div class="volume-control">
          <span>音量</span>
          <el-slider
            :model-value="store.volume"
            :min="0"
            :max="1"
            :step="0.01"
            @update:model-value="handleVolumeChange"
            style="width: 80px"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { usePlayerStore } from '@/store/player'

const store = usePlayerStore()
const minimized = ref(false)

// 进度跳转
const handleSeek = (time) => {
  store.seek(time)
}

// 音量调节
const handleVolumeChange = (vol) => {
  store.setVolume(vol)
}

// 播放/暂停
const togglePlay = () => {
  if (store.isPlaying) {
    store.pause()
  } else {
    if (store.currentSong) {
      store.play(store.currentSong)
    }
  }
}

// 关闭播放器
const closePlayer = () => {
  store.pause()
  store.currentSong = null
  if (store.audio) {
    store.audio.src = ''
    store.audio = null
  }
  minimized.value = false
}

// 最小化/恢复
const toggleMinimize = () => {
  minimized.value = !minimized.value
}

// 格式化时间
const formatTime = (seconds) => {
  if (isNaN(seconds)) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

// 监听歌曲变化，如果切换歌曲，自动恢复全尺寸模式
watch(() => store.currentSong, (newSong) => {
  if (newSong) {
    minimized.value = false
  }
})
</script>

<style scoped>
/* 悬浮球 */
.floating-ball {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--color-primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: var(--shadow-md);
  z-index: 1000;
  transition: transform 0.2s, box-shadow 0.2s;
  font-size: 24px;
  user-select: none;
}
.floating-ball:hover {
  transform: scale(1.08);
  box-shadow: var(--shadow-hover);
}
.floating-ball:active {
  transform: scale(0.96);
}

/* 完整播放器 */
.global-player {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 380px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  padding: var(--space-md) var(--space-lg);
  z-index: 1000;
  border: 1px solid var(--color-gray-200);
  transition: all 0.3s ease;
}

.player-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-sm);
}
.song-info .title {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 4px;
}
.song-info .artist {
  font-size: 12px;
  color: var(--text-secondary);
}
.analysis-tags {
  margin-top: var(--space-xs);
  display: flex;
  gap: 6px;
}
.analysis-tags .style,
.analysis-tags .emotion {
  font-size: 10px;
  background: var(--color-gray-100);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
}
.header-actions {
  display: flex;
  gap: var(--space-xs);
}
.icon-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  width: 28px;
  height: 28px;
  border-radius: var(--radius-sm);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}
.icon-btn:hover {
  background-color: var(--color-gray-200);
}

.player-controls {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}
.progress-bar {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
}
.progress-slider {
  flex: 1;
  height: 4px;
  -webkit-appearance: none;
  appearance: none;
  background: var(--color-gray-300);
  border-radius: 2px;
  outline: none;
}
.progress-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: var(--color-primary);
  cursor: pointer;
}
.time {
  font-size: 12px;
  color: var(--text-secondary);
}
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.action-btn {
  background: var(--color-primary);
  border: none;
  color: white;
  padding: 4px 12px;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}
.action-btn:hover {
  background: var(--color-primary-light);
  transform: translateY(-1px);
}
.volume-control {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
}
.volume-slider {
  width: 80px;
  height: 4px;
  -webkit-appearance: none;
  appearance: none;
  background: var(--color-gray-300);
  border-radius: 2px;
  outline: none;
}
.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 12px;
  height: 12px;
  background: var(--color-primary);
  cursor: pointer;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .global-player {
    width: 90%;
    right: 5%;
    left: 5%;
    bottom: 10px;
  }
  .floating-ball {
    bottom: 16px;
    right: 16px;
    width: 44px;
    height: 44px;
    font-size: 20px;
  }
  .volume-slider {
    width: 60px;
  }
}
</style>