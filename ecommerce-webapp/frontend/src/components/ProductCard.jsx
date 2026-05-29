import { apiAddToCart } from '../services/api'

export default function ProductCard({ product, onCartUpdate }) {
  const handleAddToCart = async () => {
    await apiAddToCart(product.id)
    if (onCartUpdate) onCartUpdate()
  }

  return (
    <div className="product-card" data-testid={`product-${product.id}`}>
      <h3 data-testid={`product-name-${product.id}`}>{product.name}</h3>
      <p>{product.description}</p>
      <span data-testid={`product-price-${product.id}`}>${product.price.toFixed(2)}</span>
      <button
        data-testid={`add-to-cart-${product.id}`}
        onClick={handleAddToCart}
      >
        Add to cart
      </button>
    </div>
  )
}
