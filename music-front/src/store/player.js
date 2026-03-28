import { defineStore } from 'pinia'
import { getLatestAnalysis } from '@/api/analysis'

export const usePlayerStore = defineStore('player', {
  state: () => ({
    currentSong: null,
    isPlaying: false,
    currentTime: 0,
    duration: 0,
    volume: 0.7,
    audio: null,
    currentAnalysis: null   // 新增：当前歌曲的分析结果
  }),
  actions: {
    initAudio() {
      if (this.audio) return
      this.audio = new Audio()
      this.audio.volume = this.volume
      this.audio.addEventListener('timeupdate', () => {
        this.currentTime = this.audio.currentTime
      })
      this.audio.addEventListener('loadedmetadata', () => {
        this.duration = this.audio.duration
      })
      this.audio.addEventListener('ended', () => {
        this.isPlaying = false
        this.currentTime = 0
      })
      this.audio.addEventListener('play', () => {
        this.isPlaying = true
      })
      this.audio.addEventListener('pause', () => {
        this.isPlaying = false
      })
      this.audio.addEventListener('error', (e) => {
        console.error('音频加载失败', e)
        this.currentSong = null
        this.isPlaying = false
        this.currentAnalysis = null
      })
    },
    async play(song) {
      if (!song || !song.fileUrl) return
      // 切换歌曲
      if (!this.currentSong || this.currentSong.id !== song.id) {
        this.currentSong = song
        this.initAudio()
        this.audio.src = song.fileUrl
        this.audio.load()
        this.audio.play().catch(e => console.warn('播放失败', e))
        this.isPlaying = true

        // 异步获取分析结果（不阻塞播放）
        try {
          const res = await getLatestAnalysis(song.id)
          if (res.code === 200) {
            this.currentAnalysis = res.data
          } else {
            this.currentAnalysis = null
          }
        } catch (e) {
          console.error('获取分析结果失败', e)
          this.currentAnalysis = null
        }
      } else if (!this.isPlaying) {
        // 同一首歌，恢复播放
        this.audio.play()
        this.isPlaying = true
      }
    },
    pause() {
      if (this.audio) {
        this.audio.pause()
        this.isPlaying = false
      }
    },
    setVolume(vol) {
      this.volume = vol
      if (this.audio) this.audio.volume = vol
    },
    seek(time) {
      if (this.audio) {
        this.audio.currentTime = time
        this.currentTime = time
      }
    }
  }
})