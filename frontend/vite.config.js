import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => ({
  server: {
    proxy: mode === 'docker'
      ? {
          '/courses': 'http://backend:7000',
          '/search': 'http://backend:7000',
          '/schedule': 'http://backend:7000',
          '/health': 'http://backend:7000',
          '/professors': 'http://backend:7000',
        }
      : {
          '/courses': 'http://localhost:7000',
          '/search': 'http://localhost:7000',
          '/schedule': 'http://localhost:7000',
          '/health': 'http://localhost:7000',
          '/professors': 'http://localhost:7000',
        }
  }
}));