import { apiAddToCart } from '../services/api'
import { useState } from 'react'

export default function ProductCard({ product, onCartUpdate }) {
  const [isLoading, setIsLoading] = useState(false)
  const [isAdded, setIsAdded] = useState(false)

  const handleAddToCart = async () => {
    setIsLoading(true)
    try {
      await apiAddToCart(product.id)
      setIsAdded(true)
      setTimeout(() => setIsAdded(false), 2000)
      if (onCartUpdate) onCartUpdate()
    } catch (err) {
      console.error(err)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="product-card" data-testid={`product-${product.id}`}>
      <div className="product-image-placeholder">
        <span>📦</span>
      </div>
      
      <div className="product-content">
        <h3 className="product-name" data-testid={`product-name-${product.id}`}>
          {product.name}
        </h3>
        
        <p className="product-description">{product.description}</p>
        
        <div className="product-footer">
          <span className="product-price" data-testid={`product-price-${product.id}`}>
            ${product.price.toFixed(2)}
          </span>
          
          <button
            className={`product-btn ${isAdded ? 'added' : ''} ${isLoading ? 'loading' : ''}`}
            data-testid={`add-to-cart-${product.id}`}
            onClick={handleAddToCart}
            disabled={isLoading}
          >
            {isLoading ? '⏳ Adding...' : isAdded ? '✓ Added' : '🛒 Add to cart'}
          </button>
        </div>
      </div>
    </div>
  )
}
