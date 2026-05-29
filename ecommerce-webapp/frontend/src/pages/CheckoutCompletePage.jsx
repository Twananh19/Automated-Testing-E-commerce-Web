import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'

export default function CheckoutCompletePage() {
  const navigate = useNavigate()

  return (
    <>
      <Header />
      <div className="checkout-complete-page">
        <h2 data-testid="success-header">Thank you for your order!</h2>
        <p>Your order has been dispatched.</p>
        <button data-testid="back-home-button" onClick={() => navigate('/inventory')}>
          Back Home
        </button>
      </div>
    </>
  )
}
