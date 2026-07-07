import { render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ProtectedRoute } from './ProtectedRoute'
import { AuthProvider } from '../context/AuthContext'
import * as authApi from '../api/auth'

vi.mock('../api/auth')

function renderAt(path: string) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<div>Login-Seite</div>} />
          <Route path="/dashboard" element={<div>Dashboard-Seite</div>} />
          <Route
            path="/admin"
            element={
              <ProtectedRoute requiredRole="ADMIN">
                <div>Admin-Bereich</div>
              </ProtectedRoute>
            }
          />
          <Route
            path="/geschuetzt"
            element={
              <ProtectedRoute>
                <div>Geschuetzt-Inhalt</div>
              </ProtectedRoute>
            }
          />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  )
}

describe('ProtectedRoute', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.resetAllMocks()
  })

  it('leitet ohne Anmeldung auf die Login-Seite', async () => {
    renderAt('/geschuetzt')
    expect(await screen.findByText('Login-Seite')).toBeInTheDocument()
  })

  it('leitet bei fehlender Rolle zurueck aufs Dashboard', async () => {
    localStorage.setItem('token', 'x')
    vi.mocked(authApi.getMe).mockResolvedValue({ username: 'user1', role: 'MITARBEITER' })
    renderAt('/admin')
    expect(await screen.findByText('Dashboard-Seite')).toBeInTheDocument()
  })

  it('zeigt den Inhalt fuer eine berechtigte Rolle', async () => {
    localStorage.setItem('token', 'x')
    vi.mocked(authApi.getMe).mockResolvedValue({ username: 'admin', role: 'ADMIN' })
    renderAt('/admin')
    expect(await screen.findByText('Admin-Bereich')).toBeInTheDocument()
  })
})
