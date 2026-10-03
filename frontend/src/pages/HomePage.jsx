import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/client'

const initialFilters = { categoryId: '', city: '', locality: '' }

export default function HomePage() {
  const [categories, setCategories] = useState([])
  const [areas, setAreas] = useState([])
  const [providers, setProviders] = useState([])
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState(initialFilters)
  const [customerLocation, setCustomerLocation] = useState(null)
  const [locationStatus, setLocationStatus] = useState('')

  const loadCategories = async () => {
    const response = await api.get('/public/categories')
    return response?.data?.data || []
  }

  const loadAreas = async () => {
    const response = await api.get('/public/areas')
    return response?.data?.data || []
  }

  const loadProviders = async (nextFilters = filters, location = customerLocation) => {
    const params = new URLSearchParams()
    if (nextFilters.categoryId) params.append('categoryId', nextFilters.categoryId)
    if (nextFilters.city) params.append('city', nextFilters.city)
    if (nextFilters.locality) params.append('locality', nextFilters.locality)
    if (location) {
      params.append('latitude', location.latitude)
      params.append('longitude', location.longitude)
    }
    params.append('page', '0')
    params.append('size', '12')

    const response = await api.get(`/public/providers?${params.toString()}`)
    const payload = response?.data?.data
    return payload?.content || payload || []
  }

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [categoryResults, areaResults] = await Promise.all([
          loadCategories(),
          loadAreas(),
        ])
        setCategories(categoryResults)
        setAreas(areaResults)
        const providerResults = await loadProviders(initialFilters)
        setProviders(providerResults)
      } catch (error) {
        console.error('Failed to load homepage data', error)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [])

  const cityOptions = useMemo(
    () => [...new Set(areas.map((area) => area.city).filter(Boolean))],
    [areas],
  )

  const localityOptions = useMemo(
    () => [...new Set(areas.map((area) => area.name).filter(Boolean))],
    [areas],
  )

  const handleFilterChange = (event) => {
    const { name, value } = event.target
    const nextFilters = { ...filters, [name]: value }
    setFilters(nextFilters)

    if (name === 'city' && value) {
      const cityLocalities = areas.filter((area) => area.city === value).map((area) => area.name)
      if (!cityLocalities.includes(filters.locality)) {
        nextFilters.locality = ''
      }
    }

    loadProviders(nextFilters)
      .then(setProviders)
      .catch((error) => console.error('Failed to load providers', error))
  }

  const clearFilters = () => {
    setFilters(initialFilters)
    loadProviders(initialFilters)
      .then(setProviders)
      .catch((error) => console.error('Failed to clear filters', error))
  }

  const findNearbyProviders = () => {
    if (!navigator.geolocation) {
      setLocationStatus('Location is not supported by this browser.')
      return
    }

    setLocationStatus('Finding providers near you...')
    navigator.geolocation.getCurrentPosition(
      ({ coords }) => {
        const location = { latitude: coords.latitude, longitude: coords.longitude }
        setCustomerLocation(location)
        loadProviders(filters, location)
          .then((nearbyProviders) => {
            setProviders(nearbyProviders)
            setLocationStatus('Nearby providers are shown first, closest to your current location.')
          })
          .catch(() => setLocationStatus('Could not load nearby providers. Please try again.'))
      },
      (locationError) => {
        setLocationStatus(locationError.code === locationError.PERMISSION_DENIED
          ? 'Location permission was denied. Use the city and locality filters instead.'
          : 'Could not get your location. Check device location services and try again.')
      },
      { enableHighAccuracy: true, timeout: 15000, maximumAge: 0 },
    )
  }

  return (
    <div className="page-shell">
      <section className="hero-section">
        <div>
          <p className="eyebrow">Trusted local experts</p>
          <h1>Book dependable services right in your neighborhood.</h1>
          <p className="lead-text">
            Browse verified providers, filter by location, and submit your service requests in minutes.
          </p>
        </div>
        <div className="hero-panel card">
          <h3>Quick filters</h3>
          <div className="form-grid compact-grid">
            <label>
              Service
              <select name="categoryId" value={filters.categoryId} onChange={handleFilterChange}>
                <option value="">All services</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>{category.categoryName}</option>
                ))}
              </select>
            </label>

            <label>
              City
              <select name="city" value={filters.city} onChange={handleFilterChange}>
                <option value="">All cities</option>
                {cityOptions.map((city) => (
                  <option key={city} value={city}>{city}</option>
                ))}
              </select>
            </label>

            <label>
              Locality
              <select name="locality" value={filters.locality} onChange={handleFilterChange}>
                <option value="">All localities</option>
                {localityOptions.map((locality) => (
                  <option key={locality} value={locality}>{locality}</option>
                ))}
              </select>
            </label>
          </div>

          <button type="button" className="btn btn-secondary full-width" onClick={clearFilters}>Clear filters</button>
        </div>
      </section>

      <section className="section-block">
        <div className="section-header-row">
          <h2>Top categories</h2>
        </div>

        <div className="category-grid">
          {categories.map((category) => (
            <div className="category-card card" key={category.id}>
              <h3>{category.categoryName}</h3>
              <p>{category.description || 'Verified professional service.'}</p>
            </div>
          ))}
        </div>
      </section>

      <section className="section-block">
        <div className="section-header-row">
          <h2>Available providers</h2>
          <button type="button" className="btn btn-secondary" onClick={findNearbyProviders}>
            Use my location
          </button>
        </div>
        {locationStatus && <p className="helper-text" role="status">{locationStatus}</p>}

        {loading ? (
          <div className="card loading-card">Loading providers...</div>
        ) : (
          <div className="provider-grid">
            {providers.length > 0 ? (
              providers.map((provider) => (
                <div className="provider-card card" key={provider.id}>
                  <div className="provider-header">
                    <div>
                      <p className="badge">{provider.categoryName}</p>
                      <h3>{provider.businessName}</h3>
                    </div>
                    <span className="rating">★ {provider.rating || '4.8'}</span>
                  </div>

                  <p>{provider.city} • {provider.locality}</p>
                  {provider.distanceKm != null && <p>{provider.distanceKm} km away</p>}
                  <p>{provider.workingHours}</p>
                  <p>Experience: {provider.experience || '3+ years'}</p>

                  <div className="provider-card-actions">
                    <Link to={`/providers/${provider.id}`} className="btn btn-primary">View profile</Link>
                  </div>
                </div>
              ))
            ) : (
              <div className="card empty-state">No providers match your current filters.</div>
            )}
          </div>
        )}
      </section>
    </div>
  )
}
