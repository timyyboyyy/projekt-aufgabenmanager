import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/** Einstieg mit rollenabhaengigen Kacheln. */
export default function Dashboard() {
  const { user } = useAuth()

  return (
    <section>
      <h1>Dashboard</h1>
      <p>
        Willkommen, <strong>{user?.username}</strong> ({user?.role}).
      </p>
      <div className="cards">
        <Link className="card" to="/projects">
          <h2>Projekte</h2>
          <p>Berechtigte Projekte, Aufgaben und Fortschritt ansehen und bearbeiten.</p>
        </Link>
        {user?.role === 'ADMIN' && (
          <Link className="card" to="/users">
            <h2>Benutzerverwaltung</h2>
            <p>Benutzerkonten anlegen, bearbeiten und Rollen zuweisen.</p>
          </Link>
        )}
      </div>
    </section>
  )
}
