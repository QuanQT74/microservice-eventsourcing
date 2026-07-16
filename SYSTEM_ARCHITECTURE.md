# Event Sourcing - Library Management System

**Status:** 🚧 In Development  
**Tech Stack:** Spring Boot 3.3.4 | Java 21 | Axon Framework | Kafka | PostgreSQL (planned) | React + TypeScript  
**Date:** 2026-07-11

---

## 📋 Quick Summary

Microservices hệ thống **mượn/trả sách thư viện** sử dụng **Event Sourcing** + **CQRS** pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                        │
│            Spring Cloud Gateway + Redis Cache                │
└──────────────┬──────────────────────────────────────────────┘
               │
    ┌──────────┼──────────┬──────────┬─────────────┐
    │          │          │          │             │
┌───▼──┐  ┌───▼──┐  ┌────▼──┐  ┌──▼───┐  ┌──────▼──┐
│Book  │  │Borrow│  │ Notif │  │Email │  │Discover │
│Svc   │  │Svc   │  │ Svc   │  │Svc   │  │Server   │
│8081  │  │8082  │  │ 8083  │  │ 8084 │  │ 8761    │
└──────┘  └──────┘  └───────┘  └──────┘  └─────────┘
    │          │          │          │
    └──────────┼──────────┼──────────┘
               │
        ┌──────▼──────────┐
        │    Kafka        │
        │  (9092, 9093)   │
        └─────────────────┘
```

---

## ✅ What's DONE

### Backend
- ✅ **Borrowing Saga** — orchestrate mượn sách, handle rollback
- ✅ **Axon Framework setup** — event sourcing + CQRS
- ✅ **Kafka integration** — event streaming
- ✅ **Email templates** (FreeMarker) — borrowing confirmation, overdue notice
- ✅ **API Key authentication** — secure endpoints
- ✅ **Book & Borrowing Commands** — create, delete, update
- ✅ **Event handlers** — persist events to DB
- ✅ **Eureka service discovery** — service registry

### Frontend
- ✅ **Browse Books** — danh sách sách, filter, borrow button
- ✅ **My Borrowings** — active + history
- ✅ **User Profile** — info + notifications
- ✅ **UI components** — buttons, cards, badges (shadcn)
- ✅ **Routing** — React Router setup
- ✅ **Mock data** — placeholder for API

### Infrastructure
- ✅ Docker support (partial)
- ✅ H2 in-memory DB (dev only)

---

## ❌ What's MISSING (Priority Order)

### 🔴 **CRITICAL - Backend**

**1. Query Model (Axon Projections)**
```
Status: NOT STARTED
Tasks:
  - BookProjection: track available count per book
  - BorrowingProjection: user's active borrowings
  - UserProjection: user profile data
  - NotificationProjection: overdue alerts
```

**2. REST Controllers - Query Side**
```
Status: PARTIAL (only commands exist)
Endpoints needed:
  GET /api/books                    → list all books (with availability)
  GET /api/books/{id}               → book details
  GET /api/borrowings/me            → my current borrowings
  GET /api/borrowings/me/history    → borrowing history
  GET /api/users/{id}               → user profile
```

**3. API Gateway Routes**
```
Status: NOT CONFIGURED
Need to map service endpoints in API Gateway config
```

**4. Persistent Database**
```
Current: H2 in-memory (loses data on restart)
Need: PostgreSQL with proper schema
  - book table
  - borrowing table
  - user table
  - event_store (Axon)
```

**5. Kafka Event Consumers**
```
Status: Events published, but no consumers
Tasks:
  - NotificationService consumes BorrowingCreatedEvent → send email
  - Track overdue borrowings → send reminder email
  - Update projections from events
```

### 🟠 **HIGH - Frontend**

**1. API Integration**
```
Status: Mock data only
Tasks:
  - Replace mock fetchBooks() with HTTP calls
  - Replace mock fetchMyBorrowings() with API
  - Borrow form → POST /api/borrowings
  - Return book → PUT /api/borrowings/{id}/return
  - Error handling + toast notifications
```

**2. Authentication**
```
Status: NOT IMPLEMENTED
Tasks:
  - Login page (username/password or API key)
  - Store JWT token / API key in localStorage
  - Auth middleware for protected routes
  - Logout functionality
```

**3. Form Validation & UX**
```
Status: Basic components only
Tasks:
  - Input validation (email, dates, etc.)
  - Better error messages from API
  - Loading spinners during async operations
  - Success/error toasts
  - Confirm dialogs for dangerous actions
```

### 🟡 **MEDIUM - DevOps**

**1. Docker Compose**
```
Services: PostgreSQL, Kafka, Axon Server, all microservices
Status: Needs creation
```

**2. Environment Config**
```
.env files for dev/prod
Database credentials
Kafka bootstrap servers
Email service config
```

**3. Flyway/Liquibase**
```
Database migrations for schema
```

---

## 🏗️ Architecture Details

### Saga Pattern (Borrowing Flow)

```
1. User requests borrow book
   ↓
2. BorrowingSaga starts
   ├─ Reserve book (BookService)
   ├─ Check availability
   ├─ Create borrowing record
   └─ On failure: rollback reservation
   ↓
