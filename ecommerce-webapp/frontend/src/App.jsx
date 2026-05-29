import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { CartProvider } from './context/CartContext'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import ProductsPage from './pages/ProductsPage'
import CartPage from './pages/CartPage'
import CheckoutInfoPage from './pages/CheckoutInfoPage'
import CheckoutOverviewPage from './pages/CheckoutOverviewPage'
import CheckoutCompletePage from './pages/CheckoutCompletePage'

export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<LoginPage />} />
            <Route path="/inventory" element={<ProtectedRoute><ProductsPage /></ProtectedRoute>} />
            <Route path="/cart" element={<ProtectedRoute><CartPage /></ProtectedRoute>} />
            <Route path="/checkout-info" element={<ProtectedRoute><CheckoutInfoPage /></ProtectedRoute>} />
            <Route path="/checkout-overview" element={<ProtectedRoute><CheckoutOverviewPage /></ProtectedRoute>} />
            <Route path="/checkout-complete" element={<ProtectedRoute><CheckoutCompletePage /></ProtectedRoute>} />
          </Routes>
        </BrowserRouter>
      </CartProvider>
    </AuthProvider>
  )
}
