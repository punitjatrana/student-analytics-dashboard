import { useState } from 'react'
import axios from 'axios'
import { Link, useNavigate } from 'react-router-dom'

const API = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const inputStyle = {
  width: '100%',
  padding: '8px 10px',
  borderRadius: 6,
  border: '1px solid #374151',
  background: '#020617',
  color: '#e5e7eb',
}

export default function Register() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [age, setAge] = useState('')
  const [gender, setGender] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const navigate = useNavigate()

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    try {
      await axios.post(`${API}/api/auth/register`, {
        username,
        password,
        fullName,
        age: age ? parseInt(age, 10) : null,
        gender: gender || null,
      })
      setSuccess(true)
      setTimeout(() => navigate('/login'), 1500)
    } catch (e: any) {
      const data = e?.response?.data
      let msg = 'Registration failed.'
      if (data) {
        if (typeof data === 'string') msg = data
        else if (data.message) msg = data.message
        else if (Array.isArray(data)) msg = data.map((x: any) => x?.defaultMessage || x?.message).filter(Boolean).join(', ') || msg
      } else if (e?.message?.includes('Network') || e?.message?.includes('ECONNREFUSED')) {
        msg = 'Cannot reach backend. Make sure the backend is running on port 8080.'
      }
      setError(msg)
    }
  }

  if (success) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '70vh' }}>
        <div style={{ color: '#22c55e', fontSize: 16 }}>Account created! Redirecting to login...</div>
      </div>
    )
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '70vh', padding: 16 }}>
      <form
        onSubmit={submit}
        style={{
          background: '#020617',
          padding: 'clamp(16px, 4vw, 24px)',
          borderRadius: 12,
          width: '100%',
          maxWidth: 360,
          boxShadow: '0 20px 40px rgba(0,0,0,0.5)',
          border: '1px solid #1f2937',
        }}
      >
        <h3 style={{ marginTop: 0, marginBottom: 16 }}>Create account</h3>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 14 }}>Username</label>
            <input value={username} onChange={e => setUsername(e.target.value)} style={inputStyle} required />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 14 }}>Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} style={inputStyle} required />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 14 }}>Full Name</label>
            <input value={fullName} onChange={e => setFullName(e.target.value)} style={inputStyle} required />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 14 }}>Age (optional)</label>
            <input type="number" min="1" max="120" value={age} onChange={e => setAge(e.target.value)} style={inputStyle} />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 14 }}>Gender (optional)</label>
            <select value={gender} onChange={e => setGender(e.target.value)} style={inputStyle}>
              <option value="">Select</option>
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="Other">Other</option>
            </select>
          </div>
          {error && <div style={{ color: '#fca5a5', fontSize: 13 }}>{error}</div>}
          <button
            type="submit"
            style={{
              marginTop: 8,
              padding: '8px 10px',
              borderRadius: 8,
              border: 'none',
              background: '#4f46e5',
              color: 'white',
              fontWeight: 500,
              cursor: 'pointer',
            }}
          >
            Register
          </button>
        </div>
        <p style={{ marginTop: 16, fontSize: 13, color: '#9ca3af' }}>
          Already have an account? <Link to="/login" style={{ color: '#60a5fa' }}>Sign in</Link>
        </p>
      </form>
    </div>
  )
}