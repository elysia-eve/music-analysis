<template>
  <div class="history">
    <el-card>
      <template #header><span>📜 我的分析历史</span></template>
      <el-table :data="records" stripe>
  <el-table-column prop="songName" label="歌曲" />
  <el-table-column prop="artist" label="艺术家" />
  <el-table-column prop="createTime" label="分析时间" width="180" />
  <el-table-column label="操作" width="150">
    <template #default="{ row }">
      <el-button link type="primary" @click="playSong(row)">播放</el-button>
    </template>
  </el-table-column>
</el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="currentSong?.songName" width="500px">
      <el-descriptions :column="1" border v-if="currentSong">
        <el-descriptions-item label="艺术家">{{ currentSong.artist }}</el-descriptions-item>
        <el-descriptions-item label="风格">{{ currentSong.latestAnalysis?.style || '未知' }}</el-descriptions-item>
        <el-descriptions-item label="情绪">{{ currentSong.latestAnalysis?.emotion || '未知' }}</el-descriptions-item>
      </el-descriptions>
      <audio v-if="currentSong?.fileUrl" controls style="width: 100%; margin-top: 15px;">
        <source :src="currentSong.fileUrl" type="audio/mpeg">
      </audio>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserHistory } from '@/api/user'
import { ElMessage } from 'element-plus'
import { usePlayerStore } from '@/store/player'

const records = ref([])
const detailVisible = ref(false)
const currentSong = ref(null)
const playerStore = usePlayerStore()

const fetchHistory = async () => {
  const res = await getUserHistory()
  if (res.code === 200) {
    records.value = res.data
  } else {
    ElMessage.error(res.message || '加载失败')
  }
}


const playSong = (song) => {
  playerStore.play(song)
}

onMounted(() => {
  fetchHistory()
})
</script>

<style scoped>
.history {
  padding: 0;
}
.el-card {
  margin-bottom: var(--space-lg);
}
</style>