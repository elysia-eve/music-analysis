import request from '@/utils/request'

export const getHotSongs = (limit = 10) => {
    return request.get('/api/recommendations/hot', { params: { limit } })
}

export const getSimilarSongs = (songId, limit = 5) => {
    return request.get(`/api/recommendations/similar/${songId}`, { params: { limit } })
}