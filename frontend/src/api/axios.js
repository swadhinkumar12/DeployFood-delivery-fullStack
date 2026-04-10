// api/axios.js
// Centralized Axios instance — auto-attaches JWT to every request

import axios from 'axios'
import { API_BASE_URL } from '../config/apiConfig'

export const getApiErrorMessage = (error, fallback = 'Something went wrong') => {
  return error?.response?.data?.message || fallback
}

const api = axios.create({
  baseURL: API_BASE_URL,
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
    if (error.response?.status === 401 || error.response?.status === 403) {
      // Token expired or invalid — clear storage and go to login
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
