import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import App from './App'

// Smoke-Test: stellt sicher, dass die Test-Toolchain (Vitest + RTL + jsdom) funktioniert.
describe('App', () => {
  it('rendert die Startueberschrift', () => {
    render(<App />)
    expect(
      screen.getByRole('heading', { name: 'Get started' }),
    ).toBeInTheDocument()
  })
})
