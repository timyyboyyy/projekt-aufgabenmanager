import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import * as usersApi from '../api/users'
import type { User } from '../api/users'
import type { Role } from '../api/auth'

const ROLES: Role[] = ['ADMIN', 'PROJEKTLEITER', 'MITARBEITER']

/** US1: Benutzer und Rollen verwalten (nur Admin). */
export default function UserManagement() {
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [role, setRole] = useState<Role>('MITARBEITER')

  async function reload() {
    setLoading(true)
    try {
      setUsers(await usersApi.listUsers())
      setError(null)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Laden fehlgeschlagen.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    reload()
  }, [])

  async function handleCreate(event: FormEvent) {
    event.preventDefault()
    try {
      await usersApi.createUser({ username, password, role })
      setUsername('')
      setPassword('')
      setRole('MITARBEITER')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Anlegen fehlgeschlagen.')
    }
  }

  async function handleRoleChange(user: User, newRole: Role) {
    try {
      await usersApi.updateUserRole(user.id, newRole)
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Rollenwechsel fehlgeschlagen.')
    }
  }

  async function handleToggleActive(user: User) {
    try {
      await usersApi.updateUser(user.id, { aktiv: !user.aktiv })
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Aktualisierung fehlgeschlagen.')
    }
  }

  return (
    <section>
      <h1>Benutzerverwaltung</h1>
      {error && (
        <p role="alert" className="error">
          {error}
        </p>
      )}

      <form className="card" onSubmit={handleCreate}>
        <h3>Neuer Benutzer</h3>
        <label htmlFor="u-name">Benutzername</label>
        <input id="u-name" value={username} onChange={(e) => setUsername(e.target.value)} required />
        <label htmlFor="u-pw">Passwort</label>
        <input
          id="u-pw"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <label htmlFor="u-role">Rolle</label>
        <select id="u-role" value={role} onChange={(e) => setRole(e.target.value as Role)}>
          {ROLES.map((r) => (
            <option key={r} value={r}>
              {r}
            </option>
          ))}
        </select>
        <div className="form-actions">
          <button type="submit">Benutzer anlegen</button>
        </div>
      </form>

      {loading ? (
        <p>Lädt…</p>
      ) : (
        <div className="table-wrap">
          <table className="table">
          <thead>
            <tr>
              <th>Benutzername</th>
              <th>Rolle</th>
              <th>Aktiv</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.username}</td>
                <td>
                  <select
                    value={user.role}
                    onChange={(e) => handleRoleChange(user, e.target.value as Role)}
                  >
                    {ROLES.map((r) => (
                      <option key={r} value={r}>
                        {r}
                      </option>
                    ))}
                  </select>
                </td>
                <td>{user.aktiv ? 'ja' : 'nein'}</td>
                <td>
                  <button type="button" className="secondary" onClick={() => handleToggleActive(user)}>
                    {user.aktiv ? 'Deaktivieren' : 'Aktivieren'}
                  </button>
                </td>
              </tr>
            ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
