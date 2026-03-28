<template>
  <div class="home">
    <!-- 上传卡片 -->
    <el-card class="upload-card">
      <template #header><span>🎵 音乐分析</span></template>
      <el-upload
        drag
        :before-upload="handleBeforeUpload"
        :http-request="handleUpload"
        :show-file-list="false"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将音频文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 MP3/WAV/FLAC，不超过 50MB
          </div>
        </template>
      </el-upload>
      <!-- 歌名和歌手输入框 -->
      <div style="margin-top: 15px;">
        <el-input v-model="songNameInput" placeholder="歌曲名（可选）" style="width: 48%; margin-right: 4%;" />
        <el-input v-model="artistInput" placeholder="歌手名（可选）" style="width: 48%;" />
      </div>
      <div v-if="uploading" class="upload-progress">
        <el-progress :percentage="uploadProgress" />
      </div>
    </el-card>

    <!-- 分析结果卡片 -->
    <el-card v-if="analysisResult" class="result-card">
      <template #header><span>📊 分析结果</span></template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="歌曲">{{ analysisResult.songName }}</el-descriptions-item>
        <el-descriptions-item label="艺术家">{{ analysisResult.artist }}</el-descriptions-item>
        <el-descriptions-item label="风格">
          <el-tag type="primary">{{ analysisResult.latestAnalysis.style }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="情绪">
          <el-tag :type="emotionTagType(analysisResult.latestAnalysis.emotion)">
            {{ analysisResult.latestAnalysis.emotion }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-button type="primary" @click="playSong(analysisResult)">播放</el-button>
      <el-button type="primary" @click="fetchSimilar(analysisResult.id)">相似推荐</el-button>
    </el-card>

    <!-- 热门推荐 -->
    <el-card class="recommend-card">
      <template #header><span>🔥 热门推荐</span></template>
      <el-table :data="hotSongs" stripe>
        <el-table-column prop="songName" label="歌曲" />
        <el-table-column prop="artist" label="艺术家" />
        <el-table-column label="操作" width="160">
      <template #default="{ row }">
        <el-button link type="primary" @click="playSong(row)">播放</el-button>
        <el-button link type="primary" @click="fetchSimilar(row.id)">相似推荐</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 相似推荐结果 -->
    <el-card v-if="similarSongs.length" class="similar-card">
      <template #header><span>🎧 相似歌曲</span></template>
      <el-table :data="similarSongs" stripe>
        <el-table-column prop="songName" label="歌曲" />
        <el-table-column prop="artist" label="艺术家" />
        <el-table-column label="操作" width="160">
            <template #default="{ row }">
                 <el-button link type="primary" @click="playSong(row)">播放</el-button>
            </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { uploadAnalyze } from '@/api/music'
import { getHotSongs, getSimilarSongs } from '@/api/recommend'
import { usePlayerStore } from '@/store/player'

const uploading = ref(false)
const uploadProgress = ref(0)
const analysisResult = ref(null)
const hotSongs = ref([])
const similarSongs = ref([])
const songNameInput = ref('')
const artistInput = ref('')
const playerStore = usePlayerStore()

const handleBeforeUpload = (file) => {
  const isAudio = /\.(mp3|wav|flac)$/i.test(file.name)
  if (!isAudio) {
    ElMessage.error('只能上传音频文件（MP3/WAV/FLAC）')
    return false
  }
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过 50MB')
    return false
  }
  return true
}

const handleUpload = async (options) => {
  uploading.value = true
  uploadProgress.value = 0
  try {
    const res = await uploadAnalyze(options.file, songNameInput.value, artistInput.value)
    if (res.code === 200) {
      analysisResult.value = res.data
      ElMessage.success('分析完成')
      fetchHotSongs()
    } else {
      ElMessage.error(res.message || '分析失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
    uploadProgress.value = 0
  }
}

const fetchHotSongs = async () => {
  const res = await getHotSongs()
  if (res.code === 200) hotSongs.value = res.data
}

const fetchSimilar = async (songId) => {
  const res = await getSimilarSongs(songId)
  if (res.code === 200) {
    similarSongs.value = res.data
    if (res.data.length === 0) {
      ElMessage.info('暂无相似歌曲')
    } else {
      ElMessage.success('加载相似歌曲成功')
    }
  } else {
    ElMessage.error(res.message || '加载失败')
  }
}

const emotionTagType = (emotion) => {
  const map = { 快乐: 'success', 激昂: 'warning', 平静: 'info', 悲伤: 'danger' }
  return map[emotion] || ''
}

// 播放歌曲的方法
const playSong = (song) => {
  playerStore.play(song)
}

onMounted(() => {
  fetchHotSongs()
})
</script>

<style scoped>
.home {
  padding: 0;
  background-color: var(--bg-color);
}
.upload-card, .result-card, .recommend-card, .similar-card {
  margin-bottom: var(--space-lg);
}
.upload-card .el-card__body {
  padding: var(--space-lg);
}
</style>