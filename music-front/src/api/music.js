import request from '@/utils/request'

export const uploadAnalyze = (file, songName, artist) => {
    const formData = new FormData()
    formData.append('file', file)
    if (songName) formData.append('songName', songName)
    if (artist) formData.append('artist', artist)
    return request.post('/api/music/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}