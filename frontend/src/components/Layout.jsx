import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const navLinkClass = ({ isActive }) =>
  ['nav-link', isActive ? 'active' : ''].filter(Boolean).join(' ')

export default function Layout({ children }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/')
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand-wrap">
          <Link to="/" className="brand">ServiceLink</Link>
        </div>

        <nav className="main-nav" aria-label="Main navigation">
          <NavLink to="/" className={navLinkClass}>Home</NavLink>

          {user?.role === 'PROVIDER' && (
            <NavLink to="/provider/dashboard" className={navLinkClass}>Provider Dashboard</NavLink>
          )}

          {user?.role === 'ADMIN' && (
            <NavLink to="/admin/dashboard" className={navLinkClass}>Admin Dashboard</NavLink>
          )}
        </nav>

        <div className="nav-actions">
          {!user ? (
            <>
              <Link to="/login" className="btn btn-secondary">Login</Link>
              <Link to="/register" className="btn btn-primary">Register</Link>
            </>
          ) : (
            <>
              <span className="user-chip">{user.username}</span>
              <button type="button" className="btn btn-secondary" onClick={handleLogout}>Logout</button>
            </>
          )}
        </div>
      </header>

      <main className="main-content">{children}</main>
    </div>
  )
}
