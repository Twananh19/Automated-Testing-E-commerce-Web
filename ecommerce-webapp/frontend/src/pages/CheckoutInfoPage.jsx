import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'

export default function CheckoutInfoPage() {
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [zipCode, setZipCode] = useState('')
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const navigate = useNavigate()

  const handleContinue = async (e) => {
    e.preventDefault()
    setError('')
    
    if (!firstName.trim()) return setError('First Name is required')
    if (!lastName.trim()) return setError('Last Name is required')
    if (!zipCode.trim()) return setError('Postal Code is required')

    setIsSubmitting(true)
    // Simulate validation delay
    setTimeout(() => {
      sessionStorage.setItem('checkoutInfo', JSON.stringify({ firstName, lastName, zipCode }))
      navigate('/checkout-overview')
    }, 500)
  }

  return (
    <>
      <Header />
      <div className="checkout-info-wrapper">
        <div className="checkout-info-container">
          <div className="checkout-progress">
            <div className="progress-step active">
              <span className="step-number">1</span>
              <span className="step-label">Shipping Info</span>
            </div>
            <div className="progress-line"></div>
            <div className="progress-step">
              <span className="step-number">2</span>
              <span className="step-label">Review Order</span>
            </div>
            <div className="progress-line"></div>
            <div className="progress-step">
              <span className="step-number">3</span>
              <span className="step-label">Confirm</span>
            </div>
          </div>

          <div className="checkout-form-section">
            <div className="form-header">
              <h1>📍 Shipping Information</h1>
              <p>Please enter your delivery details</p>
            </div>

            {error && (
              <div className="error-message" data-testid="error-message">
                <span className="error-icon">⚠️</span>
                <span>{error}</span>
              </div>
            )}

            <form onSubmit={handleContinue} className="checkout-form">
              <div className="form-group">
                <label htmlFor="firstName">First Name</label>
                <input
                  id="firstName"
                  data-testid="first-name"
                  type="text"
                  placeholder="John"
                  value={firstName}
                  onChange={e => setFirstName(e.target.value)}
                  disabled={isSubmitting}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="lastName">Last Name</label>
                <input
                  id="lastName"
                  data-testid="last-name"
                  type="text"
                  placeholder="Doe"
                  value={lastName}
                  onChange={e => setLastName(e.target.value)}
                  disabled={isSubmitting}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="zipCode">Postal Code</label>
                <input
                  id="zipCode"
                  data-testid="postal-code"
                  type="text"
                  placeholder="12345"
                  value={zipCode}
                  onChange={e => setZipCode(e.target.value)}
                  disabled={isSubmitting}
                  required
                />
              </div>

              <button 
                data-testid="continue-button" 
                type="submit"
                className="submit-button"
                disabled={isSubmitting}
              >
                {isSubmitting ? '⏳ Processing...' : '✓ Continue to Review'}
              </button>
            </form>

            <button 
              className="back-button-link"
              onClick={() => navigate('/cart')}
            >
              ← Back to Cart
            </button>
          </div>
        </div>
      </div>
    </>
  )
}
