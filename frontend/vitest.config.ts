import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

// Vitest-Konfiguration (bewusst getrennt von vite.config.ts). Wird von Vitest zur
// Laufzeit geladen und ist nicht Teil des App-Typechecks (siehe tsconfig.node.json).
export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
  },
})
