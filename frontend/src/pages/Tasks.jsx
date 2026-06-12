import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api';
import { useAuth } from '../AuthContext';

const NEXT_STATUS = { TODO: 'IN_PROGRESS', IN_PROGRESS: 'DONE', DONE: 'TODO' };
const STATUS_LABEL = { TODO: 'To do', IN_PROGRESS: 'In progress', DONE: 'Done' };

export default function Tasks() {
  const [tasks, setTasks] = useState([]);
  const [stats, setStats] = useState(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const { logout } = useAuth();
  const navigate = useNavigate();

  async function refresh() {
    const [taskList, taskStats] = await Promise.all([
      api('/tasks'),
      api('/tasks/stats'),
    ]);
    if (taskList) setTasks(taskList);
    if (taskStats) setStats(taskStats);
  }

  useEffect(() => {
    refresh().catch((err) => setError(err.message));
  }, []);

  async function addTask(event) {
    event.preventDefault();
    setError('');
    try {
      await api('/tasks', {
        method: 'POST',
        body: JSON.stringify({ title, description }),
      });
      setTitle('');
      setDescription('');
      await refresh();
    } catch (err) {
      setError(err.message);
    }
  }

  async function cycleStatus(task) {
    await api(`/tasks/${task.id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status: NEXT_STATUS[task.status] }),
    });
    await refresh();
  }

  async function removeTask(id) {
    await api(`/tasks/${id}`, { method: 'DELETE' });
    await refresh();
  }

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <div className="tasks-page">
      <header>
        <h1>TaskFlow</h1>
        <button className="secondary" onClick={handleLogout}>
          Log out
        </button>
      </header>

      {stats && (
        <div className="stats">
          <span>To do: {stats.todo}</span>
          <span>In progress: {stats.inProgress}</span>
          <span>Done: {stats.done}</span>
          <span>Total: {stats.total}</span>
        </div>
      )}

      {error && <p className="error">{error}</p>}

      <form className="card" onSubmit={addTask}>
        <input
          placeholder="Task title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <input
          placeholder="Description (optional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button type="submit">Add task</button>
      </form>

      <ul className="task-list">
        {tasks.map((task) => (
          <li key={task.id} className={`task status-${task.status.toLowerCase()}`}>
            <div>
              <strong>{task.title}</strong>
              {task.description && <p>{task.description}</p>}
            </div>
            <div className="actions">
              <button className="secondary" onClick={() => cycleStatus(task)}>
                {STATUS_LABEL[task.status]}
              </button>
              <button className="danger" onClick={() => removeTask(task.id)}>
                Delete
              </button>
            </div>
          </li>
        ))}
      </ul>
      {tasks.length === 0 && <p className="empty">No tasks yet — add your first one above.</p>}
    </div>
  );
}
