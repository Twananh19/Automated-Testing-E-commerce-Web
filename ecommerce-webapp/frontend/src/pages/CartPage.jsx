import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'
import { apiGetCart, apiRemoveFromCart } from '../services/api'
import { useCart } from '../context/CartContext'

export default function CartPage() {
  const { cartItems, updateCart } = useCart()
  const navigate = useNavigate()

  useEffect(() => {
    apiGetCart().then(updateCart)
  }, [])

  const handleRemove = async (cartItemId) => {
    await apiRemoveFromCart(cartItemId)
    const items = await apiGetCart()
    updateCart(items)
  }

  return (
    <>
      <Header />
      <div className="cart-page">
        <h2 data-testid="page-title">Your Cart</h2>
        {cartItems.length === 0 ? (
          <p data-testid="empty-cart">Cart is empty</p>
        ) : (
          cartItems.map(item => (
            <div key={item.id} className="cart-item" data-testid={`cart-item-${item.product_id}`}>
              <span data-testid={`item-name-${item.product_id}`}>{item.name}</span>
              <span>${item.price.toFixed(2)}</span>
              <button
                data-testid={`remove-${item.product_id}`}
                onClick={() => handleRemove(item.id)}
              >
                Remove
              </button>
            </div>
          ))
        )}
        <button
          data-testid="checkout-button"
          onClick={() => navigate('/checkout-info')}
          disabled={cartItems.length === 0}
        >
          Checkout
        </button>
      </div>
    </>
  )
}
