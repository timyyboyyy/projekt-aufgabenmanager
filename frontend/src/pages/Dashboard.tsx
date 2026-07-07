import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/** Einstieg mit rollenabhaengigen Kacheln. */
export default function Dashboard() {
  const { user } = useAuth()

  return (
    <section>
      <div className="page-head">
        <p className="eyebrow">Übersicht</p>
        <h1>Willkommen, {user?.username}</h1>
        <p className="muted">Angemeldet als {user?.role}. Wähle einen Bereich, um loszulegen.</p>
      </div>

      <div className="cards">
        <Link className="card tile" to="/projects">
          <span className="tile-icon" aria-hidden="true">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
            </svg>
          </span>
          <h2>Projekte</h2>
          <p>Berechtigte Projekte, Aufgaben und Fortschritt ansehen und bearbeiten.</p>
          <span className="tile-arrow" aria-hidden="true">→</span>
        </Link>

        {user?.role === 'ADMIN' && (
          <Link className="card tile" to="/users">
            <span className="tile-icon" aria-hidden="true">
              <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M22 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" />
              </svg>
            </span>
            <h2>Benutzerverwaltung</h2>
            <p>Benutzerkonten anlegen, bearbeiten und Rollen zuweisen.</p>
            <span className="tile-arrow" aria-hidden="true">→</span>
          </Link>
        )}
      </div>
    </section>
  )
}
