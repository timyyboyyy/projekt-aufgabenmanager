import type { TaskStatus } from './api/tasks'
import type { ProjectStatus } from './api/projects'

export const TASK_STATUS_LABEL: Record<TaskStatus, string> = {
  OFFEN: 'Offen',
  IN_BEARBEITUNG: 'In Bearbeitung',
  ERLEDIGT: 'Erledigt',
}

export const PROJECT_STATUS_LABEL: Record<ProjectStatus, string> = {
  AKTIV: 'Aktiv',
  ARCHIVIERT: 'Archiviert',
}

export const TASK_STATUSES: TaskStatus[] = ['OFFEN', 'IN_BEARBEITUNG', 'ERLEDIGT']
