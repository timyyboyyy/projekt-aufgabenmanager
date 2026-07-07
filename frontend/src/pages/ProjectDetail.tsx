import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import * as projectsApi from '../api/projects'
import * as tasksApi from '../api/tasks'
import { listUsers } from '../api/users'
import type { Project, Progress } from '../api/projects'
import type { Task, TaskInput, TaskStatus } from '../api/tasks'
import type { User } from '../api/users'
import ProgressBar from '../components/ProgressBar'
import StatusBadge from '../components/StatusBadge'
import TaskForm from '../components/TaskForm'
import { TASK_STATUSES, TASK_STATUS_LABEL } from '../labels'

/** US4/US5/US6: Aufgabenliste, Mitglieder und Fortschritt eines Projekts. */
export default function ProjectDetail() {
  const { id } = useParams()
  const projectId = Number(id)
  const { user } = useAuth()

  const [project, setProject] = useState<Project | null>(null)
  const [tasks, setTasks] = useState<Task[]>([])
  const [progress, setProgress] = useState<Progress | null>(null)
  const [allUsers, setAllUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [taskError, setTaskError] = useState<string | null>(null)

  const [showNewTask, setShowNewTask] = useState(false)
  const [editingTask, setEditingTask] = useState<Task | null>(null)
  const [newMemberId, setNewMemberId] = useState('')

  const isLeiter = !!project && project.leiter.username === user?.username
  const isMember = !!project && project.members.some((m) => m.username === user?.username)
  const canEditTasks = isLeiter || isMember
  const assignees = project ? dedupeById([project.leiter, ...project.members]) : []

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const projects = await projectsApi.listProjects()
      const found = projects.find((p) => p.id === projectId) ?? null
      setProject(found)
      if (!found) {
        setError('Projekt nicht gefunden oder kein Zugriff.')
        return
      }
      setError(null)
      try {
        const [taskList, prog] = await Promise.all([
          tasksApi.listTasks(projectId),
          projectsApi.getProgress(projectId),
        ])
        setTasks(taskList)
        setProgress(prog)
        setTaskError(null)
      } catch {
        setTaskError('Aufgaben/Fortschritt konnten nicht geladen werden (keine Mitgliedschaft?).')
      }
      if (found.leiter.username === user?.username) {
        setAllUsers(await listUsers())
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Laden fehlgeschlagen.')
    } finally {
      setLoading(false)
    }
  }, [projectId, user?.username])

  useEffect(() => {
    load()
  }, [load])

  async function handleStatusChange(task: Task, status: TaskStatus) {
    setTaskError(null)
    try {
      await tasksApi.updateTaskStatus(task.id, status)
      await load()
    } catch (err) {
      setTaskError(err instanceof Error ? err.message : 'Statuswechsel fehlgeschlagen.')
    }
  }

  async function handleCreateTask(input: TaskInput) {
    await tasksApi.createTask(projectId, input)
    setShowNewTask(false)
    await load()
  }

  async function handleUpdateTask(input: TaskInput) {
    if (!editingTask) return
    await tasksApi.updateTask(editingTask.id, input)
    setEditingTask(null)
    await load()
  }

  async function handleAddMember() {
    if (!newMemberId) return
    try {
      await projectsApi.addMember(projectId, Number(newMemberId))
      setNewMemberId('')
      await load()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Hinzufuegen fehlgeschlagen.')
    }
  }

  async function handleRemoveMember(userId: number) {
    try {
      await projectsApi.removeMember(projectId, userId)
      await load()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Entfernen fehlgeschlagen.')
    }
  }

  if (loading) return <p>Lädt…</p>
  if (!project) {
    return (
      <section>
        <p role="alert" className="error">
          {error}
        </p>
        <Link to="/projects">Zurück zur Projektliste</Link>
      </section>
    )
  }

  const assignableUsers = allUsers.filter(
    (u) => u.id !== project.leiter.id && !project.members.some((m) => m.id === u.id),
  )

  return (
    <section>
      <p>
        <Link to="/projects">← Projekte</Link>
      </p>
      <div className="detail-head">
        <h1>{project.name}</h1>
        <StatusBadge status={project.status} />
      </div>
      {project.beschreibung && <p>{project.beschreibung}</p>}
      <p className="muted">Leiter: {project.leiter.username}</p>

      {error && (
        <p role="alert" className="error">
          {error}
        </p>
      )}

      {progress && (
        <div className="card">
          <h3>Fortschritt</h3>
          <ProgressBar percent={progress.percentDone} />
          <p className="muted">
            {progress.doneTasks} von {progress.totalTasks} Aufgaben erledigt
          </p>
        </div>
      )}

      <div className="card">
        <h3>Mitglieder</h3>
        <ul className="member-list">
          {project.members.length === 0 && <li className="muted">Keine Mitglieder.</li>}
          {project.members.map((m) => (
            <li key={m.id}>
              {m.username}
              {isLeiter && (
                <button
                  type="button"
                  className="link-button"
                  onClick={() => handleRemoveMember(m.id)}
                >
                  entfernen
                </button>
              )}
            </li>
          ))}
        </ul>
        {isLeiter && (
          <div className="member-add">
            <select value={newMemberId} onChange={(e) => setNewMemberId(e.target.value)}>
              <option value="">Mitarbeitende auswählen…</option>
              {assignableUsers.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.username} ({u.role})
                </option>
              ))}
            </select>
            <button type="button" onClick={handleAddMember} disabled={!newMemberId}>
              Hinzufügen
            </button>
          </div>
        )}
      </div>

      <div className="detail-head">
        <h2>Aufgaben</h2>
        {canEditTasks && !showNewTask && !editingTask && (
          <button type="button" onClick={() => setShowNewTask(true)}>
            Neue Aufgabe
          </button>
        )}
      </div>

      {taskError && (
        <p role="alert" className="error">
          {taskError}
        </p>
      )}

      {showNewTask && (
        <TaskForm
          assignees={assignees}
          initial={null}
          onSubmit={handleCreateTask}
          onCancel={() => setShowNewTask(false)}
        />
      )}
      {editingTask && (
        <TaskForm
          assignees={assignees}
          initial={editingTask}
          onSubmit={handleUpdateTask}
          onCancel={() => setEditingTask(null)}
        />
      )}

      {tasks.length === 0 ? (
        <p className="muted">Keine Aufgaben.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Titel</th>
              <th>Bearbeiter</th>
              <th>Status</th>
              {canEditTasks && <th></th>}
            </tr>
          </thead>
          <tbody>
            {tasks.map((task) => (
              <tr key={task.id}>
                <td>
                  <strong>{task.titel}</strong>
                  {task.beschreibung && <div className="muted">{task.beschreibung}</div>}
                </td>
                <td>{task.bearbeiter?.username ?? '—'}</td>
                <td>
                  {canEditTasks ? (
                    <select
                      value={task.status}
                      onChange={(e) => handleStatusChange(task, e.target.value as TaskStatus)}
                    >
                      {TASK_STATUSES.map((s) => (
                        <option key={s} value={s}>
                          {TASK_STATUS_LABEL[s]}
                        </option>
                      ))}
                    </select>
                  ) : (
                    <StatusBadge status={task.status} />
                  )}
                </td>
                {canEditTasks && (
                  <td>
                    <button
                      type="button"
                      className="secondary"
                      onClick={() => {
                        setShowNewTask(false)
                        setEditingTask(task)
                      }}
                    >
                      Bearbeiten
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}

function dedupeById(users: User[]): User[] {
  const seen = new Set<number>()
  return users.filter((u) => (seen.has(u.id) ? false : seen.add(u.id)))
}
