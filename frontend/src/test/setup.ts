// Globale Test-Einrichtung fuer Vitest + React Testing Library.
// Ergaenzt die jest-dom-Matcher (z. B. toBeInTheDocument) und raeumt nach jedem Test auf.
import '@testing-library/jest-dom/vitest'
import { cleanup } from '@testing-library/react'
import { afterEach } from 'vitest'

afterEach(() => {
  cleanup()
})
