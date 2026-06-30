# 🚀 FleetFlow — Delivery & Logistics Tracking System

**FleetFlow** is a full-stack enterprise logistics management platform for tracking shipments, managing drivers, vehicles, warehouses, routes, and customers. Built as a capstone project for SE4801 Enterprise Application Development.

---

## 🏗️ Tech Stack

| Layer        | Technology                                         |
|-------------|---------------------------------------------------|
| **Backend**  | Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA |
| **Database** | PostgreSQL 15 (with Flyway migrations)             |
| **Auth**     | JWT (JSON Web Token) with BCrypt password hashing  |
| **API Docs** | Swagger / OpenAPI 3 (SpringDoc)                    |
| **Testing**  | JUnit 5, Mockito, JaCoCo (code coverage)           |
| **Frontend** | Angular 19, TypeScript, TailwindCSS                |
| **DevOps**   | Docker, Docker Compose, Railway (backend), Vercel (frontend) |

---

## 📦 Project Structure

```
fleetflow/
├── src/main/java/com/fleetflow/
│   ├── config/          # Security configuration
│   ├── controller/      # REST API controllers (8 controllers)
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entity classes (8 entities)
│   ├── exception/       # Custom exceptions + global handler
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JWT filter, util, user details service
│   └── service/         # Business logic services
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/    # Flyway SQL migrations
├── src/test/            # Unit tests (17 test classes)
├── Dockerfile           # Multi-stage Docker build
├── docker-compose.yml   # Full-stack Docker setup
└── pom.xml              # Maven dependencies
```

---

## 🚀 Quick Start

### Option 1: Docker (Recommended for grading)

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/se4801-group-fleetflow.git
cd se4801-group-fleetflow

# Start everything (database + app)
docker compose up --build

# App will be available at: http://localhost:8080
# Swagger UI at: http://localhost:8080/swagger-ui.html
```

### Option 2: Local Development

```bash
# 1. Start only the database
docker compose up -d db

# 2. Run the Spring Boot app
./mvnw spring-boot:run

# App at: http://localhost:8080
```

### Run Tests

```bash
# Run all tests with coverage report
./mvnw test

# View coverage report
open target/site/jacoco/index.html
```

---

## 🔐 Default Login Credentials

| Email                | Password   | Role  |
|---------------------|------------|-------|
| `admin@fleetflow.com` | `admin123` | ADMIN |

---

## 📡 API Endpoints

| Resource     | Method | Endpoint                         | Auth Required |
|-------------|--------|----------------------------------|:------------:|
| **Auth**     | POST   | `/api/auth/login`                | ❌            |
|              | POST   | `/api/auth/register`             | ❌            |
| **Shipments**| GET    | `/api/shipments`                 | ✅            |
|              | GET    | `/api/shipments/{id}`            | ✅            |
|              | GET    | `/api/shipments/tracking/{code}` | ✅            |
|              | POST   | `/api/shipments`                 | ✅            |
|              | PUT    | `/api/shipments/{id}/status`     | ✅            |
| **Drivers**  | GET    | `/api/drivers`                   | ✅            |
|              | GET    | `/api/drivers/available`         | ✅            |
|              | POST   | `/api/drivers`                   | ✅            |
| **Vehicles** | GET    | `/api/vehicles`                  | ✅            |
|              | GET    | `/api/vehicles/active`           | ✅            |
|              | POST   | `/api/vehicles`                  | ✅            |
| **Warehouses** | GET  | `/api/warehouses`                | ✅            |
|              | POST   | `/api/warehouses`                | ✅            |
| **Routes**   | GET    | `/api/routes`                    | ✅            |
|              | POST   | `/api/routes`                    | ✅            |
| **Customers**| GET    | `/api/customers`                 | ✅            |
|              | POST   | `/api/customers`                 | ✅            |
| **Audit Log**| GET    | `/api/audit-logs`                | ✅            |
| **Swagger**  | GET    | `/swagger-ui.html`               | ❌            |

> Full interactive API documentation available at `/swagger-ui.html`

---

## 🌐 Live Deployment

| Service   | URL |
|-----------|-----|
| **Backend API**  | [https://se4801-group-fleetflow-production.up.railway.app](https://se4801-group-fleetflow-production.up.railway.app) |
| **Frontend App** | *(Deployed on Vercel — add your URL here)* |
| **Swagger UI**   | [https://se4801-group-fleetflow-production.up.railway.app/swagger-ui.html](https://se4801-group-fleetflow-production.up.railway.app/swagger-ui.html) |

---

## 🗃️ Database Schema

8 tables with full referential integrity:

- **users** — Authentication accounts (ADMIN, DISPATCHER, DRIVER)
- **drivers** — Delivery operators linked to user accounts
- **warehouses** — Distribution hubs / fulfillment centers
- **vehicles** — Fleet trucks/vans with capacity tracking
- **routes** — Predefined delivery paths between cities
- **customers** — Order recipients
- **shipments** — Core delivery records linking all entities
- **audit_log** — Full history trail of shipment status changes

---

## 👥 Team Members

| Name | Role | Contribution |
|------|------|-------------|
| *(Add your name)* | Group Leader | *(Add contribution)* |
| *(Add member)* | Developer | *(Add contribution)* |
| *(Add member)* | Developer | *(Add contribution)* |

---

## 📄 License

This project was built for academic purposes as part of SE4801 — Enterprise Application Development.
