# Vigility Technologies – Full Stack Challenge  
## Interactive Product Analytics Dashboard

Backend: **Spring Boot (Java 17+)**, Spring Data JPA, Spring Security (JWT), PostgreSQL, Lombok  
Frontend: **React + Vite + TypeScript**, Recharts, Axios, js-cookie

---

## 1. Running the project locally

### 1.1 Prerequisites
- **Java 17+**
- **Node.js** and **npm**
- **PostgreSQL** installed and running

### 1.2 Database setup
1. Create a database in PostgreSQL:
   - **Database name:** `Interactive_Product`
   - **Username:** `postgres`
   - **Password:** `1234`

2. Connection is configured in `backend/src/main/resources/application.yml`:
   - `spring.datasource.url=jdbc:postgresql://localhost:5432/Interactive_Product`
   - `spring.datasource.username=postgres`
   - `spring.datasource.password=1234`

3. **Schema:** Hibernate creates/updates tables automatically (`spring.jpa.hibernate.ddl-auto: update`). Flyway is disabled.

### 1.3 Backend (Spring Boot)
**Option A – STS / Eclipse**
- Import the `backend` folder as a Maven project.
- Run the main class: `com.example.studentms.StudentmsApplication`.

**Option B – Command line**
```bash
cd backend
mvn spring-boot:run
```
- API runs at **http://localhost:8080**.

### 1.4 Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev
```
- Open the URL shown (usually **http://localhost:5173**).

### 1.5 First-time login / demo data
- **Option 1 – Seed from UI:** On the **Login** page, click **“Seed database (create demo users)”**. Then log in with **user1** / **password1**.
- **Option 2 – Register:** Click **Register**, create an account, then sign in.
- **Option 3 – Auto-seed on startup:** If the database has no feature clicks, the backend seeds 10 users and sample clicks on first run. Use **user1** / **password1**, etc.

---

## 2. Application architecture

### 2.1 Backend
- **Framework:** Spring Boot 3.x
- **Persistence:** Spring Data JPA + PostgreSQL
- **Security:** Stateless JWT (no sessions)
- **Lombok:** Used for entities and DTOs (ensure Lombok is installed in STS/Eclipse if you use it).

#### Domain models
| Model         | Main fields |
|--------------|-------------|
| **User**     | `id`, `username`, `password` (BCrypt), `fullName`, `age`, `gender`, `enabled`, `roles` |
| **FeatureClick** | `id`, `user`, `featureName`, `clickedAt` |
| **Role**     | `id`, `name` (ADMIN, TEACHER, STUDENT) |

#### API endpoints
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST   | `/api/auth/register` | No  | Register; body: `username`, `password`, `fullName`, `age?`, `gender?` |
| POST   | `/api/auth/login`    | No  | Login; returns `{ "token": "<JWT>" }` |
| POST   | `/api/seed`         | No  | Create demo users and feature clicks (if none exist) |
| POST   | `/api/track`        | Yes | Record interaction; body: `{ "featureName": "..." }` |
| GET    | `/api/analytics`    | Yes | Chart data; params: `startDate`, `endDate`, `ageGroup?`, `gender?`, `featureName?` |

### 2.2 Frontend
- **Stack:** React 18, Vite, TypeScript, React Router v6, Recharts, Axios, js-cookie
- **Pages:** Login, Register, Dashboard (filters + bar chart + line chart)
- **Auth:** JWT stored in `localStorage`; sent as `Authorization: Bearer <token>`
- **Filters:** Last chosen date range, age group, and gender are stored in cookies and restored on refresh
- **Responsive:** Layout and header adapt to small screens

---

## 3. Seeding (dummy data)

### 3.1 From the UI
- Go to **http://localhost:5173/login**
- Click **“Seed database (create demo users)”**
- Log in with **user1** / **password1** (or user2/password2 … user10/password10)

### 3.2 From the API
- `POST http://localhost:8080/api/seed` (no auth)
- If user1 already exists, returns a message; otherwise creates 10 users and ~100+ feature clicks over the last 30 days.

### 3.3 On backend startup
- A `CommandLineRunner` in `DataSeeder` runs at startup.
- If the `feature_clicks` table is empty, it creates roles, 10 users, and sample feature clicks.

---

## 4. Scaling to 1 million write-events per minute

If this dashboard had to handle **1 million write-events per minute**, the backend would need to move away from direct synchronous writes to PostgreSQL:

- **Ingestion:** The `/track` endpoint would act as a thin producer, publishing events to a **message queue/stream** (e.g. Kafka, Kinesis, or RabbitMQ) instead of writing to the DB in the request path.
- **Consumption:** A set of **consumer services** would read from the stream and **batch-insert** into storage (with partitioning by time or user) to avoid single-node bottlenecks.
- **Analytics storage:** Use a **time-series or columnar store** (e.g. ClickHouse, TimescaleDB, BigQuery) and **pre-aggregated rollups** (e.g. counts per feature per minute/hour/day) so dashboard queries are fast reads instead of full scans.
- **Scaling:** Run multiple API instances behind a load balancer, multiple stream consumers, and sharded/partitioned storage so the system scales out with event volume.

This keeps write latency low for users while allowing high throughput and efficient analytics.

---

## 5. Project structure

```
Student_Systtem/
├── backend/                 # Spring Boot API
│   ├── src/main/java/com/example/studentms/
│   │   ├── config/          # Security, JWT, CORS, DataSeeder
│   │   ├── domain/          # User, Role, FeatureClick
│   │   ├── repository/      # JPA repositories
│   │   ├── service/         # JwtService
│   │   └── web/             # Controllers, DTOs
│   └── src/main/resources/
│       └── application.yml  # DB and JWT config
├── frontend/                # React + Vite
│   ├── src/
│   │   ├── pages/           # App, Login, Register, Dashboard
│   │   └── main.tsx
│   └── index.html
└── README.md
```

---

## 6. Configuration summary

| Item        | Location / value |
|------------|-------------------|
| DB URL     | `backend/src/main/resources/application.yml` |
| JWT secret | `jwt.secret` in `application.yml` (change in production) |
| JWT expiry | `jwt.expiration-ms` (default 24 hours) |
| API base   | Frontend uses `http://localhost:8080` or `VITE_API_BASE_URL` |
| CORS       | Allowed origin: `http://localhost:5173` |

---

You can change or extend this project later; this README describes the current setup.
