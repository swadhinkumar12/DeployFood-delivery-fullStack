// api/axios.js
// Centralized Axios instance — auto-attaches JWT to every request

import axios from 'axios'

const api = axios.create({
  baseURL: '/api', // Vite proxies /api → http://localhost:8080/api
  headers: { 'Content-Type': 'application/json' },
})

// REQUEST INTERCEPTOR: attach Bearer token if available
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// RESPONSE INTERCEPTOR: redirect to login if 401 Unauthorized
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid — clear storage and go to login
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
