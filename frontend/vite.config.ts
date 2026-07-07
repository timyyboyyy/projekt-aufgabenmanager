import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
// Testkonfiguration liegt in vitest.config.ts (getrennt, damit der App-Typecheck
// nicht die Vitest-Typen einbezieht).
export default defineConfig({
  plugins: [react()],
  server: {
    // Backend-REST-API im Dev-Betrieb unter /api an Spring Boot weiterreichen.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
