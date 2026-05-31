import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'
import { apiGetCart, apiRemoveFromCart } from '../services/api'
import { useCart } from '../context/CartContext'

export default function CartPage() {
  const { cartItems, updateCart } = useCart()
  const [isRemoving, setIsRemoving] = useState(null)
  const navigate = useNavigate()

  useEffect(() => {
    apiGetCart().then(updateCart)
  }, [])

  const handleRemove = async (cartItemId, productId) => {
    setIsRemoving(cartItemId)
    try {
      await apiRemoveFromCart(cartItemId)
      const items = await apiGetCart()
      updateCart(items)
    } finally {
      setIsRemoving(null)
    }
  }

  const totalPrice = cartItems.reduce((sum, item) => sum + (item.price * (item.quantity || 1)), 0)
  const itemCount = cartItems.reduce((sum, item) => sum + (item.quantity || 1), 0)

  return (
    <>
      <Header />
      <div className="cart-wrapper">
        <div className="cart-container">
          <div className="cart-header">
            <h1>🛒 Shopping Cart</h1>
            <p>{itemCount} {itemCount === 1 ? 'item' : 'items'} in cart</p>
          </div>

          {cartItems.length === 0 ? (
            <div className="empty-cart">
              <div className="empty-icon">🛍️</div>
              <h2 data-testid="empty-cart">Your cart is empty</h2>
              <p>Start shopping to add items to your cart!</p>
              <button 
                className="continue-shopping-btn"
                onClick={() => navigate('/inventory')}
              >
                ← Continue Shopping
              </button>
            </div>
          ) : (
            <div className="cart-content">
              <div className="cart-items-section">
                <h2 className="section-title">Items</h2>
                <div className="cart-items-list">
                  {cartItems.map(item => (
                    <div 
                      key={item.id} 
                      className="cart-item" 
                      data-testid={`cart-item-${item.product_id}`}
                    >
                      <div className="item-icon">📦</div>
                      
                      <div className="item-details">
                        <h3 data-testid={`item-name-${item.product_id}`}>{item.name}</h3>
                        <p className="item-qty">Quantity: {item.quantity || 1}</p>
                      </div>

                      <div className="item-price">
                        ${item.price.toFixed(2)}
                      </div>

                      <button
                        data-testid={`remove-${item.product_id}`}
                        className={`remove-btn ${isRemoving === item.id ? 'removing' : ''}`}
                        onClick={() => handleRemove(item.id, item.product_id)}
                        disabled={isRemoving === item.id}
                      >
                        {isRemoving === item.id ? '⏳' : '✕'}
                      </button>
                    </div>
                  ))}
                </div>
              </div>

              <div className="cart-summary-section">
                <div className="summary-card">
                  <h2 className="section-title">Order Summary</h2>
                  
                  <div className="summary-rows">
                    <div className="summary-row">
                      <span>Subtotal:</span>
                      <span>${totalPrice.toFixed(2)}</span>
                    </div>
                    <div className="summary-row">
                      <span>Shipping:</span>
                      <span className="free">Free</span>
                    </div>
                    <div className="summary-divider"></div>
                    <div className="summary-row total">
                      <span>Total:</span>
                      <span>${totalPrice.toFixed(2)}</span>
                    </div>
                  </div>

                  <button
                    data-testid="checkout-button"
                    className="checkout-btn"
                    onClick={() => navigate('/checkout-info')}
                  >
                    ✓ Proceed to Checkout
                  </button>

                  <button
                    className="continue-btn"
                    onClick={() => navigate('/inventory')}
                  >
                    ← Continue Shopping
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </>
  )
}
