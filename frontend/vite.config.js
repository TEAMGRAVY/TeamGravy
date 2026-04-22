import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/courses':  'http://localhost:7000',
      '/search':   'http://localhost:7000',
      '/schedule': 'http://localhost:7000',
      '/health':   'http://localhost:7000',
      '/professors': 'http://localhost:7000',
    }
  }
})