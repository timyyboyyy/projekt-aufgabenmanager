import { apiFetch } from './client'
import type { Role } from './auth'

export interface User {
  id: number
  username: string
  role: Role
  aktiv: boolean
}

export interface CreateUserInput {
  username: string
  password: string
  role: Role
}

export interface UpdateUserInput {
  username?: string
  password?: string
  aktiv?: boolean
}

export const listUsers = () => apiFetch<User[]>('/api/users')

export const createUser = (input: CreateUserInput) =>
  apiFetch<User>('/api/users', { method: 'POST', body: JSON.stringify(input) })

export const updateUser = (id: number, input: UpdateUserInput) =>
  apiFetch<User>(`/api/users/${id}`, { method: 'PUT', body: JSON.stringify(input) })

export const updateUserRole = (id: number, role: Role) =>
  apiFetch<User>(`/api/users/${id}/role`, { method: 'PUT', body: JSON.stringify({ role }) })
