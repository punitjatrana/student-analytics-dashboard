import { useEffect, useState } from 'react'
import axios from 'axios'
import Cookies from 'js-cookie'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  LineChart,
  Line
} from 'recharts'
import { useNavigate } from 'react-router-dom'

const API = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

type FeatureUsagePoint = { featureName: string; totalClicks: number }
type TimeSeriesPoint = { timestamp: string; totalClicks: number }

type AnalyticsResponse = {
  barChart: FeatureUsagePoint[]
  lineChart: TimeSeriesPoint[]
}

const tokenHeader = () => {
  const token = localStorage.getItem('authToken')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

const today = new Date()
const thirtyDaysAgo = new Date()
thirtyDaysAgo.setDate(today.getDate() - 30)

const toDateInput = (d: Date) => d.toISOString().split('T')[0]

export default function Dashboard() {
  const navigate = useNavigate()

  const [startDate, setStartDate] = useState(Cookies.get('filter_start') || toDateInput(thirtyDaysAgo))
  const [endDate, setEndDate] = useState(Cookies.get('filter_end') || toDateInput(today))
  const [ageGroup, setAgeGroup] = useState(Cookies.get('filter_age') || '')
  const [gender, setGender] = useState(Cookies.get('filter_gender') || '')
  const [selectedFeature, setSelectedFeature] = useState('')

  const [data, setData] = useState<AnalyticsResponse | null>(null)
  const [loading, setLoading] = useState(false)

  const saveFiltersToCookies = () => {
    Cookies.set('filter_start', startDate)
    Cookies.set('filter_end', endDate)
    Cookies.set('filter_age', ageGroup)
    Cookies.set('filter_gender', gender)
  }

  const track = async (featureName: string) => {
    try {
      await axios.post(
        `${API}/api/track`,
        { featureName },
        { headers: tokenHeader() }
      )
    } catch {
      // ignore tracking errors
    }
  }

  const loadAnalytics = async (featureOverride?: string) => {
    if (!localStorage.getItem('authToken')) {
      navigate('/login')
      return
    }
    setLoading(true)
    try {
      const params: any = {
        startDate,
        endDate
      }
      if (ageGroup) params.ageGroup = ageGroup
      if (gender) params.gender = gender
      if (featureOverride || selectedFeature) params.featureName = featureOverride || selectedFeature

      const res = await axios.get<AnalyticsResponse>(`${API}/api/analytics`, {
        params,
        headers: tokenHeader()
      })
      setData(res.data)
    } catch (e: any) {
      if (e?.response?.status === 401) {
        navigate('/login')
      }
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadAnalytics()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const applyFilters = async () => {
    saveFiltersToCookies()
    await track('filters_change')
    await loadAnalytics()
  }

  const handleBarClick = async (entry: FeatureUsagePoint) => {
    setSelectedFeature(entry.featureName)
    await track('bar_chart_click')
    await loadAnalytics(entry.featureName)
  }

  return (
    <div
      style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(min(100%, 320px), 1fr))',
        gap: 'clamp(16px, 3vw, 24px)',
        minHeight: 0,
      }}
    >
      <section style={{ background: '#020617', borderRadius: 12, padding: 'clamp(12px, 2vw, 16px)', border: '1px solid #1f2937', minWidth: 0 }}>
        <h3 style={{ marginTop: 0, marginBottom: 12, fontSize: 16 }}>Filters</h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: 12 }}>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 13 }}>Start Date</label>
            <input
              type="date"
              value={startDate}
              onChange={e => setStartDate(e.target.value)}
              onBlur={() => track('date_filter')}
              style={{ width: '100%', padding: '6px 8px', borderRadius: 6, border: '1px solid #374151', background: '#020617', color: '#e5e7eb' }}
            />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 13 }}>End Date</label>
            <input
              type="date"
              value={endDate}
              onChange={e => setEndDate(e.target.value)}
              onBlur={() => track('date_filter')}
              style={{ width: '100%', padding: '6px 8px', borderRadius: 6, border: '1px solid #374151', background: '#020617', color: '#e5e7eb' }}
            />
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 13 }}>Age Group</label>
            <select
              value={ageGroup}
              onChange={e => {
                setAgeGroup(e.target.value)
                track('age_filter')
              }}
              style={{ width: '100%', padding: '6px 8px', borderRadius: 6, border: '1px solid #374151', background: '#020617', color: '#e5e7eb' }}
            >
              <option value="">All</option>
              <option value="<18">{'<18'}</option>
              <option value="18-40">18-40</option>
              <option value=">40">{'>40'}</option>
            </select>
          </div>
          <div>
            <label style={{ display: 'block', marginBottom: 4, fontSize: 13 }}>Gender</label>
            <select
              value={gender}
              onChange={e => {
                setGender(e.target.value)
                track('gender_filter')
              }}
              style={{ width: '100%', padding: '6px 8px', borderRadius: 6, border: '1px solid #374151', background: '#020617', color: '#e5e7eb' }}
            >
              <option value="">All</option>
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="Other">Other</option>
            </select>
          </div>
        </div>
        <button
          onClick={applyFilters}
          style={{
            marginTop: 16,
            padding: '8px 12px',
            borderRadius: 8,
            border: 'none',
            background: '#22c55e',
            color: '#022c22',
            fontWeight: 600,
            cursor: 'pointer'
          }}
        >
          Apply Filters
        </button>
      </section>

      <section style={{ display: 'flex', flexDirection: 'column', gap: 16, minWidth: 0 }}>
        <div style={{ background: '#020617', borderRadius: 12, padding: 'clamp(12px, 2vw, 16px)', border: '1px solid #1f2937', minHeight: 220 }}>
          <h3 style={{ marginTop: 0, marginBottom: 8, fontSize: 16 }}>Feature Usage</h3>
          <p style={{ marginTop: 0, marginBottom: 8, fontSize: 12, color: '#9ca3af' }}>
            Click a bar to focus the time trend below.
          </p>
          {loading && <div style={{ fontSize: 13 }}>Loading...</div>}
          {!loading && data && data.barChart.length === 0 && <div style={{ fontSize: 13 }}>No data for current filters.</div>}
          {!loading && data && data.barChart.length > 0 && (
            <div style={{ width: '100%', height: 180 }}>
              <ResponsiveContainer>
                <BarChart data={data.barChart} margin={{ top: 8, right: 8, left: -20, bottom: 30 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" />
                  <XAxis
                    dataKey="featureName"
                    tick={{ fontSize: 10, fill: '#9ca3af' }}
                    interval={0}
                    minTickGap={5}
                  />
                  <YAxis tick={{ fontSize: 11, fill: '#9ca3af' }} />
                  <Tooltip
                    contentStyle={{ background: '#020617', border: '1px solid #1f2937', borderRadius: 8, fontSize: 12 }}
                  />
                  <Bar
                    dataKey="totalClicks"
                    fill="#4f46e5"
                    radius={[4, 4, 0, 0]}
                    onClick={({ activePayload }) => {
                      if (!activePayload || !activePayload[0]) return
                      const entry = activePayload[0].payload as FeatureUsagePoint
                      handleBarClick(entry)
                    }}
                  />
                </BarChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>

        <div style={{ background: '#020617', borderRadius: 12, padding: 'clamp(12px, 2vw, 16px)', border: '1px solid #1f2937', minHeight: 220 }}>
          <h3 style={{ marginTop: 0, marginBottom: 8, fontSize: 16 }}>
            Time Trend {selectedFeature ? `– ${selectedFeature}` : ''}
          </h3>
          {loading && <div style={{ fontSize: 13 }}>Loading...</div>}
          {!loading && data && data.lineChart.length === 0 && <div style={{ fontSize: 13 }}>No time-series data.</div>}
          {!loading && data && data.lineChart.length > 0 && (
            <div style={{ width: '100%', height: 180 }}>
              <ResponsiveContainer>
                <LineChart data={data.lineChart} margin={{ top: 8, right: 8, left: -20, bottom: 4 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#1f2937" />
                  <XAxis
                    dataKey="timestamp"
                    tick={{ fontSize: 11, fill: '#9ca3af' }}
                    tickFormatter={v => new Date(v).toLocaleDateString()}
                  />
                  <YAxis tick={{ fontSize: 11, fill: '#9ca3af' }} />
                  <Tooltip
                    contentStyle={{ background: '#020617', border: '1px solid #1f2937', borderRadius: 8, fontSize: 12 }}
                    labelFormatter={v => new Date(v).toLocaleString()}
                  />
                  <Line type="monotone" dataKey="totalClicks" stroke="#22c55e" strokeWidth={2} dot={false} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>
      </section>
    </div>
  )
}