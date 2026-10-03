import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getApiErrorMessage, getFieldErrorsFromMessage } from '../utils/formErrors'

export default function LoginPage() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '' })
  const [fieldErrors, setFieldErrors] = useState({})
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  if (user) {
    return <Navigate to={user.role === 'ADMIN' ? '/admin/dashboard' : user.role === 'PROVIDER' ? '/provider/dashboard' : '/'} replace />
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setSubmitting(true)
    setError('')
    setFieldErrors({})

    try {
      const profile = await login(form)
      const role = String(profile?.role || '').toUpperCase()
      if (role === 'ADMIN') {
        navigate('/admin/dashboard')
      } else if (role === 'PROVIDER') {
        navigate('/provider/dashboard')
      } else {
        navigate('/')
      }
    } catch (loginError) {
      const message = getApiErrorMessage(loginError, 'Login failed. Please try again.')
      setError(message)
      const mappedErrors = getFieldErrorsFromMessage(message, {
        username: ['username', 'user not found'],
        password: ['password', 'invalid credentials'],
      })
      setFieldErrors(mappedErrors)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page-shell narrow-shell">
      <div className="card auth-card">
        <p className="eyebrow">Welcome back</p>
        <h1>Login to ServiceLink</h1>

        <form onSubmit={handleSubmit} className="stacked-form">
          <label>
            Username
            <input
              type="text"
              value={form.username}
              onChange={(event) => setForm({ ...form, username: event.target.value })}
              placeholder="Enter username"
              className={fieldErrors.username ? 'input-error' : ''}
              required
            />
            {fieldErrors.username && <span className="field-error">Check your username and try again.</span>}
          </label>

          <label>
            Password
            <input
              type="password"
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
              placeholder="Enter password"
              className={fieldErrors.password ? 'input-error' : ''}
              required
            />
            {fieldErrors.password && <span className="field-error">Password is incorrect or missing.</span>}
          </label>

          {error && <div className="error-banner">{error}</div>}

          <button type="submit" className="btn btn-primary full-width" disabled={submitting}>
            {submitting ? 'Signing in...' : 'Login'}
          </button>
        </form>

        <p className="helper-text">
          Need an account? <Link to="/register">Create one</Link>
        </p>
      </div>
    </div>
  )
}