3. BorrowingCreatedEvent emitted
   ↓
4. NotificationService listens
   └─ Send confirmation email
```

### Event Sourcing (What we store)

```
book-events:
  - BookCreatedEvent
  - BookUpdatedEvent
  - BookDeleteEvent

borrowing-events:
  - BorrowingCreatedEvent
  - BorrowingReturnedEvent
  - BorrowingDeleteEvent
```

### API Key Auth

Current: Custom filter validates `X-API-Key` header  
Flow: API Gateway → validates key → route to service

---

## 🚀 Next Steps (Recommended Order)

### Phase 1: Backend Query Model (1-2 days)
1. Create Axon Projections (Book, Borrowing, User)
2. Create Query handlers
3. Add Query Controller endpoints
4. Test with curl

### Phase 2: Database (1 day)
1. Switch from H2 to PostgreSQL
2. Create schema with Flyway
3. Setup event_store table
4. Test persistence

### Phase 3: Kafka Consumers (1 day)
1. NotificationService listens to Borrowing events
2. Send emails via SMTP
3. Update notification projections

### Phase 4: API Gateway (1 day)
1. Configure routes in Spring Cloud Gateway
2. Add Redis caching
3. Load balance services

### Phase 5: Frontend Integration (2-3 days)
1. Replace mock calls with HTTP requests
2. Add login/auth
3. Better error handling
4. Form validation

### Phase 6: Docker & CI/CD (1-2 days)
1. Docker Compose setup
2. GitHub Actions pipeline
3. Local dev environment

---

## 📁 Current File Structure

```
eventSourcing/
├── discoverserver/          → Eureka Service Discovery (8761)
├── bookservice/             → Book CRUD (8081)
├── borrowingservice/        → Borrowing + Saga (8082) ⭐ Main logic
├── notificationservice/     → Email notifications (8083)
├── employeeservice/         → (Future) Employee/librarian management
├── apigateway/             → API Gateway (8080)
├── commonservice/          → Shared DTOs, configs
└── frontend/               → React UI (5173)
```

---

## 🔧 Running Locally (Current)

```bash
# Terminal 1: Start Eureka
cd discoverserver
mvn spring-boot:run

# Terminal 2: Start Book Service
cd bookservice
mvn spring-boot:run

# Terminal 3: Start Borrowing Service (with Saga)
cd borrowingservice
mvn spring-boot:run

# Terminal 4: Start API Gateway
cd apigateway
mvn spring-boot:run

# Terminal 5: Start Frontend
cd frontend
npm run dev
```

Access: `http://localhost:5173`

⚠️ **NOTE:** Kafka/Axon Server need to be running too (missing Docker setup)

---

## 🧪 Testing Borrowing Flow

```bash
# 1. Get available books
curl http://localhost:8080/api/books

# 2. Borrow a book
curl -X POST http://localhost:8080/api/borrowings \
  -H "Content-Type: application/json" \
  -H "X-API-Key: your-key" \
  -d '{"userId":"user-1", "bookId":"book-123"}'

# 3. Check saga execution
curl http://localhost:8080/api/saga/status/{sagaId}

# 4. See Kafka events (if you had Kafka UI)
# Topic: borrowing-events
```

---

## 📝 Database Schema (Planned)

```sql
-- Books
CREATE TABLE books (
  id UUID PRIMARY KEY,
  title VARCHAR(255),
  author VARCHAR(255),
  isbn VARCHAR(20),
  total_count INT,
  created_at TIMESTAMP
);

-- Borrowings
CREATE TABLE borrowings (
  id UUID PRIMARY KEY,
  user_id UUID,
  book_id UUID,
  borrowed_date TIMESTAMP,
  due_date TIMESTAMP,
  returned_date TIMESTAMP,
  status VARCHAR(20), -- ACTIVE, RETURNED, OVERDUE
  created_at TIMESTAMP,
  FOREIGN KEY (book_id) REFERENCES books(id)
);

-- Users
CREATE TABLE users (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(255),
  created_at TIMESTAMP
);

-- Axon Event Store
CREATE TABLE event_store (
  aggregate_id VARCHAR(255),
  event_type VARCHAR(255),
  payload JSON,
  timestamp TIMESTAMP,
  version INT
);
```

---

## 🎯 Success Criteria

- [ ] All Query endpoints working
- [ ] PostgreSQL persistent storage
- [ ] Kafka events flowing to NotificationService
- [ ] Frontend can borrow/return books
- [ ] Email notifications sent on borrow
- [ ] Saga rollback works on failure
- [ ] API Gateway routing all requests
- [ ] Docker Compose runs entire system
- [ ] 100% saga execution logged in event store

---

## 🔗 Related Docs

- Axon Framework: https://docs.axoniq.io/
- Spring Cloud: https://spring.io/cloud
- Event Sourcing Pattern: https://martinfowler.com/eaaDev/EventSourcing.html
- CQRS Pattern: https://martinfowler.com/bliki/CQRS.html

---

**Last Updated:** 2026-07-11  
**Next Review:** After Phase 1 (Query Model)