// components/Navbar.jsx
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { useEffect } from 'react'

export default function Navbar() {
  const { user, logout } = useAuth()
  const { cartCount, refreshCartCount } = useCart()
  const navigate = useNavigate()
  const location = useLocation()

  // Refresh cart count whenever location changes (e.g., after adding item)
  useEffect(() => {
    if (user) refreshCartCount()
  }, [location, user])

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const isActive = (path) => location.pathname === path ? 'nav-link active' : 'nav-link'

  return (
    <nav className="nav-shell">
      <div className="container nav-inner">
        {/* Logo */}
        <Link to="/" className="brand">
          <span className="brand-icon">🍔</span>
          <span className="brand-text">ByToSoul</span>
        </Link>

        {/* Nav Links */}
        {user && (
          <div className="nav-links">
            <Link to="/" className={isActive('/')}>Home</Link>
            <Link to="/cart" className={isActive('/cart')}>
              Cart
              {cartCount > 0 && (
                <span className="cart-badge">{cartCount}</span>
              )}
            </Link>
            <Link to="/orders" className={isActive('/orders')}>Orders</Link>
            {user.role === 'SELLER' && (
              <Link to="/seller" className={isActive('/seller')}>Seller</Link>
            )}
          </div>
        )}

        {/* User / Auth */}
        <div className="nav-actions">
          {user ? (
            <>
              <span className="user-chip">Hi, {user.name?.split(' ')[0]}</span>
              <button onClick={handleLogout} className="btn btn-ghost btn-sm">Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link">Login</Link>
              <Link to="/register" className="btn btn-primary btn-sm">Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}
