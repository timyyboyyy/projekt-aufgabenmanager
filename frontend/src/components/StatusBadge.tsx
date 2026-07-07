import type { TaskStatus } from '../api/tasks'
import type { ProjectStatus } from '../api/projects'
import { PROJECT_STATUS_LABEL, TASK_STATUS_LABEL } from '../labels'

/** Farbige Statusmarkierung fuer Aufgaben- und Projektstatus. */
export default function StatusBadge({ status }: { status: TaskStatus | ProjectStatus }) {
  const label =
    status in TASK_STATUS_LABEL
      ? TASK_STATUS_LABEL[status as TaskStatus]
      : PROJECT_STATUS_LABEL[status as ProjectStatus]
  return <span className={`badge badge-${status}`}>{label}</span>
}
