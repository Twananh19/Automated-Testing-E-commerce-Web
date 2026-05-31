import { Link } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'

export default function Header() {
  const { cartCount } = useCart()
  const { logout } = useAuth()

  return (
    <header className="header">
      <div className="header-container">
        <div className="header-left">
          <Link to="/inventory" className="logo">
            <span className="logo-icon">🛍️</span>
            <span className="logo-text">Mini Shop</span>
          </Link>
        </div>

        <div className="header-center">
          <nav className="header-nav">
            <Link to="/inventory" className="nav-link">
              <span>📦</span>
              <span>Products</span>
            </Link>
          </nav>
        </div>

        <div className="header-right">
          <Link to="/cart" data-testid="cart-link" className="cart-button">
            <span className="cart-icon">🛒</span>
            {cartCount > 0 && (
              <span data-testid="cart-badge" className="cart-badge">{cartCount}</span>
            )}
          </Link>
          <button 
            onClick={logout} 
            data-testid="logout-button" 
            className="logout-button"
          >
            <span>🚪</span>
            <span>Logout</span>
          </button>
        </div>
      </div>
    </header>
  )
}
