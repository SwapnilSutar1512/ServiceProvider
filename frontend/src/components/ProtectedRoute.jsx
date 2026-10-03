import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function ProtectedRoute({ children, allowedRoles = [] }) {
  const { user, loading } = useAuth()

  if (loading) {
    return <div className="page-shell"><div className="card loading-card">Loading profile...</div></div>
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(String(user.role).toUpperCase())) {
    return <Navigate to="/" replace />
  }

  return children
}
