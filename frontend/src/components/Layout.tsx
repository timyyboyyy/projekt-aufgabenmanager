import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import BrandMark from './BrandMark'

/** Rahmen fuer alle geschuetzten Seiten: Navigationsleiste + Inhaltsbereich. */
export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  function onLogout() {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div>
      <header className="nav">
        <div className="nav-inner">
          <span className="brand">
            <BrandMark />
            Aufgabenmanager
          </span>
          <nav className="nav-links">
            <NavLink to="/dashboard">Dashboard</NavLink>
            <NavLink to="/projects">Projekte</NavLink>
            {user?.role === 'ADMIN' && <NavLink to="/users">Benutzer</NavLink>}
          </nav>
          <div className="nav-user">
            <span className="user-chip">
              <span className="avatar">{user?.username?.charAt(0).toUpperCase()}</span>
              <span className="user-meta">
                <span className="user-name">{user?.username}</span>
                <span className="user-role">{user?.role}</span>
              </span>
            </span>
            <button type="button" className="secondary" onClick={onLogout}>
              Abmelden
            </button>
          </div>
        </div>
      </header>
      <main className="container">
        <Outlet />
      </main>
    </div>
  )
}
