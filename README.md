# 📚 Library Management System – Microservice Event Sourcing

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/) [![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot) [![Axon Framework](https://img.shields.io/badge/Axon_Framework-FFA500?style=for-the-badge&logoColor=white)](https://axoniq.io/) [![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/) [![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/) [![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/) [![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/) [![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)](https://kubernetes.io/) [![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/) [![Keycloak](https://img.shields.io/badge/Keycloak-ADD8E6?style=for-the-badge&logo=keycloak&logoColor=black)](https://www.keycloak.org/) [![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)](https://www.postman.com/)

## 📋 Project Overview

A **library management system** built on a **microservice** architecture with **Event Sourcing**, **CQRS**, and **Saga orchestration**. The system manages books, employees, and borrowing/return workflows while guaranteeing data consistency across distributed services.

## 🏗️ System Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        Client[Client Applications]
    end

    subgraph "API Gateway Layer"
        Gateway[API Gateway\nPort: 8080\nRate Limiting & Auth]
    end

    subgraph "Service Discovery"
        Eureka[Eureka Server\nPort: 8761]
    end

    subgraph "Microservices"
        BookService[Book Service\nPort: 9001\nCQRS + Event Sourcing]
        BorrowingService[Borrowing Service\nPort: 9003\nSaga Orchestration]
        EmployeeService[Employee Service\nPort: 9002\nCQRS + Event Sourcing]
        UserService[User Service\nPort: 9005\nAuthentication]
        NotificationService[Notification Service\nPort: 9003\nEmail & Kafka Consumer]
        CommonService[Common Service\nShared Components]
    end

    subgraph "Event Store & Messaging"
        AxonServer[Axon Server\nPort: 8124\nEvent Store & Router]
        Kafka[Apache Kafka\nPort: 9092\nEvent Streaming]
        Zookeeper[Zookeeper\nPort: 2181]
    end

    subgraph "Data Layer"
        BookDB[(H2 Database\nBook Data)]
        BorrowingDB[(H2 Database\nBorrowing Data)]
        EmployeeDB[(H2 Database\nEmployee Data)]
        UserDB[(MySQL Database\nUser Data)]
    end

    subgraph "Cache & Infrastructure"
        Redis[Redis\nPort: 6379\nRate Limiting]
        ControlCenter[Kafka Control Center\nPort: 9021]
    end

    Client --> Gateway
    Gateway --> Eureka
    Gateway --> Redis
    Gateway --> BookService
    Gateway --> EmployeeService
    Gateway --> UserService

    BookService --> Eureka
    BorrowingService --> Eureka
    EmployeeService --> Eureka
    UserService --> Eureka
    NotificationService --> Eureka

    BookService --> AxonServer
    BorrowingService --> AxonServer
    EmployeeService --> AxonServer
    NotificationService --> AxonServer

    BookService --> BookDB
    BorrowingService --> BorrowingDB
    EmployeeService --> EmployeeDB
    UserService --> UserDB

    NotificationService --> Kafka
    Kafka --> Zookeeper
    Kafka --> ControlCenter

    BookService -.Event.-> BorrowingService
    BorrowingService -.Event.-> BookService
    BorrowingService -.Event.-> EmployeeService

    style Gateway fill:#4CAF50
    style AxonServer fill:#FF9800
    style Kafka fill:#2196F3
    style Redis fill:#F44336
```

## 🎯 Core Features

- **Book Service** – create, update, delete books; track availability; automatic state rollback on failed transactions.
- **Employee Service** – manage staff records; enforce disciplinary status for borrowing eligibility.
- **Borrowing Service** – issue borrowing requests; enforce business rules via Saga; automatic compensation on failures; audit trail for all borrow/return actions.
- **User Service** – secure authentication with Keycloak; JWT‑based authorization for protected APIs.
- **Notification Service** – email notifications triggered by Kafka events (borrow, return, overdue).

## 🛠️ Tech Stack

- **Java 17** • **Spring Boot 3.x** • **Maven**
- **Axon Framework 4.x** + **Axon Server** (Event Store, Command Bus)
- **Apache Kafka 7.x** • **Zookeeper**
- **MySQL** (persistent user data) • **H2** (in‑memory for domain services)
- **Keycloak** (OAuth2 / OpenID Connect)
- **Redis** (distributed rate limiting)
- **Docker & Docker‑Compose** (containerisation)
- **Kubernetes** (optional orchestration)
- **React 18** (frontend dashboard – not covered by this README but part of the full solution).

## 📦 Project Structure

```
eventSourcing/
├── apigateway/            # Spring Cloud Gateway with rate limiting
├── discoverserver/        # Eureka service registry
├── bookservice/           # Book microservice (CQRS + Event Sourcing)
├── borrowingservice/      # Borrowing microservice (Saga)
├── employeeservice/       # Employee microservice (CQRS)
├── userservice/           # Authentication & user management
├── notificationservice/   # Email + Kafka consumer
├── commonservice/         # Shared DTOs, events, utilities
├── docker-compose.yml     # Infrastructure definition
└── README.md              # This document
```

## 🚀 Getting Started

### Prerequisites
- **Java 17** (or newer)
- **Maven 3.6+**
- **Docker & Docker‑Compose**
- **8 GB RAM** (recommended for all services running locally)

### Run with Docker‑Compose (recommended)
```bash
# Clone repository
git clone <repo‑url>
cd eventSourcing

# Start infrastructure (Eureka, Axon, Kafka, Redis, MySQL, etc.)
docker-compose -f docker-compose-provider.yml up -d

# Start all microservices
docker-compose up -d
```
Check containers:
```bash
docker-compose ps
```
All services expose the API Gateway at `http://localhost:8080`.

### Run Locally (for development)
```bash
# Start infrastructure first
docker-compose -f docker-compose-provider.yml up -d

# Launch each service in separate terminals
cd discoverserver && mvn spring-boot:run
cd ../bookservice && mvn spring-boot:run
cd ../employeeservice && mvn spring-boot:run
cd ../borrowingservice && mvn spring-boot:run
cd ../userservice && mvn spring-boot:run
cd ../notificationservice && mvn spring-boot:run
cd ../apigateway && mvn spring-boot:run
```

## 🔗 API Endpoints & Ports

| Service | Port | Primary URL (via API Gateway) |
|--------|------|-------------------------------|
| API Gateway | 8080 | `http://localhost:8080` |
| Eureka Server | 8761 | `http://localhost:8761` |
| Book Service | 9001 | `/api/v1/books/**` |
| Employee Service | 9002 | `/api/v1/employees/**` |
| Borrowing Service | 9003 | `/api/v1/borrowing/**` |
| User Service | 9005 | `/api/v1/users/**` (protected) |
| Notification Service | 9003 | internal consumer (no external API) |
| Axon Server | 8124 | Event store (internal) |
| Redis | 6379 | Rate‑limiting (internal) |
| Kafka Broker | 9092 | Event streaming (internal) |
| Kafka Control Center | 9021 | `http://localhost:9021` |

## 📚 API Documentation
Swagger UI is auto‑generated by SpringDoc and available at:
```
http://localhost:8080/swagger-ui.html
```
OpenAPI spec (JSON) at `http://localhost:8080/v3/api-docs`.

## 🔐 Security Model

- **Keycloak** realm `ltfullstack` – handles user registration, login, role management.
- **API Gateway** validates JWT on protected routes (`/api/v1/users/**`).
- **Redis** rate limiting: 10 requests/second with burst capacity of 20.
- Service‑to‑service communication inside the mesh is trusted; internal APIs do not expose JWT checks.

## 🎨 Architectural Patterns

- **CQRS** – separate command and query models for Book, Employee, Borrowing services.
- **Event Sourcing** – all state changes persisted as immutable events in Axon Server.
- **Saga (Orchestration)** – Borrowing workflow coordinates Book and Employee services; compensating transactions roll back on failure.
- **API Gateway** – single entry point, cross‑cutting concerns (auth, rate limiting).
- **Service Discovery** – Eureka enables dynamic routing and client‑side load balancing.

## 📊 Database Schemas (illustrative)

```sql
-- Book (H2)
CREATE TABLE book (
    id VARCHAR PRIMARY KEY,
    title VARCHAR,
    author VARCHAR,
    available BOOLEAN
);

-- Employee (H2)
CREATE TABLE employee (
    id VARCHAR PRIMARY KEY,
    first_name VARCHAR,
    last_name VARCHAR,
    disciplined BOOLEAN
);

-- Borrowing (H2)
CREATE TABLE borrowing (
    id VARCHAR PRIMARY KEY,
    book_id VARCHAR,
    employee_id VARCHAR,
    borrow_date DATE,
    return_date DATE
);
```
User data lives in MySQL with standard JPA entities.

## 🧪 Testing

- **Postman collection**: `KeyCloak.postman_collection.json` (covers auth flow and core CRUD APIs).
- **H2 console**: accessible at `http://localhost:<service‑port>/h2-console` for Book, Employee, Borrowing services.
- Unit & integration tests are located under each service's `src/test/java` package and run via `mvn test`.

## 🐳 Docker & Kubernetes

Each microservice ships with its own `Dockerfile`. The `docker-compose.yml` orchestrates the complete stack. For production, Kubernetes manifests live in the `k8s/` directory (Deployments, Services, ConfigMaps, Secrets).

## 📈 Logging & Monitoring

- **SLF4J + Logback** – structured logs per service.
- **Axon Server Dashboard** – event store health.
- **Kafka Control Center** – broker metrics.
- **Eureka Dashboard** – service registration health.
- Optional: integrate Prometheus + Grafana for metrics collection.

## 🤝 Contributing

1. Fork the repository.
2. Create a feature branch.
3. Ensure `mvn test` passes.
4. Open a Pull Request.
5. CI runs lint, tests, and Docker build checks.

## 📄 License

Add appropriate license text here (e.g., MIT, Apache‑2.0).

## 👨‍💻 Author

**LTFullStack** – Udemy instructor, system architect.

---

*This README serves as a professional starter template for any microservice‑based Event‑Sourcing project. Replace placeholders with project‑specific values where needed.*
