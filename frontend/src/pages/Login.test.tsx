import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import Login from './Login'
import { AuthProvider } from '../context/AuthContext'
import * as authApi from '../api/auth'
import { ApiError } from '../api/client'

vi.mock('../api/auth')

function renderLogin() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/dashboard" element={<div>Dashboard-Ziel</div>} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  )
}

describe('Login-Flow', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.resetAllMocks()
  })

  it('meldet an, speichert das Token und leitet zum Dashboard', async () => {
    vi.mocked(authApi.login).mockResolvedValue({
      token: 'jwt-123',
      username: 'admin',
      role: 'ADMIN',
    })
    const user = userEvent.setup()
    renderLogin()

    await user.type(screen.getByLabelText('Benutzername'), 'admin')
    await user.type(screen.getByLabelText('Passwort'), 'admin123')
    await user.click(screen.getByRole('button', { name: 'Anmelden' }))

    expect(await screen.findByText('Dashboard-Ziel')).toBeInTheDocument()
    expect(authApi.login).toHaveBeenCalledWith('admin', 'admin123')
    expect(localStorage.getItem('token')).toBe('jwt-123')
  })

  it('zeigt eine Fehlermeldung bei falschen Zugangsdaten', async () => {
    vi.mocked(authApi.login).mockRejectedValue(new ApiError(401, 'egal'))
    const user = userEvent.setup()
    renderLogin()

    await user.type(screen.getByLabelText('Benutzername'), 'admin')
    await user.type(screen.getByLabelText('Passwort'), 'falsch')
    await user.click(screen.getByRole('button', { name: 'Anmelden' }))

    expect(await screen.findByRole('alert')).toHaveTextContent(/falsch/i)
    expect(localStorage.getItem('token')).toBeNull()
  })
})
