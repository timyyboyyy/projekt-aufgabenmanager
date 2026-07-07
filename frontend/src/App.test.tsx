import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it } from 'vitest'
import App from './App'
import { AuthProvider } from './context/AuthContext'

// Smoke-Test der Routing-/Auth-Grundlage: ohne Anmeldung fuehrt eine geschuetzte
// Route auf die Login-Seite. (Umfangreichere Frontend-Tests folgen in Schritt 8.)
describe('App', () => {
  it('leitet nicht angemeldete Nutzer auf die Login-Seite', async () => {
    render(
      <MemoryRouter initialEntries={['/dashboard']}>
        <AuthProvider>
          <App />
        </AuthProvider>
      </MemoryRouter>,
    )
    expect(
      await screen.findByRole('heading', { name: 'Anmelden' }),
    ).toBeInTheDocument()
  })
})
