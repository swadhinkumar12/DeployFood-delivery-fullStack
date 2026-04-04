// components/MenuItemCard.jsx
import { useState } from 'react'
import { cartAPI } from '../api/services'
import { useCart } from '../context/CartContext'

export default function MenuItemCard({ item }) {
  const [adding, setAdding]   = useState(false)
  const [added, setAdded]     = useState(false)
  const [error, setError]     = useState('')
  const { incrementCart }     = useCart()

  const handleAddToCart = async () => {
    setAdding(true)
    setError('')
    try {
      await cartAPI.addItem({ menuItemId: item.id, quantity: 1 })
      incrementCart(1)
      setAdded(true)
      // Reset the "Added!" state after 2 seconds
      setTimeout(() => setAdded(false), 2000)
    } catch (err) {
      setError('Failed to add to cart')
    } finally {
      setAdding(false)
    }
  }

  return (
    <div className="menu-card">
      {/* Category badge */}
      {item.category && (
        <span className="menu-category">{item.category}</span>
      )}

      <div className="menu-body">
        <div style={styles.info}>
          <h4 style={styles.name}>{item.name}</h4>
          {item.description && (
            <p style={styles.description}>{item.description}</p>
          )}
        </div>
        <div style={styles.priceRow}>
          <span style={styles.price}>₹{item.price?.toFixed(2)}</span>
          <button
            onClick={handleAddToCart}
            disabled={adding}
            style={{
              ...styles.addBtn,
              background: added ? '#2e7d32' : adding ? '#ccc' : '#d4552d',
            }}
          >
            {adding ? '...' : added ? '✓ Added!' : '+ Add'}
          </button>
        </div>
        {error && <p style={styles.error}>{error}</p>}
      </div>
    </div>
  )
}

const styles = {
  info: { marginBottom: '12px' },
  name: {
    fontSize: '1rem',
    fontWeight: 700,
    marginBottom: '4px',
  },
  description: {
    fontSize: '0.83rem',
    color: '#736556',
    lineHeight: 1.4,
  },
  priceRow: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  price: {
    fontSize: '1.1rem',
    fontWeight: 700,
    color: '#211912',
  },
  addBtn: {
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    padding: '8px 16px',
    fontSize: '0.88rem',
    fontWeight: 600,
    cursor: 'pointer',
    transition: 'background 0.2s',
    minWidth: '80px',
  },
  error: {
    color: '#c62828',
    fontSize: '0.8rem',
    marginTop: '6px',
  },
}
