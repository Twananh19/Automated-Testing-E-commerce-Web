import { createContext, useContext, useState } from 'react'

const CartContext = createContext()

export function CartProvider({ children }) {
  const [cartItems, setCartItems] = useState([])
  const [cartCount, setCartCount] = useState(0)

  const updateCart = (items) => {
    setCartItems(items)
    setCartCount(items.reduce((sum, item) => sum + item.quantity, 0))
  }

  const clearCart = () => {
    setCartItems([])
    setCartCount(0)
  }

  return (
    <CartContext.Provider value={{ cartItems, cartCount, updateCart, clearCart }}>
      {children}
    </CartContext.Provider>
  )
}

export const useCart = () => useContext(CartContext)
