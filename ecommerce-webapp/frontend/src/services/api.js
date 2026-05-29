const API_BASE = '/api'

function getHeaders() {
  const token = localStorage.getItem('token')
  return {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` })
  }
}

export async function apiLogin(username, password) {
  const res = await fetch(`${API_BASE}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.error)
  return data
}

export async function apiGetProducts() {
  const res = await fetch(`${API_BASE}/products`, { headers: getHeaders() })
  return res.json()
}

export async function apiGetCart() {
  const res = await fetch(`${API_BASE}/cart`, { headers: getHeaders() })
  return res.json()
}

export async function apiAddToCart(productId) {
  const res = await fetch(`${API_BASE}/cart`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify({ productId })
  })
  return res.json()
}

export async function apiRemoveFromCart(cartItemId) {
  const res = await fetch(`${API_BASE}/cart/${cartItemId}`, {
    method: 'DELETE',
    headers: getHeaders()
  })
  return res.json()
}

export async function apiCreateOrder(firstName, lastName, zipCode) {
  const res = await fetch(`${API_BASE}/orders`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify({ firstName, lastName, zipCode })
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.error)
  return data
}
