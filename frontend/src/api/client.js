import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 20000,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const setAuthStorage = (accessToken, refreshToken) => {
  if (accessToken) {
    localStorage.setItem('accessToken', accessToken)
  }
  if (refreshToken) {
    localStorage.setItem('refreshToken', refreshToken)
  }
}

export const clearAuthStorage = () => {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('user')
}

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry) {
      const refreshToken = localStorage.getItem('refreshToken')
      if (!refreshToken) {
        clearAuthStorage()
        window.location.href = '/login'
        return Promise.reject(error)
      }

      originalRequest._retry = true

      try {
        const response = await api.post('/auth/refresh-token', { refreshToken })
        const payload = response?.data?.data || {}
        const nextAccessToken = payload.accessToken
        const nextRefreshToken = payload.refreshToken

        if (nextAccessToken) {
          setAuthStorage(nextAccessToken, nextRefreshToken || refreshToken)
          originalRequest.headers.Authorization = `Bearer ${nextAccessToken}`
          return api(originalRequest)
        }
      } catch (refreshError) {
        clearAuthStorage()
        window.location.href = '/login'
      }
    }

    return Promise.reject(error)
  },
)

export default api
