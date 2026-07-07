import { apiFetch } from './client'
import type { User } from './users'

export type TaskStatus = 'OFFEN' | 'IN_BEARBEITUNG' | 'ERLEDIGT'

export interface Task {
  id: number
  titel: string
  beschreibung: string | null
  status: TaskStatus
  projectId: number
  bearbeiter: User | null
}

export interface TaskInput {
  titel: string
  beschreibung: string
  bearbeiterId: number | null
}

export const listTasks = (projectId: number) =>
  apiFetch<Task[]>(`/api/projects/${projectId}/tasks`)

export const createTask = (projectId: number, input: TaskInput) =>
  apiFetch<Task>(`/api/projects/${projectId}/tasks`, {
    method: 'POST',
    body: JSON.stringify(input),
  })

export const updateTask = (taskId: number, input: TaskInput) =>
  apiFetch<Task>(`/api/tasks/${taskId}`, { method: 'PUT', body: JSON.stringify(input) })

export const updateTaskStatus = (taskId: number, status: TaskStatus) =>
  apiFetch<Task>(`/api/tasks/${taskId}/status`, {
    method: 'PUT',
    body: JSON.stringify({ status }),
  })
