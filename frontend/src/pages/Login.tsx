import { useState } from 'react'
import axios from 'axios'
import { Link, useNavigate } from 'react-router-dom'

const API = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [seedMsg, setSeedMsg] = useState('')
  const [seedError, setSeedError] = useState(false)
  const [loading, setLoading] = useState(false)
  const [seedLoading, setSeedLoading] = useState(false)
  const navigate = useNavigate()

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSeedMsg('')
    setLoading(true)
    try {
      const res = await axios.post(`${API}/api/auth/login`, { username, password })
      const token = res.data.token
      localStorage.setItem('authToken', token)
      navigate('/dashboard')
    } catch {
      setError('Invalid username or password')
    } finally {
      setLoading(false)
    }
  }

  const runSeed = async () => {
    setError('')
    setSeedMsg('')
    setSeedError(false)
    setSeedLoading(true)
    try {
      const res = await axios.post(`${API}/api/seed`)
      setSeedMsg(res.data?.message || res.data || 'Database seeded! Use user1 / password1')
    } catch (e: any) {
      setSeedError(true)
      const msg = e?.response?.data?.message || e?.response?.data || e?.message || 'Request failed'
      const hint = (msg.includes('Network') || msg.includes('ECONNREFUSED') || msg.includes('Failed to fetch'))
        ? ' If local: run "mvn spring-boot:run" in the backend folder. If on the live site, the API may be starting—try again shortly.'
        : ''
      setSeedMsg(msg + hint)
    } finally {
      setSeedLoading(false)
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '70vh', padding: 16, position: 'relative', zIndex: 2 }}>
      <form
        onSubmit={submit}
        style={{
          background: '#020617',
          padding: 'clamp(16px, 4vw, 24px)',
          borderRadius: 12,
          width: '100%',
          maxWidth: 360,
          boxShadow: '0 20px 40px rgba(0,0,0,0.5)',
          border: '1px solid #1f2937'
        }}
      >
        <h3 style={{ marginTop: 0, marginBottom: 16 }}>Sign in</h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 14 }}>Username</label>
            <input
              value={username}
              onChange={e => setUsername(e.target.value)}
              style={{
                width: '100%',
                padding: '8px 10px',
                borderRadius: 6,
                border: '1px solid #374151',
                background: '#020617',
                color: '#e5e7eb'
              }}
            />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 14 }}>Password</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              style={{
                width: '100%',
                padding: '8px 10px',
                borderRadius: 6,
                border: '1px solid #374151',
                background: '#020617',
                color: '#e5e7eb'
              }}
            />
          </div>
          {error && <div style={{ color: '#fca5a5', fontSize: 13 }}>{error}</div>}
          {seedMsg && (
            <div style={{ color: seedError ? '#fca5a5' : '#22c55e', fontSize: 13 }}>{seedMsg}</div>
          )}
          <button
            type="submit"
            disabled={loading}
            style={{
              marginTop: 8,
              padding: '8px 10px',
              borderRadius: 8,
              border: 'none',
              background: loading ? '#6b7280' : '#4f46e5',
              color: 'white',
              fontWeight: 500,
              cursor: loading ? 'wait' : 'pointer'
            }}
          >
            {loading ? 'Logging in…' : 'Login'}
          </button>
          <button
            type="button"
            disabled={seedLoading}
            onClick={(e) => { e.preventDefault(); runSeed() }}
            style={{
              padding: '8px 10px',
              borderRadius: 8,
              border: '1px solid #374151',
              background: seedLoading ? '#1f2937' : 'transparent',
              color: '#9ca3af',
              cursor: seedLoading ? 'wait' : 'pointer',
              fontSize: 13
            }}
          >
            {seedLoading ? 'Seeding…' : 'Seed database (create demo users)'}
          </button>
        </div>
        <p style={{ marginTop: 16, fontSize: 13, color: '#9ca3af' }}>
          Don&apos;t have an account? <Link to="/register" style={{ color: '#60a5fa', cursor: 'pointer' }}>Register</Link>
        </p>
      </form>
    </div>
  )
}