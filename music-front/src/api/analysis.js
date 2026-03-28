import request from '@/utils/request'

export const getLatestAnalysis = (songId) => {
  return request.get(`/api/music/latest/${songId}`)   // 根据实际后端路径调整
}