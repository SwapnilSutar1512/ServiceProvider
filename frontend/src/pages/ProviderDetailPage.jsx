import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import api from '../api/client'

const initialPayload = {
  providerId: '',
  serviceId: '',
  areaId: '',
  name: '',
  mobile: '',
  description: '',
  preferredDate: '',
  preferredTime: '09:00',
  address: '',
  photoUrl: '',
}

export default function ProviderDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [provider, setProvider] = useState(null)
  const [categories, setCategories] = useState([])
  const [areas, setAreas] = useState([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState(initialPayload)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [providerResponse, categoriesResponse, areasResponse] = await Promise.all([
          api.get(`/public/providers/${id}`),
          api.get('/public/categories'),
          api.get('/public/areas'),
        ])

        const providerPayload = providerResponse?.data?.data
        const categoryPayload = categoriesResponse?.data?.data || []
        const areaPayload = areasResponse?.data?.data || []

        setProvider(providerPayload)
        setCategories(categoryPayload)
        setAreas(areaPayload)

        const matchedCategory = categoryPayload.find((category) => category.categoryName === providerPayload?.categoryName)
        setForm((current) => ({
          ...current,
          providerId: providerPayload?.id,
          serviceId: matchedCategory?.id || '',
          areaId: areaPayload[0]?.id || '',
          name: '',
          mobile: '',
          description: '',
          preferredDate: '',
          preferredTime: '09:00',
          address: '',
          photoUrl: '',
        }))
      } catch (fetchError) {
        console.error('Failed to load provider details', fetchError)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [id])

  const categoryOptions = useMemo(
    () => categories.map((category) => ({ value: category.id, label: category.categoryName })),
    [categories],
  )

  const existingProvider = provider || {}
  const phoneNumber = String(existingProvider.phoneNumber || '').replace(/[^\d+]/g, '')
  const whatsappDigits = phoneNumber.replace(/\D/g, '')
  const whatsappNumber = whatsappDigits.length === 10 ? `91${whatsappDigits}` : whatsappDigits
  const mapQuery = existingProvider.latitude != null && existingProvider.longitude != null
    ? `${existingProvider.latitude},${existingProvider.longitude}`
    : `${existingProvider.locality || ''}, ${existingProvider.city || ''}`

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    setSuccess('')

    try {
      const response = await api.post('/public/service-requests', {
        ...form,
        providerId: Number(form.providerId),
        serviceId: Number(form.serviceId),
        areaId: Number(form.areaId),
      })

      setSuccess(response?.data?.message || 'Service request submitted successfully')
      setForm((current) => ({ ...current, description: '', preferredDate: '', preferredTime: '09:00', address: '', name: '', mobile: '' }))
      setTimeout(() => navigate('/'), 1500)
    } catch (submitError) {
      setError(submitError?.response?.data?.message || 'Unable to submit service request.')
    }
  }

  if (loading) {
    return <div className="page-shell"><div className="card loading-card">Loading provider profile...</div></div>
  }

  return (
    <div className="page-shell detail-shell">
      <div className="card detail-card">
        <p className="eyebrow">Professional profile</p>
        <h1>{existingProvider.businessName}</h1>
        <div className="profile-meta">
          <span>⭐ {existingProvider.rating || '4.8'}</span>
          <span>{existingProvider.categoryName}</span>
          <span>{existingProvider.city}</span>
          <span>{existingProvider.locality}</span>
        </div>

        <div className="profile-details">
          <p><strong>Full name:</strong> {existingProvider.fullName}</p>
          <p><strong>Working hours:</strong> {existingProvider.workingHours}</p>
          <p><strong>Experience:</strong> {existingProvider.experience}</p>
          <p><strong>Service areas:</strong> {existingProvider.serviceAreas?.join(', ')}</p>
        </div>
        <div className="provider-card-actions">
          {phoneNumber && <a className="btn btn-primary" href={`tel:${phoneNumber}`}>Call provider</a>}
          {whatsappNumber && (
            <a className="btn btn-secondary" href={`https://wa.me/${whatsappNumber}`} target="_blank" rel="noreferrer">
              WhatsApp
            </a>
          )}
          <a
            className="btn btn-secondary"
            href={`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(mapQuery)}`}
            target="_blank"
            rel="noreferrer"
          >
            View map
          </a>
        </div>
      </div>

      <div className="card form-card">
        <h2>Request a service</h2>

        <form onSubmit={handleSubmit} className="stacked-form">
          <div className="form-grid two-col-grid">
            <label>
              Your name
              <input type="text" value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} required />
            </label>

            <label>
              Mobile number
              <input type="tel" value={form.mobile} onChange={(event) => setForm({ ...form, mobile: event.target.value })} required />
            </label>
          </div>

          <div className="form-grid two-col-grid">
            <label>
              Service
              <select value={form.serviceId} onChange={(event) => setForm({ ...form, serviceId: event.target.value })} required>
                <option value="">Select a service</option>
                {categoryOptions.map((category) => (
                  <option key={category.value} value={category.value}>{category.label}</option>
                ))}
              </select>
            </label>

            <label>
              Area
              <select value={form.areaId} onChange={(event) => setForm({ ...form, areaId: event.target.value })} required>
                <option value="">Select an area</option>
                {areas.map((area) => (
                  <option key={area.id} value={area.id}>{area.name} ({area.city})</option>
                ))}
              </select>
            </label>
          </div>

          <div className="form-grid two-col-grid">
            <label>
              Preferred date
              <input type="date" value={form.preferredDate} onChange={(event) => setForm({ ...form, preferredDate: event.target.value })} required />
            </label>

            <label>
              Preferred time
              <input type="time" value={form.preferredTime} onChange={(event) => setForm({ ...form, preferredTime: event.target.value })} required />
            </label>
          </div>

          <label>
            Address
            <textarea value={form.address} onChange={(event) => setForm({ ...form, address: event.target.value })} required />
          </label>

          <label>
            Job description
            <textarea value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} rows="4" required />
          </label>

          <label>
            Photo URL (optional)
            <input type="text" value={form.photoUrl} onChange={(event) => setForm({ ...form, photoUrl: event.target.value })} placeholder="https://example.com/service-photo.jpg" />
          </label>

          {error && <div className="error-banner">{error}</div>}
          {success && <div className="success-banner">{success}</div>}

          <button type="submit" className="btn btn-primary full-width">Submit request</button>
        </form>
      </div>
    </div>
  )
}
