import { useEffect, useState } from 'react'
import api from '../api/client'

const categoryFormInitial = { categoryName: '', description: '', active: true }
const areaFormInitial = { name: '', city: '', state: '', pincode: '', active: true }

export default function AdminDashboard() {
  const [dashboard, setDashboard] = useState(null)
  const [providers, setProviders] = useState([])
  const [categories, setCategories] = useState([])
  const [areas, setAreas] = useState([])
  const [categoryForm, setCategoryForm] = useState(categoryFormInitial)
  const [areaForm, setAreaForm] = useState(areaFormInitial)
  const [loading, setLoading] = useState(true)

  const loadDashboard = async () => {
    const [dashboardResponse, providersResponse, categoriesResponse, areasResponse] = await Promise.all([
      api.get('/admin/dashboard'),
      api.get('/admin/providers'),
      api.get('/admin/categories'),
      api.get('/admin/areas'),
    ])

    setDashboard(dashboardResponse?.data?.data || null)
    setProviders(providersResponse?.data?.data || [])
    setCategories(categoriesResponse?.data?.data || [])
    setAreas(areasResponse?.data?.data || [])
  }

  useEffect(() => {
    loadDashboard()
      .catch((error) => console.error('Failed to load admin data', error))
      .finally(() => setLoading(false))
  }, [])

  const updateProviderStatus = async (providerId, action) => {
    try {
      await api.patch(`/admin/providers/${providerId}/${action}`)
      await loadDashboard()
    } catch (error) {
      console.error('Failed to update provider status', error)
    }
  }

  const createCategory = async (event) => {
    event.preventDefault()
    try {
      await api.post('/admin/categories', categoryForm)
      setCategoryForm(categoryFormInitial)
      await loadDashboard()
    } catch (error) {
      console.error('Failed to add category', error)
    }
  }

  const createArea = async (event) => {
    event.preventDefault()
    try {
      await api.post('/admin/areas', areaForm)
      setAreaForm(areaFormInitial)
      await loadDashboard()
    } catch (error) {
      console.error('Failed to add area', error)
    }
  }

  if (loading) {
    return <div className="page-shell"><div className="card loading-card">Loading admin dashboard...</div></div>
  }

  return (
    <div className="page-shell admin-shell">
      <div className="section-header-row">
        <h1>Admin dashboard</h1>
      </div>

      {dashboard && (
        <div className="stats-grid">
          <div className="card stat-card"><span>Total providers</span><strong>{dashboard.totalProviders}</strong></div>
          <div className="card stat-card"><span>Pending providers</span><strong>{dashboard.pendingProviders}</strong></div>
          <div className="card stat-card"><span>Approved providers</span><strong>{dashboard.approvedProviders}</strong></div>
          <div className="card stat-card"><span>Total requests</span><strong>{dashboard.totalRequests}</strong></div>
          <div className="card stat-card"><span>Pending requests</span><strong>{dashboard.pendingRequests}</strong></div>
          <div className="card stat-card"><span>Completed requests</span><strong>{dashboard.completedRequests}</strong></div>
          <div className="card stat-card"><span>Total categories</span><strong>{dashboard.totalCategories}</strong></div>
          <div className="card stat-card"><span>Total areas</span><strong>{dashboard.totalAreas}</strong></div>
        </div>
      )}

      <div className="admin-columns">
        <div className="card">
          <h2>Providers</h2>
          <div className="stacked-list compact-list">
            {providers.map((provider) => (
              <div className="mini-item" key={provider.id}>
                <div>
                  <strong>{provider.businessName}</strong>
                  <p>{provider.city} • {provider.category?.categoryName || provider.categoryName || 'General'}</p>
                </div>
                <div className="inline-actions">
                  <button type="button" className="btn btn-primary" onClick={() => updateProviderStatus(provider.id, 'approve')}>Approve</button>
                  <button type="button" className="btn btn-secondary" onClick={() => updateProviderStatus(provider.id, 'reject')}>Reject</button>
                  <button type="button" className="btn btn-secondary" onClick={() => updateProviderStatus(provider.id, 'suspend')}>Suspend</button>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <h2>Categories</h2>
          <form onSubmit={createCategory} className="stacked-form small-form">
            <label>
              Category name
              <input type="text" value={categoryForm.categoryName} onChange={(event) => setCategoryForm({ ...categoryForm, categoryName: event.target.value })} required />
            </label>
            <label>
              Description
              <textarea value={categoryForm.description} onChange={(event) => setCategoryForm({ ...categoryForm, description: event.target.value })} rows="3" />
            </label>
            <button type="submit" className="btn btn-primary full-width">Add category</button>
          </form>

          <div className="pill-list">
            {categories.map((category) => (
              <span key={category.id} className="pill">{category.categoryName}</span>
            ))}
          </div>
        </div>

        <div className="card">
          <h2>Areas</h2>
          <form onSubmit={createArea} className="stacked-form small-form">
            <label>
              Area name
              <input type="text" value={areaForm.name} onChange={(event) => setAreaForm({ ...areaForm, name: event.target.value })} required />
            </label>
            <label>
              City
              <input type="text" value={areaForm.city} onChange={(event) => setAreaForm({ ...areaForm, city: event.target.value })} required />
            </label>
            <label>
              State
              <input type="text" value={areaForm.state} onChange={(event) => setAreaForm({ ...areaForm, state: event.target.value })} required />
            </label>
            <label>
              Pincode
              <input type="text" value={areaForm.pincode} onChange={(event) => setAreaForm({ ...areaForm, pincode: event.target.value })} required />
            </label>
            <button type="submit" className="btn btn-primary full-width">Add area</button>
          </form>

          <div className="pill-list">
            {areas.map((area) => (
              <span key={area.id} className="pill">{area.name} ({area.city})</span>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
