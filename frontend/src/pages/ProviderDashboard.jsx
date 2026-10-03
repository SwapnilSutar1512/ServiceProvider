import { useEffect, useState } from 'react'
import api from '../api/client'

export default function ProviderDashboard() {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)

  const loadRequests = async () => {
    const response = await api.get('/provider/service-requests')
    const payload = response?.data?.data || []
    setRequests(payload)
  }

  useEffect(() => {
    loadRequests()
      .catch((error) => console.error('Failed to load provider requests', error))
      .finally(() => setLoading(false))
  }, [])

  const updateStatus = async (requestId, action, reason) => {
    try {
      if (action === 'reject') {
        await api.patch(`/provider/service-requests/${requestId}/reject`, { reason: reason || 'Not suitable for this request' })
      } else if (action === 'accept') {
        await api.patch(`/provider/service-requests/${requestId}/accept`)
      } else if (action === 'complete') {
        await api.patch(`/provider/service-requests/${requestId}/complete`)
      }

      await loadRequests()
    } catch (error) {
      console.error('Request update failed', error)
    }
  }

  const handleReject = async (requestId) => {
    const response = window.prompt('Reason for rejection', 'Please contact the customer for more details.')
    if (response !== null) {
      await updateStatus(requestId, 'reject', response)
    }
  }

  return (
    <div className="page-shell">
      <div className="section-header-row">
        <h1>Provider dashboard</h1>
      </div>

      {loading ? (
        <div className="card loading-card">Loading requests...</div>
      ) : (
        <div className="stacked-list">
          {requests.length === 0 ? (
            <div className="card empty-state">No service requests found.</div>
          ) : (
            requests.map((request) => (
              <div className="card request-card" key={request.requestId || request.id}>
                <div className="request-heading">
                  <div>
                    <p className="eyebrow">{request.requestId}</p>
                    <h3>{request.customerName}</h3>
                  </div>
                  <span className={`status-badge ${String(request.status).toLowerCase()}`}>{request.status}</span>
                </div>

                <div className="meta-grid">
                  <span>{request.categoryName}</span>
                  <span>{request.areaName}</span>
                  <span>{request.preferredDate}</span>
                  <span>{request.preferredTime}</span>
                </div>

                <p>{request.description}</p>

                {request.rejectReason && <p className="reject-reason">Reason: {request.rejectReason}</p>}

                <div className="inline-actions">
                  <button type="button" className="btn btn-primary" onClick={() => updateStatus(request.requestId, 'accept')}>Accept</button>
                  <button type="button" className="btn btn-secondary" onClick={() => handleReject(request.requestId)}>Reject</button>
                  <button type="button" className="btn btn-secondary" onClick={() => updateStatus(request.requestId, 'complete')}>Complete</button>
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  )
}
