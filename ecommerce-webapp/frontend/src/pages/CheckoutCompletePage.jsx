import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'

export default function CheckoutCompletePage() {
  const navigate = useNavigate()

  return (
    <>
      <Header />
      <div className="checkout-complete-wrapper">
        <div className="checkout-complete-container">
          <div className="success-animation">
            <div className="checkmark">✓</div>
          </div>

          <div className="success-content">
            <h1 data-testid="success-header">Order Confirmed!</h1>
            <p className="success-message">Your order has been successfully placed and dispatched.</p>
            
            <div className="order-details">
              <p>Thank you for shopping with us! 🎉</p>
              <p className="order-reference">Order ID: #<span>{Math.random().toString(36).substr(2, 9).toUpperCase()}</span></p>
            </div>

            <div className="next-steps">
              <h3>What's Next?</h3>
              <ul>
                <li>📧 Confirmation email has been sent</li>
                <li>📦 Your order is being prepared</li>
                <li>🚚 Tracking info will be provided soon</li>
              </ul>
            </div>

            <button 
              data-testid="back-home-button" 
              className="home-button"
              onClick={() => navigate('/inventory')}
            >
              🏠 Back to Shopping
            </button>
          </div>
        </div>
      </div>
    </>
  )
}
