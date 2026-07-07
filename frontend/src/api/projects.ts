import { apiFetch } from './client'
import type { User } from './users'

export type ProjectStatus = 'AKTIV' | 'ARCHIVIERT'

export interface Project {
  id: number
  name: string
  beschreibung: string | null
  status: ProjectStatus
  leiter: User
  members: User[]
}

export interface Progress {
  projectId: number
  totalTasks: number
  doneTasks: number
  percentDone: number
}

export interface ProjectInput {
  name: string
  beschreibung: string
}

export const listProjects = () => apiFetch<Project[]>('/api/projects')

export const createProject = (input: ProjectInput) =>
  apiFetch<Project>('/api/projects', { method: 'POST', body: JSON.stringify(input) })

export const updateProject = (id: number, input: ProjectInput) =>
  apiFetch<Project>(`/api/projects/${id}`, { method: 'PUT', body: JSON.stringify(input) })

export const archiveProject = (id: number) =>
  apiFetch<Project>(`/api/projects/${id}/archive`, { method: 'PUT' })

export const addMember = (id: number, userId: number) =>
  apiFetch<Project>(`/api/projects/${id}/members`, {
    method: 'POST',
    body: JSON.stringify({ userId }),
  })

export const removeMember = (id: number, userId: number) =>
  apiFetch<Project>(`/api/projects/${id}/members/${userId}`, { method: 'DELETE' })

export const getProgress = (id: number) => apiFetch<Progress>(`/api/projects/${id}/progress`)
