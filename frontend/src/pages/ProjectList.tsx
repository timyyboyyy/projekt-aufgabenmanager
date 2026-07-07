import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import * as projectsApi from '../api/projects'
import type { Project } from '../api/projects'
import StatusBadge from '../components/StatusBadge'

/** US2/US8: berechtigte Projekte auflisten; Projektleiter koennen anlegen und archivieren. */
export default function ProjectList() {
  const { user } = useAuth()
  const isProjektleiter = user?.role === 'PROJEKTLEITER'

  const [projects, setProjects] = useState<Project[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [name, setName] = useState('')
  const [beschreibung, setBeschreibung] = useState('')

  async function reload() {
    setLoading(true)
    try {
      setProjects(await projectsApi.listProjects())
      setError(null)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Laden fehlgeschlagen.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    reload()
  }, [])

  async function handleCreate(event: FormEvent) {
    event.preventDefault()
    try {
      await projectsApi.createProject({ name, beschreibung })
      setName('')
      setBeschreibung('')
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Anlegen fehlgeschlagen.')
    }
  }

  async function handleArchive(id: number) {
    try {
      await projectsApi.archiveProject(id)
      await reload()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Archivieren fehlgeschlagen.')
    }
  }

  return (
    <section>
      <h1>Projekte</h1>
      {error && (
        <p role="alert" className="error">
          {error}
        </p>
      )}

      {isProjektleiter && (
        <form className="card" onSubmit={handleCreate}>
          <h3>Neues Projekt</h3>
          <label htmlFor="p-name">Name</label>
          <input id="p-name" value={name} onChange={(e) => setName(e.target.value)} required />
          <label htmlFor="p-beschreibung">Beschreibung</label>
          <textarea
            id="p-beschreibung"
            value={beschreibung}
            onChange={(e) => setBeschreibung(e.target.value)}
            rows={2}
          />
          <div className="form-actions">
            <button type="submit">Projekt anlegen</button>
          </div>
        </form>
      )}

      {loading ? (
        <p>Lädt…</p>
      ) : projects.length === 0 ? (
        <p>Keine Projekte vorhanden.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Leiter</th>
              <th>Mitglieder</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {projects.map((project) => (
              <tr key={project.id}>
                <td>
                  <Link to={`/projects/${project.id}`}>{project.name}</Link>
                </td>
                <td>{project.leiter.username}</td>
                <td>{project.members.length}</td>
                <td>
                  <StatusBadge status={project.status} />
                </td>
                <td>
                  {isProjektleiter && project.leiter.username === user?.username && (
                    <button
                      type="button"
                      className="secondary"
                      onClick={() => handleArchive(project.id)}
                    >
                      Archivieren
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}
