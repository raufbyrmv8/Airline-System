# AirlineReservationSystem

A microservices-based **Flight Reservation System** with full **Authentication & Authorization**, **Flight Management**, **Booking**, and **Notifications**. The system uses JWT for auth, email verification & reset flows, and role-based access (Admin, Operator, Customer).

## Table of Contents
- [Architecture](#architecture)
- [Core Features](#core-features)
- [Roles & Permissions](#roles--permissions)
- [Auth Flow](#auth-flow)
- [Services](#services)
- [Local Development](#local-development)
- [Configuration (.env)](#configuration-env)
- [Database & Migrations](#database--migrations)
- [Testing & Coverage](#testing--coverage)
- [Notifications & Events](#notifications--events)
- [API Sketch](#api-sketch)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## Architecture

Monorepo layout (Gradle multi-module):

AirlineReservationSystem/
├─ common/ # shared code (DTOs, exceptions, utils)
├─ flight-ms/ # flights CRUD, approvals, search
├─ user-ms/ # auth, users, roles, email verification/password reset
├─ notification-ms/ # email notifications (registration, approvals, bookings)
├─ files/ # assets/templates (e.g., email templates)
├─ docker-compose.yml # Kafka, Postgres, Mail dev stack
├─ build.gradle, settings.gradle, gradlew*
└─ ... (infra: kafka/, init/, etc.)

pgsql
Kodu kopyala

**Tech**: Java 17, Spring Boot, Spring Security, JWT, Spring Data JPA, Kafka, PostgreSQL, Flyway, Testcontainers/JUnit5/Mockito, Mail (SMTP).

---

## Core Features

### Authentication & Authorization
- Create account by email for all roles.
- Email verification required to activate the account.
- Secure password reset via email link.
- JWT issued on successful login; used for authenticated requests.
- At least one **Admin** is seeded by default.

### Flights Management
- Only **active Operators** can create/edit flights.
- New/edited flights are **Pending Approval**.
- Admin can **approve/reject** with comments, filter by status (Pending/Approved/Rejected).
- Admin can **cancel flights** or **change schedule**; affected customers are notified and refunds issued.

### Booking
- Customers can search by route, price, location, dates, and available seats.
- Multi-step booking: select flight → passengers info → payment → confirmation.
- Confirmation email sent on success.
- Refunds allowed up to 24h before departure.
- Customers get notifications about cancellations/refunds/schedule changes.

---

## Roles & Permissions

| Role     | Capabilities                                                                 |
|----------|-------------------------------------------------------------------------------|
| Admin    | Verify operators, approve/reject flights, cancel/change schedules, view logs. |
| Operator | Create/edit flights (Pending Approval), view their flight statuses.           |
| Customer | Search & book flights, request refunds, receive notifications.               |

---

## Auth Flow

1. **Register** → user receives **verification email**.
2. **Verify** account → login with credentials.
3. **Login** → receive **JWT** (Authorization: Bearer …).
4. **Password Reset** → request reset link by email → set new password.

Security is enforced via JWT filters and role-based method/endpoint protection.

---

## Services

### user-ms
- User registration, email verification, login (JWT), password reset.
- Role management: Admin / Operator / Customer.
- Seeds one **Admin** on startup.

### flight-ms
- Manage flights (create/edit by active operators).
- Admin approvals (Pending → Approved/Rejected + comments).
- Schedule change/cancel capabilities.

### notification-ms
- Sends emails for: registration, operator activation, flight approvals, bookings, refunds, cancellations.
- Consumes Kafka events from `user-ms` / `flight-ms`.

---

## Local Development

### Prerequisites
- Java 17
- Docker & Docker Compose
- Gradle (wrapper included)

### Start infrastructure (Postgres, Kafka, MailHog)
```bash
docker compose up -d
Build all modules
bash
Kodu kopyala
./gradlew clean build -x test
Run services
bash
Kodu kopyala
# In separate terminals:
./gradlew :user-ms:bootRun
./gradlew :flight-ms:bootRun
./gradlew :notification-ms:bootRun
Alternatively, run JARs:

bash
Kodu kopyala
java -jar user-ms/build/libs/user-ms-*.jar
java -jar flight-ms/build/libs/flight-ms-*.jar
java -jar notification-ms/build/libs/notification-ms-*.jar
Configuration (.env)
Create a .env in repo root or export env vars in your shell:

dotenv
Kodu kopyala
# Postgres
DB_HOST=localhost
DB_PORT=5432
DB_NAME=airline_db
DB_USER=airline
DB_PASS=airline

# Kafka
KAFKA_BOOTSTRAP=localhost:9092

# JWT
JWT_SECRET=change-me-in-prod
JWT_EXP_MIN=60

# Mail (MailHog)
MAIL_HOST=localhost
MAIL_PORT=1025
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_FROM=no-reply@airline.local

# URLs used in emails
FRONTEND_URL=http://localhost:3000
VERIFY_URL=${FRONTEND_URL}/verify?token=
RESET_URL=${FRONTEND_URL}/reset?token=
Each service reads its own application.yml and can override via env.

Database & Migrations
Flyway runs on startup for each service that owns a schema.

Default seed inserts one Admin user in user-ms (email & password from env, or generated and logged on startup).

Testing & Coverage
Unit tests with JUnit 5 + Mockito (service-layer heavily covered).

Integration tests with Testcontainers (Kafka/Postgres when needed).

Run tests:

bash
Kodu kopyala
./gradlew test
Generate coverage (JaCoCo):

bash
Kodu kopyala
./gradlew jacocoTestReport
# report: <module>/build/reports/jacoco/test/html/index.html
Notifications & Events
Kafka topics (example):

user.registered, user.operator-activated

flight.created.pending, flight.approved, flight.rejected

booking.completed, booking.refund-requested, flight.canceled, flight.schedule-changed

notification-ms consumes events and delivers emails.

API Sketch
Full OpenAPI/Swagger is available per service at /swagger-ui.html or /v3/api-docs.

user-ms (examples)

POST /api/v1/auth/register → register user

POST /api/v1/auth/login → JWT

POST /api/v1/auth/verify → email verification

POST /api/v1/auth/forgot → send reset link

POST /api/v1/auth/reset → set new password

POST /api/v1/admin/operators/{id}/activate → Admin activates operator

flight-ms

POST /api/v1/flights (Operator, active) → create (Pending)

PUT /api/v1/flights/{id} (Operator, active) → edit (Pending)

GET /api/v1/flights/pending (Admin) → list pending

POST /api/v1/flights/{id}/approve (Admin) → approve

POST /api/v1/flights/{id}/reject (Admin) → reject with comment

POST /api/v1/flights/{id}/cancel (Admin) → cancel (notify & refund)

PUT /api/v1/flights/{id}/schedule (Admin) → change schedule

booking (in flight-ms or dedicated booking-ms if split)

GET /api/v1/search → query by route/date/price/seats

POST /api/v1/bookings → step 1 (select flight & seats)

PUT /api/v1/bookings/{id}/passengers → step 2 (passenger info)

POST /api/v1/bookings/{id}/pay → step 3 (payment)

POST /api/v1/bookings/{id}/refund → request refund (>=24h)

Roadmap
Payment provider integration (sandbox mode).

Rate limiting & audit logs.

Multi-tenant support.

Advanced search (Elasticsearch).

SAGA/Orchestration for booking workflows.

Contributing
PRs are welcome. Please:

Create a feature branch.

Add/keep tests green (./gradlew test).

Follow module code style & commit conventions.