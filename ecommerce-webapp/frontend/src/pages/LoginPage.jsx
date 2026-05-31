import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { apiLogin } from '../services/api'

export default function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [rememberMe, setRememberMe] = useState(false)
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleLogin = async (e) => {
    e.preventDefault()
    setError('')
    
    if (!username.trim() || !password.trim()) {
      setError('Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu')
      return
    }

    setIsLoading(true)
    try {
      const data = await apiLogin(username, password)
      
      if (rememberMe) {
        localStorage.setItem('rememberUsername', username)
      } else {
        localStorage.removeItem('rememberUsername')
      }
      
      login(data.token, data.username)
      navigate('/inventory')
    } catch (err) {
      setError(err.message || 'Đăng nhập không thành công. Vui lòng kiểm tra lại thông tin.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="login-wrapper">
      <div className="login-container">
        {/* Left side - Decorative */}
        <div className="login-side-left">
          <h2>Khám phá thế giới mua sắm tuyệt vời</h2>
          <p>Trải nghiệm mua sắm hiện đại với đầy đủ tính năng</p>
          <div className="login-illustration">
            <div className="floating-shape shape-1"></div>
            <div className="floating-shape shape-2"></div>
            <div className="floating-shape shape-3"></div>
            <div className="floating-shape shape-4"></div>
          </div>
        </div>

        {/* Right side - Form */}
        <div className="login-side-right">
          <div className="login-card">
            <div className="login-header">
              <h1>Đăng nhập</h1>
              <p>Vào hệ thống của bạn</p>
            </div>

            {/* Error message */}
            {error && (
              <div data-testid="error-message" className="error-alert">
                ⚠️ {error}
              </div>
            )}

            {/* Login Form */}
            <form onSubmit={handleLogin} className="login-form">
              <div className="input-group">
                <label htmlFor="username">Tên đăng nhập</label>
                <input
                  id="username"
                  data-testid="username"
                  type="text"
                  placeholder="Nhập tài khoản của bạn..."
                  value={username}
                  onChange={e => setUsername(e.target.value)}
                  disabled={isLoading}
                  required
                />
              </div>

              <div className="input-group">
                <label htmlFor="password">Mật khẩu</label>
                <input
                  id="password"
                  data-testid="password"
                  type="password"
                  placeholder="Nhập mật khẩu..."
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  disabled={isLoading}
                  required
                />
              </div>

              <div className="input-options">
                <label className="remember-me">
                  <input
                    type="checkbox"
                    checked={rememberMe}
                    onChange={e => setRememberMe(e.target.checked)}
                    disabled={isLoading}
                  />
                  <span>Ghi nhớ tôi</span>
                </label>
                <a href="#" className="forgot-password" onClick={(e) => {
                  e.preventDefault()
                  alert('Tính năng "Quên mật khẩu" đang được phát triển')
                }}>
                  Quên mật khẩu?
                </a>
              </div>

              <button 
                data-testid="login-button" 
                type="submit" 
                className="btn-login"
                disabled={isLoading}
              >
                {isLoading ? '⏳ Đang đăng nhập...' : '🔓 Đăng nhập'}
              </button>
            </form>
            
            <div className="login-footer">
              <p>
                Chưa có tài khoản? <a href="#signup">Đăng ký ngay</a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}