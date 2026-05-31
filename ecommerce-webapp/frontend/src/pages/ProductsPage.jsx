import { useEffect, useState } from 'react'
import Header from '../components/Header'
import ProductCard from '../components/ProductCard'
import { apiGetProducts, apiGetCart } from '../services/api'
import { useCart } from '../context/CartContext'

export default function ProductsPage() {
  const [products, setProducts] = useState([])
  const { updateCart } = useCart()

  const loadCart = async () => {
    const items = await apiGetCart()
    updateCart(items)
  }

  useEffect(() => {
    apiGetProducts().then(setProducts)
    loadCart()
  }, [])

  return (
    <>
      <Header />
      <div className="products-wrapper">
        <div className="products-side-right">
          <div className="products-card">
            <div className="products-header">
              <h1>Sản phẩm</h1>
              <p>Danh sách các sản phẩm có sẵn</p>
            </div>

            <div className="product-grid">
              {products.map(p => (
                <ProductCard key={p.id} product={p} onCartUpdate={loadCart} />
              ))}
            </div>
          </div>
        </div>
      </div>
    </>
  )
}
