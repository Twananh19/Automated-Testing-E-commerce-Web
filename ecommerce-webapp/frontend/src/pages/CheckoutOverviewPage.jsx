import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'
import { apiGetCart, apiCreateOrder } from '../services/api'
import { useCart } from '../context/CartContext'

export default function CheckoutOverviewPage() {
  const { cartItems, updateCart, clearCart } = useCart()
  const [total, setTotal] = useState(0)
  const [isProcessing, setIsProcessing] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    apiGetCart().then(items => {
      updateCart(items)
      const sum = items.reduce((s, i) => s + i.price * i.quantity, 0)
      setTotal(sum)
    })
  }, [])

  const handleFinish = async () => {
    const info = JSON.parse(sessionStorage.getItem('checkoutInfo') || '{}')
    setIsProcessing(true)
    try {
      await apiCreateOrder(info.firstName, info.lastName, info.zipCode)
      clearCart()
      navigate('/checkout-complete')
    } catch (err) {
      alert(err.message)
      setIsProcessing(false)
    }
  }

  const itemCount = cartItems.reduce((sum, item) => sum + (item.quantity || 1), 0)

  return (
    <>
      <Header />
      <div className="checkout-overview-wrapper">
        <div className="checkout-overview-container">
          <div className="checkout-overview-header">
            <h1>Order Review</h1>
            <p>Please review your order before confirming</p>
          </div>

          <div className="checkout-overview-content">
            <div className="order-items-section">
              <h2 className="section-title">📦 Order Items</h2>
              <div className="order-items-list">
                {cartItems.map(item => (
                  <div key={item.id} className="order-item" data-testid={`item-name-${item.product_id}`}>
                    <div className="item-info">
                      <span className="item-name">{item.name}</span>
                      <span className="item-qty">Qty: {item.quantity || 1}</span>
                    </div>
                    <span className="item-price">${item.price.toFixed(2)}</span>
                  </div>
                ))}
              </div>
            </div>

            <div className="order-summary-section">
              <h2 className="section-title">💳 Order Summary</h2>
              <div className="summary-card">
                <div className="summary-row">
                  <span className="summary-label">Items:</span>
                  <span className="summary-value">{itemCount}</span>
                </div>
                <div className="summary-row">
                  <span className="summary-label">Subtotal:</span>
                  <span className="summary-value">${total.toFixed(2)}</span>
                </div>
                <div className="summary-row">
                  <span className="summary-label">Shipping:</span>
                  <span className="summary-value">Free</span>
                </div>
                <div className="summary-divider"></div>
                <div className="summary-row total-row">
                  <span className="summary-label">Total:</span>
                  <span className="total-price" data-testid="total-price">${total.toFixed(2)}</span>
                </div>
              </div>

              <button 
                className="finish-button" 
                data-testid="finish-button" 
                onClick={handleFinish}
                disabled={isProcessing}
              >
                {isProcessing ? '⏳ Processing...' : '✓ Complete Order'}
              </button>

              <button 
                className="back-button" 
                onClick={() => navigate('/cart')}
              >
                ← Back to Cart
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}
