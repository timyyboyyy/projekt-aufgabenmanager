import { useAuth } from '../context/AuthContext'

/**
 * Platzhalter-Dashboard fuer Schritt 6 (geschuetzte Route). Die eigentlichen,
 * rollenabhaengigen Inhalte folgen in Schritt 7.
 */
export default function Dashboard() {
  const { user, logout } = useAuth()

  return (
    <main style={{ maxWidth: 640, margin: '2rem auto', padding: '0 1rem' }}>
      <h1>Dashboard</h1>
      <p>
        Angemeldet als <strong>{user?.username}</strong> ({user?.role}).
      </p>
      <button type="button" onClick={logout}>
        Abmelden
      </button>
    </main>
  )
}
