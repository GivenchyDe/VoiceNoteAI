import request from '@/utils/request.js'

// 用户
export const login = (data) => request.post('/user/login', data)
export const register = (data) => request.post('/user/register', data)
export const getUserInfo = () => request.get('/user/info')
export const updatePassword = (data) => request.put('/user/password', data)
export const updateProfile = (data) => request.put('/user/update', data)
export const uploadAvatar = (formData) => request.post('/user/avatar', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

// 音频
export const uploadAudio = (formData) => request.post('/audio/upload', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})
export const getAudioList = (params) => request.get('/audio/list', { params })
export const deleteAudio = (id) => request.delete(`/audio/delete/${id}`)

// 笔记
export const getNoteList = (params) => request.get('/note/list', { params })
export const addNote = (data) => request.post('/note/add', data)
export const updateNote = (data) => request.put('/note/update', data)
export const deleteNote = (id) => request.delete(`/note/delete/${id}`)
export const archiveNote = (id, status) => request.put(`/note/archive/${id}?status=${status}`)

// 反馈
export const submitFeedback = (data) => request.post('/feedback/add', data)
export const getMyFeedbacks = (params) => request.get('/feedback/list', { params })

// 公告
export const getNoticeList = () => request.get('/notice/list')
