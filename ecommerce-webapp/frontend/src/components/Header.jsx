import { Link } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'

export default function Header() {
  const { cartCount } = useCart()
  const { logout } = useAuth()

  return (
    <header className="header">
      <h1>Mini Shop</h1>
      <nav>
        <Link to="/inventory">Products</Link>
        <Link to="/cart" data-testid="cart-link" className="cart-link">
          🛒
          {cartCount > 0 && (
            <span data-testid="cart-badge" className="cart-badge">{cartCount}</span>
          )}
        </Link>
        <button onClick={logout} data-testid="logout-button">Logout</button>
      </nav>
    </header>
  )
}
