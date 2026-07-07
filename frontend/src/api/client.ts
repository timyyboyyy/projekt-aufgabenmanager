// Zentraler fetch-Wrapper gegen die REST-API. Haengt das JWT als Bearer-Header an
// (aus localStorage) und wandelt Fehlerantworten in eine ApiError um.

export const TOKEN_KEY = 'token'

// Wird bei einer 401-Antwort ausgeloest, damit der AuthProvider die Sitzung raeumen
// und zur Login-Seite fuehren kann (z. B. abgelaufenes/ungueltiges Token).
export const UNAUTHORIZED_EVENT = 'auth:unauthorized'

export class ApiError extends Error {
  status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  if (options.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(path, { ...options, headers })

  if (!response.ok) {
    let message = response.statusText
    try {
      const body = await response.json()
      if (body?.message) {
        message = body.message
      }
    } catch {
      // keine JSON-Fehlermeldung vorhanden
    }
    // Abgelaufenes/ungueltiges Token: Sitzung raeumen und den AuthProvider informieren.
    if (response.status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      window.dispatchEvent(new Event(UNAUTHORIZED_EVENT))
    }
    throw new ApiError(response.status, message)
  }

  if (response.status === 204) {
    return undefined as T
  }
  const text = await response.text()
  return (text ? JSON.parse(text) : undefined) as T
}
