import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Header from '../components/Header'
import { apiGetCart, apiCreateOrder } from '../services/api'
import { useCart } from '../context/CartContext'

export default function CheckoutOverviewPage() {
  const { cartItems, updateCart, clearCart } = useCart()
  const [total, setTotal] = useState(0)
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
    try {
      await apiCreateOrder(info.firstName, info.lastName, info.zipCode)
      clearCart()
      navigate('/checkout-complete')
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <>
      <Header />
      <div className="checkout-overview-page">
        <h2>Checkout: Overview</h2>
        {cartItems.map(item => (
          <div key={item.id} className="overview-item">
            <span data-testid={`item-name-${item.product_id}`}>{item.name}</span>
            <span>${item.price.toFixed(2)}</span>
          </div>
        ))}
        <p data-testid="total-price" className="total">Total: ${total.toFixed(2)}</p>
        <button data-testid="finish-button" onClick={handleFinish}>Finish</button>
      </div>
    </>
  )
}
