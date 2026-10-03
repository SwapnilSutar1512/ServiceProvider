import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import api, { clearAuthStorage, setAuthStorage } from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const storedUser = localStorage.getItem('user')
    return storedUser ? JSON.parse(storedUser) : null
  })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const bootstrap = async () => {
      const token = localStorage.getItem('accessToken')
      if (!token) {
        setUser(null)
        setLoading(false)
        return
      }

      try {
        const response = await api.get('/auth/me')
        const profile = response?.data?.data
        if (profile) {
          setUser(profile)
          localStorage.setItem('user', JSON.stringify(profile))
        }
      } catch (error) {
        clearAuthStorage()
        setUser(null)
      } finally {
        setLoading(false)
      }
    }

    bootstrap()
  }, [])

  const login = async (credentials) => {
    const response = await api.post('/auth/login', credentials)
    const payload = response?.data?.data || {}
    const { accessToken, refreshToken } = payload

    if (!accessToken) {
      throw new Error(response?.data?.message || 'Login failed')
    }

    setAuthStorage(accessToken, refreshToken)

    const profileResponse = await api.get('/auth/me')
    const profile = profileResponse?.data?.data
    if (profile) {
      setUser(profile)
      localStorage.setItem('user', JSON.stringify(profile))
    }

    return profile
  }

  const register = async (endpoint, payload) => {
    const response = await api.post(endpoint, payload)
    return response.data
  }

  const logout = async () => {
    const refreshToken = localStorage.getItem('refreshToken')
    if (refreshToken) {
      try {
        await api.post('/auth/logout', { refreshToken })
      } catch (error) {
        console.warn('Logout request failed', error)
      }
    }

    clearAuthStorage()
    setUser(null)
  }

  const value = useMemo(
    () => ({ user, loading, login, logout, register, setUser }),
    [user, loading],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
