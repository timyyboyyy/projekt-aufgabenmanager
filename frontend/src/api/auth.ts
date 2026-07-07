import { apiFetch } from './client'

export type Role = 'ADMIN' | 'PROJEKTLEITER' | 'MITARBEITER'

export interface AuthUser {
  username: string
  role: Role
}

export interface LoginResponse {
  token: string
  username: string
  role: Role
}

export function login(username: string, password: string): Promise<LoginResponse> {
  return apiFetch<LoginResponse>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  })
}

export function getMe(): Promise<AuthUser> {
  return apiFetch<AuthUser>('/api/auth/me')
}
