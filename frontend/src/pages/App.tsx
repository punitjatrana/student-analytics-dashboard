import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'

export default function App() {
  const location = useLocation()
  const navigate = useNavigate()
  const isLogin = location.pathname === '/login'

  const goToDashboard = () => {
    navigate('/dashboard', { replace: true })
  }

  const logout = () => {
    localStorage.removeItem('authToken')
    navigate('/login', { replace: true })
  }

  return (
    <div style={{ fontFamily: 'system-ui, sans-serif', minHeight: '100vh', background: '#0f172a', color: '#e5e7eb' }}>
      <header
        style={{
          padding: '12px 16px',
          borderBottom: '1px solid #1f2937',
          display: 'flex',
          flexWrap: 'wrap',
          justifyContent: 'space-between',
          alignItems: 'center',
          gap: 12,
          position: 'sticky',
          top: 0,
          zIndex: 9999,
          backgroundColor: '#0f172a',
          isolation: 'isolate',
        }}
      >
        <Link to="/dashboard" style={{ textDecoration: 'none', color: '#e5e7eb', cursor: 'pointer', minWidth: 0 }}>
          <h2 style={{ margin: 0, fontSize: 'clamp(14px, 4vw, 20px)', cursor: 'pointer' }}>Interactive Product Analytics Dashboard</h2>
        </Link>
        {!isLogin && (
          <nav style={{ display: 'flex', flexWrap: 'wrap', gap: 8, alignItems: 'center' }}>
            <button
              type="button"
              onClick={goToDashboard}
              style={{
                background: 'none',
                border: '1px solid #374151',
                borderRadius: 6,
                color: '#9ca3af',
                cursor: 'pointer',
                fontSize: 14,
                padding: '8px 16px',
                fontFamily: 'inherit',
                pointerEvents: 'auto',
              }}
            >
              Dashboard
            </button>
            <Link
              to="/register"
              style={{
                color: '#9ca3af',
                textDecoration: 'none',
                fontSize: 14,
                padding: '8px 16px',
                border: '1px solid #374151',
                borderRadius: 6,
              }}
            >
              Register
            </Link>
            <Link
              to="/login"
              style={{
                color: '#9ca3af',
                textDecoration: 'none',
                fontSize: 14,
                padding: '8px 16px',
                border: '1px solid #374151',
                borderRadius: 6,
              }}
            >
              Login
            </Link>
            <button
              type="button"
              onClick={logout}
              style={{
                background: 'none',
                border: '1px solid #374151',
                borderRadius: 6,
                color: '#9ca3af',
                cursor: 'pointer',
                fontSize: 14,
                padding: '8px 16px',
                fontFamily: 'inherit',
                pointerEvents: 'auto',
              }}
            >
              Logout
            </button>
          </nav>
        )}
      </header>
      <main style={{ padding: 'clamp(12px, 3vw, 24px)', position: 'relative', zIndex: 1 }}>
        <Outlet />
      </main>
    </div>
  )
}