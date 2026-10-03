import { useEffect, useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import api from '../api/client'
import { useAuth } from '../context/AuthContext'
import { getApiErrorMessage, getFieldErrorsFromMessage } from '../utils/formErrors'

const initialCustomer = { username: '', email: '', password: '' }
const initialProvider = {
  username: '',
  email: '',
  password: '',
  name: '',
  mobile: '',
  businessName: '',
  categoryId: '',
  experience: '',
  street: '',
  locality: '',
  city: '',
  state: '',
  pincode: '',
  latitude: '',
  longitude: '',
  workingHours: '',
}

const customerFieldRules = {
  username: ['username', 'already exists'],
  email: ['email'],
  password: ['password', 'at least 6'],
}

const providerFieldRules = {
  username: ['username', 'already exists'],
  email: ['email'],
  password: ['password', 'at least 6'],
  name: ['full name', 'name is required'],
  mobile: ['mobile'],
  businessName: ['business name'],
  categoryId: ['category'],
  experience: ['experience'],
  street: ['street'],
  locality: ['locality'],
  city: ['city'],
  state: ['state'],
  pincode: ['pincode'],
}

export default function RegisterPage() {
  const { user, register } = useAuth()
  const navigate = useNavigate()
  const [accountType, setAccountType] = useState('customer')
  const [customerForm, setCustomerForm] = useState(initialCustomer)
  const [providerForm, setProviderForm] = useState(initialProvider)
  const [customerErrors, setCustomerErrors] = useState({})
  const [providerErrors, setProviderErrors] = useState({})
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [categories, setCategories] = useState([])
  const [locationStatus, setLocationStatus] = useState('')

  useEffect(() => {
    const loadCategories = async () => {
      try {
        const response = await api.get('/public/categories')
        const list = response?.data?.data || response?.data || []
        setCategories(Array.isArray(list) ? list : [])
      } catch (loadError) {
        console.warn('Unable to load categories', loadError)
      }
    }

    loadCategories()
  }, [])

  if (user) {
    return <Navigate to={user.role === 'ADMIN' ? '/admin/dashboard' : user.role === 'PROVIDER' ? '/provider/dashboard' : '/'} replace />
  }

  const handleCustomerSubmit = async (event) => {
    event.preventDefault()
    setSubmitting(true)
    setError('')
    setSuccess('')
    setCustomerErrors({})

    try {
      const response = await register('/auth/register/customer', customerForm)
      setSuccess(response?.message || 'Customer registered successfully')
      setCustomerForm(initialCustomer)
      setTimeout(() => navigate('/login'), 1200)
    } catch (registerError) {
      const message = getApiErrorMessage(registerError, 'Unable to register customer.')
      setError(message)
      setCustomerErrors(getFieldErrorsFromMessage(message, customerFieldRules))
    } finally {
      setSubmitting(false)
    }
  }

  const handleProviderSubmit = async (event) => {
    event.preventDefault()
    setSubmitting(true)
    setError('')
    setSuccess('')
    setProviderErrors({})

    try {
      const payload = {
        ...providerForm,
        categoryId: Number(providerForm.categoryId),
        experience: Number(providerForm.experience),
        latitude: providerForm.latitude === '' ? null : Number(providerForm.latitude),
        longitude: providerForm.longitude === '' ? null : Number(providerForm.longitude),
      }

      const response = await register('/auth/register/provider', payload)
      setSuccess(response?.message || 'Provider registration sent.')
      setProviderForm(initialProvider)
      setTimeout(() => navigate('/login'), 1500)
    } catch (registerError) {
      const message = getApiErrorMessage(registerError, 'Unable to register provider.')
      setError(message)
      setProviderErrors(getFieldErrorsFromMessage(message, providerFieldRules))
    } finally {
      setSubmitting(false)
    }
  }

  const captureProviderLocation = () => {
    if (!navigator.geolocation) {
      setLocationStatus('Location is not supported by this browser. You can still submit your address.')
      return
    }

    setLocationStatus('Waiting for location permission...')
    navigator.geolocation.getCurrentPosition(
      ({ coords }) => {
        setProviderForm((current) => ({
          ...current,
          latitude: String(coords.latitude),
          longitude: String(coords.longitude),
        }))
        setLocationStatus('Exact location captured and will be saved with your application.')
      },
      (locationError) => {
        setLocationStatus(locationError.code === locationError.PERMISSION_DENIED
          ? 'Location permission was denied. You can still register using your address.'
          : 'Could not get your location. Check device location services and try again.')
      },
      { enableHighAccuracy: true, timeout: 15000, maximumAge: 0 },
    )
  }

  const renderCustomerForm = () => (
    <form onSubmit={handleCustomerSubmit} className="stacked-form">
      <label>
        Username
        <input
          type="text"
          value={customerForm.username}
          onChange={(event) => setCustomerForm({ ...customerForm, username: event.target.value })}
          className={customerErrors.username ? 'input-error' : ''}
          required
        />
        {customerErrors.username && <span className="field-error">Username already exists or is invalid.</span>}
      </label>

      <label>
        Email
        <input
          type="email"
          value={customerForm.email}
          onChange={(event) => setCustomerForm({ ...customerForm, email: event.target.value })}
          className={customerErrors.email ? 'input-error' : ''}
          required
        />
        {customerErrors.email && <span className="field-error">Please enter a valid email address.</span>}
      </label>

      <label>
        Password
        <input
          type="password"
          value={customerForm.password}
          onChange={(event) => setCustomerForm({ ...customerForm, password: event.target.value })}
          className={customerErrors.password ? 'input-error' : ''}
          required
        />
        {customerErrors.password && <span className="field-error">Password must be at least 6 characters long.</span>}
      </label>

      {error && <div className="error-banner">{error}</div>}
      {success && <div className="success-banner">{success}</div>}

      <button type="submit" className="btn btn-primary full-width" disabled={submitting}>
        {submitting ? 'Registering...' : 'Register as customer'}
      </button>
    </form>
  )

  const renderProviderForm = () => (
    <form onSubmit={handleProviderSubmit} className="stacked-form">
      <label>
        Username
        <input
          type="text"
          value={providerForm.username}
          onChange={(event) => setProviderForm({ ...providerForm, username: event.target.value })}
          className={providerErrors.username ? 'input-error' : ''}
          required
        />
        {providerErrors.username && <span className="field-error">Username already exists or is invalid.</span>}
      </label>

      <label>
        Full Name
        <input
          type="text"
          value={providerForm.name}
          onChange={(event) => setProviderForm({ ...providerForm, name: event.target.value })}
          className={providerErrors.name ? 'input-error' : ''}
          required
        />
        {providerErrors.name && <span className="field-error">Full name is required.</span>}
      </label>

      <label>
        Email
        <input
          type="email"
          value={providerForm.email}
          onChange={(event) => setProviderForm({ ...providerForm, email: event.target.value })}
          className={providerErrors.email ? 'input-error' : ''}
          required
        />
        {providerErrors.email && <span className="field-error">Please enter a valid email address.</span>}
      </label>

      <label>
        Mobile
        <input
          type="tel"
          value={providerForm.mobile}
          onChange={(event) => setProviderForm({ ...providerForm, mobile: event.target.value })}
          className={providerErrors.mobile ? 'input-error' : ''}
          required
        />
        {providerErrors.mobile && <span className="field-error">Mobile number must be exactly 10 digits.</span>}
      </label>

      <label>
        Business Name
        <input
          type="text"
          value={providerForm.businessName}
          onChange={(event) => setProviderForm({ ...providerForm, businessName: event.target.value })}
          className={providerErrors.businessName ? 'input-error' : ''}
          required
        />
        {providerErrors.businessName && <span className="field-error">Business name is required.</span>}
      </label>

      <label>
        Service Category
        <select
          value={providerForm.categoryId}
          onChange={(event) => setProviderForm({ ...providerForm, categoryId: event.target.value })}
          className={providerErrors.categoryId ? 'input-error' : ''}
          required
        >
          <option value="">Select a category</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>{category.categoryName}</option>
          ))}
        </select>
        {providerErrors.categoryId && <span className="field-error">Please select a valid category.</span>}
      </label>

      <label>
        Experience (years)
        <input
          type="number"
          min="0"
          value={providerForm.experience}
          onChange={(event) => setProviderForm({ ...providerForm, experience: event.target.value })}
          className={providerErrors.experience ? 'input-error' : ''}
          required
        />
        {providerErrors.experience && <span className="field-error">Experience is required and must be 0 or more.</span>}
      </label>

      <label>
        Street Address
        <input
          type="text"
          value={providerForm.street}
          onChange={(event) => setProviderForm({ ...providerForm, street: event.target.value })}
          className={providerErrors.street ? 'input-error' : ''}
          required
        />
        {providerErrors.street && <span className="field-error">Street address is required.</span>}
      </label>

      <label>
        Locality
        <input
          type="text"
          value={providerForm.locality}
          onChange={(event) => setProviderForm({ ...providerForm, locality: event.target.value })}
          className={providerErrors.locality ? 'input-error' : ''}
          required
        />
        {providerErrors.locality && <span className="field-error">Locality is required.</span>}
      </label>

      <div className="two-column-grid">
        <label>
          City
          <input
            type="text"
            value={providerForm.city}
            onChange={(event) => setProviderForm({ ...providerForm, city: event.target.value })}
            className={providerErrors.city ? 'input-error' : ''}
            required
          />
          {providerErrors.city && <span className="field-error">City is required.</span>}
        </label>

        <label>
          State
          <input
            type="text"
            value={providerForm.state}
            onChange={(event) => setProviderForm({ ...providerForm, state: event.target.value })}
            className={providerErrors.state ? 'input-error' : ''}
            required
          />
          {providerErrors.state && <span className="field-error">State is required.</span>}
        </label>
      </div>

      <label>
        Pincode
        <input
          type="text"
          value={providerForm.pincode}
          onChange={(event) => setProviderForm({ ...providerForm, pincode: event.target.value })}
          className={providerErrors.pincode ? 'input-error' : ''}
          required
        />
        {providerErrors.pincode && <span className="field-error">Pincode must be exactly 6 digits.</span>}
      </label>

      <div>
        <button type="button" className="btn btn-secondary" onClick={captureProviderLocation}>
          Use my current location
        </button>
        {locationStatus && <p className="helper-text" role="status">{locationStatus}</p>}
        {providerForm.latitude && providerForm.longitude && (
          <p className="helper-text">
            Coordinates captured: {Number(providerForm.latitude).toFixed(5)}, {Number(providerForm.longitude).toFixed(5)}
          </p>
        )}
      </div>

      <label>
        Working Hours
        <input
          type="text"
          value={providerForm.workingHours}
          onChange={(event) => setProviderForm({ ...providerForm, workingHours: event.target.value })}
          placeholder="e.g. 9:00 AM - 7:00 PM"
        />
      </label>

      <label>
        Password
        <input
          type="password"
          value={providerForm.password}
          onChange={(event) => setProviderForm({ ...providerForm, password: event.target.value })}
          className={providerErrors.password ? 'input-error' : ''}
          required
        />
        {providerErrors.password && <span className="field-error">Password must be at least 6 characters long.</span>}
      </label>

      {error && <div className="error-banner">{error}</div>}
      {success && <div className="success-banner">{success}</div>}

      <button type="submit" className="btn btn-primary full-width" disabled={submitting}>
        {submitting ? 'Submitting...' : 'Register as provider'}
      </button>
    </form>
  )

  return (
    <div className="page-shell narrow-shell">
      <div className="card auth-card">
        <p className="eyebrow">Join ServiceLink</p>
        <h1>Create your account</h1>

        <div className="segmented-control">
          <button type="button" className={accountType === 'customer' ? 'active' : ''} onClick={() => setAccountType('customer')}>Customer</button>
          <button type="button" className={accountType === 'provider' ? 'active' : ''} onClick={() => setAccountType('provider')}>Provider</button>
        </div>

        {accountType === 'customer' ? renderCustomerForm() : renderProviderForm()}

        <p className="helper-text">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  )
}
