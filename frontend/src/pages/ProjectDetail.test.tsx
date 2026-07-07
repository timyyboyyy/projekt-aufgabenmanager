import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import ProjectDetail from './ProjectDetail'
import { AuthProvider } from '../context/AuthContext'
import * as authApi from '../api/auth'
import * as projectsApi from '../api/projects'
import * as tasksApi from '../api/tasks'
import * as usersApi from '../api/users'
import type { User } from '../api/users'
import type { Project } from '../api/projects'
import type { Task } from '../api/tasks'

vi.mock('../api/auth')
vi.mock('../api/projects')
vi.mock('../api/tasks')
vi.mock('../api/users')

const leiter: User = { id: 1, username: 'pleiter', role: 'PROJEKTLEITER', aktiv: true }
const member: User = { id: 2, username: 'user1', role: 'MITARBEITER', aktiv: true }
const project: Project = {
  id: 1,
  name: 'Website',
  beschreibung: 'Beschreibung',
  status: 'AKTIV',
  leiter,
  members: [member],
}
const task: Task = {
  id: 10,
  titel: 'Aufgabe A',
  beschreibung: null,
  status: 'OFFEN',
  projectId: 1,
  bearbeiter: member,
}

function renderDetail() {
  return render(
    <MemoryRouter initialEntries={['/projects/1']}>
      <AuthProvider>
        <Routes>
          <Route path="/projects/:id" element={<ProjectDetail />} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  )
}

describe('ProjectDetail – Aufgabenstatus umschalten', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.resetAllMocks()
    localStorage.setItem('token', 'x')
    vi.mocked(authApi.getMe).mockResolvedValue({ username: 'user1', role: 'MITARBEITER' })
    vi.mocked(projectsApi.listProjects).mockResolvedValue([project])
    vi.mocked(projectsApi.getProgress).mockResolvedValue({
      projectId: 1,
      totalTasks: 1,
      doneTasks: 0,
      percentDone: 0,
    })
    vi.mocked(tasksApi.listTasks).mockResolvedValue([task])
    vi.mocked(tasksApi.updateTaskStatus).mockResolvedValue({ ...task, status: 'IN_BEARBEITUNG' })
    vi.mocked(usersApi.listUsers).mockResolvedValue([])
  })

  it('ruft updateTaskStatus mit dem neuen Status auf', async () => {
    const user = userEvent.setup()
    renderDetail()

    expect(await screen.findByText('Aufgabe A')).toBeInTheDocument()

    const statusSelect = screen.getByDisplayValue('Offen')
    await user.selectOptions(statusSelect, 'IN_BEARBEITUNG')

    await waitFor(() =>
      expect(tasksApi.updateTaskStatus).toHaveBeenCalledWith(10, 'IN_BEARBEITUNG'),
    )
  })
})
