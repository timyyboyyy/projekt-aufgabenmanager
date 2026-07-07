import { useState } from 'react'
import type { FormEvent } from 'react'
import type { Task, TaskInput } from '../api/tasks'
import type { User } from '../api/users'

/**
 * Formular zum Anlegen/Bearbeiten einer Aufgabe (US4). Der Bearbeiter kann aus
 * Leiter und Mitgliedern des Projekts gewaehlt werden oder offen bleiben.
 */
export default function TaskForm({
  assignees,
  initial,
  onSubmit,
  onCancel,
}: {
  assignees: User[]
  initial: Task | null
  onSubmit: (input: TaskInput) => Promise<void>
  onCancel: () => void
}) {
  const [titel, setTitel] = useState(initial?.titel ?? '')
  const [beschreibung, setBeschreibung] = useState(initial?.beschreibung ?? '')
  const [bearbeiterId, setBearbeiterId] = useState<string>(
    initial?.bearbeiter ? String(initial.bearbeiter.id) : '',
  )
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await onSubmit({
        titel,
        beschreibung,
        bearbeiterId: bearbeiterId ? Number(bearbeiterId) : null,
      })
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Speichern fehlgeschlagen.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form className="card task-form" onSubmit={handleSubmit}>
      <h3>{initial ? 'Aufgabe bearbeiten' : 'Neue Aufgabe'}</h3>
      <label htmlFor="task-titel">Titel</label>
      <input
        id="task-titel"
        value={titel}
        onChange={(e) => setTitel(e.target.value)}
        required
      />
      <label htmlFor="task-beschreibung">Beschreibung</label>
      <textarea
        id="task-beschreibung"
        value={beschreibung}
        onChange={(e) => setBeschreibung(e.target.value)}
        rows={3}
      />
      <label htmlFor="task-bearbeiter">Bearbeiter</label>
      <select
        id="task-bearbeiter"
        value={bearbeiterId}
        onChange={(e) => setBearbeiterId(e.target.value)}
      >
        <option value="">— nicht zugewiesen —</option>
        {assignees.map((user) => (
          <option key={user.id} value={user.id}>
            {user.username}
          </option>
        ))}
      </select>
      {error && (
        <p role="alert" className="error">
          {error}
        </p>
      )}
      <div className="form-actions">
        <button type="submit" disabled={submitting}>
          {submitting ? 'Speichern…' : 'Speichern'}
        </button>
        <button type="button" className="secondary" onClick={onCancel}>
          Abbrechen
        </button>
      </div>
    </form>
  )
}
