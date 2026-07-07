import type { ReactNode } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import type { Role } from '../api/auth'

/**
 * Schuetzt Routen: ohne Anmeldung Weiterleitung auf /login (mit gemerktem Ziel),
 * bei fehlender Rolle zurueck aufs Dashboard. Die serverseitige Autorisierung bleibt
 * massgeblich – dies ist nur die UI-Fuehrung.
 */
export function ProtectedRoute({
  children,
  requiredRole,
}: {
  children: ReactNode
  requiredRole?: Role
}) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return <p>Lädt…</p>
  }
  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }
  if (requiredRole && user.role !== requiredRole) {
    return <Navigate to="/dashboard" replace />
  }
  return <>{children}</>
}
