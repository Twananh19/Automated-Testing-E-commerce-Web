import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'

export default function CheckoutInfoPage() {
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [zipCode, setZipCode] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleContinue = (e) => {
    e.preventDefault()
    if (!firstName) return setError('Error: First Name is required')
    if (!lastName) return setError('Error: Last Name is required')
    if (!zipCode) return setError('Error: Postal Code is required')

    // Lưu thông tin vào sessionStorage để dùng ở trang Overview
    sessionStorage.setItem('checkoutInfo', JSON.stringify({ firstName, lastName, zipCode }))
    navigate('/checkout-overview')
  }

  return (
    <>
      <Header />
      <div className="checkout-info-page">
        <h2>Checkout: Your Information</h2>
        <form onSubmit={handleContinue}>
          <input
            data-testid="first-name"
            type="text"
            placeholder="First Name"
            value={firstName}
            onChange={e => setFirstName(e.target.value)}
          />
          <input
            data-testid="last-name"
            type="text"
            placeholder="Last Name"
            value={lastName}
            onChange={e => setLastName(e.target.value)}
          />
          <input
            data-testid="postal-code"
            type="text"
            placeholder="Zip/Postal Code"
            value={zipCode}
            onChange={e => setZipCode(e.target.value)}
          />
          <button data-testid="continue-button" type="submit">Continue</button>
        </form>
        {error && <p data-testid="error-message" className="error">{error}</p>}
      </div>
    </>
  )
}
