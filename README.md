# 📚 <PROJECT_NAME> - Microservice Event Sourcing

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/) [![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot) [![Axon Framework](https://img.shields.io/badge/Axon_Framework-FFA500?style=for-the-badge&logoColor=white)](https://www.axoniq.io/) [![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/) [![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/) [![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/) [![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/) [![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)](https://kubernetes.io/) [![Maven](https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/) [![Keycloak](https://img.shields.io/badge/Keycloak-ADD8E6?style=for-the-badge&logo=keycloak&logoColor=black)](https://www.keycloak.org/) [![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)](https://www.postman.com/)

## 📋 Tổng Quan Dự Án

<PROJECT_DESCRIPTION>

## 🏗️ Kiến Trúc Hệ Thống

```mermaid
graph TB
    subgraph "Client Layer"
        Client[Client Applications]
    end

    subgraph "API Gateway Layer"
        Gateway[API Gateway<br/>Port: 8080<br/>Rate Limiting & Auth]
    end

    subgraph "Service Discovery"
        Eureka[Eureka Server<br/>Port: 8761]
    end

    subgraph "Microservices"
        BookService[Book Service<br/>Port: 9001<br/>CQRS + Event Sourcing]
        BorrowingService[Borrowing Service<br/>Port: 9003<br/>Saga Orchestration]
        EmployeeService[Employee Service<br/>Port: 9002<br/>CQRS + Event Sourcing]
        UserService[User Service<br/>Port: 9005<br/>Authentication]
        NotificationService[Notification Service<br/>Port: 9003<br/>Email & Kafka Consumer]
        CommonService[Common Service<br/>Shared Components]
    end

    subgraph "Event Store & Messaging"
        AxonServer[Axon Server<br/>Port: 8124<br/>Event Store & Message Router]
        Kafka[Apache Kafka<br/>Port: 9092<br/>Event Streaming]
        Zookeeper[Zookeeper<br/>Port: 2181]
    end

    subgraph "Data Layer"
        BookDB[(H2 Database<br/>Book Data)]
        BorrowingDB[(H2 Database<br/>Borrowing Data)]
        EmployeeDB[(H2 Database<br/>Employee Data)]
        UserDB[(MySQL Database<br/>User Data)]
    end

    subgraph "Cache & Infrastructure"
        Redis[Redis<br/>Port: 6379<br/>Rate Limiting]
        ControlCenter[Kafka Control Center<br/>Port: 9021]
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

    BookService -.Event.-&gt; BorrowingService
    BorrowingService -.Event.-&gt; BookService
    BorrowingService -.Event.-&gt; EmployeeService

    style Gateway fill:#4CAF50
    style AxonServer fill:#FF9800
    style Kafka fill:#2196F3
    style Redis fill:#F44336
```

## 🎯 Tính Năng Chính

### 📖 Quản Lý Sách (Book Service)
- ✅ Tạo, cập nhật, xóa sách
- ✅ Theo dõi trạng thái sẵn sàng
- ✅ Rollback khi giao dịch thất bại

### 👥 Quản Lý Nhân Viên (Employee Service)
- ✅ CRUD nhân viên
- ✅ Kiểm tra trạng thái kỷ luật
- ✅ Kiểm tra điều kiện mượn sách

### 📝 Quản Lý Mượn Sách (Borrowing Service)
- ✅ Tạo phiếu mượn
- ✅ Saga orchestration để đảm bảo tính nhất quán
- ✅ Rollback tự động

### 🔐 Quản Lý Người Dùng (User Service)
- ✅ Xác thực, phân quyền
- ✅ Tích hợp Keycloak OAuth2/JWT

### 📧 Thông Báo (Notification Service)
- ✅ Gửi email
- ✅ Xử lý Kafka events

## 🛠️ Tech Stack

- **Spring Boot 3.x**
- **Java 17**
- **Axon Framework & Server**
- **Apache Kafka & Zookeeper**
- **MySQL / H2**
- **Keycloak**
- **Redis**
- **Docker & Docker Compose**
- **Kubernetes**

## 📦 Cấu Trúc Dự Án
```
<PROJECT_ROOT>/
├── apigateway/              # API Gateway
├── discoverserver/          # Eureka
├── bookservice/             # Book microservice
├── borrowingservice/        # Borrowing microservice
├── employeeservice/         # Employee microservice
├── userservice/             # User & Auth microservice
├── notificationservice/     # Notification microservice
└── commonservice/           # Shared components
```

## 🚀 Hướng Dẫn Chạy Dự Án

### Yêu Cầu Hệ Thống
- Java 17+, Maven, Docker & Docker Compose

### Docker Compose
```bash
git clone <repo-url>
cd <repo-dir>

docker-compose -f docker-compose-provider.yml up -d   # infra
docker-compose up -d                                # all services
```

### Local Development
```bash
# start infra
docker-compose -f docker-compose-provider.yml up -d

# start services one by one
cd discoverserver && mvn spring-boot:run
cd ../bookservice && mvn spring-boot:run
# … repeat for other services
```

## 🔗 Endpoints & Ports
| Service | Port | Description |
|---------|------|-------------|
| API Gateway | 8080 | Entry point |
| Eureka | 8761 | Service registry |
| Book Service | 9001 | CRUD sách |
| Employee Service | 9002 | CRUD nhân viên |
| Borrowing Service | 9003 | Quản lý mượn sách |
| User Service | 9005 | Auth |
| Notification Service | 9003 | Email |
| Axon Server | 8124 | Event store |
| Redis | 6379 | Rate limiting |
| Kafka | 9092 | Event streaming |

## 📚 API Docs
- Swagger UI tại `http://localhost:<gateway-port>/swagger-ui.html`
- OpenAPI JSON tại `http://localhost:<gateway-port>/v3/api-docs`

## 🔐 Security
- Keycloak realm: `<REALM_NAME>`
- JWT validation via Spring Security OAuth2 Resource Server
- Rate limiting: Redis (10 req/s, burst 20)

## 🎨 Patterns Used
- **CQRS** – tách command và query
- **Event Sourcing** – lưu lịch sử events
- **Saga** – quản lý transaction phân tán
- **API Gateway** – single entry point
- **Service Discovery** – Eureka

## 📊 Database Schema (example)
```sql
-- Book
CREATE TABLE book (id VARCHAR PRIMARY KEY, name VARCHAR, author VARCHAR, is_ready BOOLEAN);
-- Employee
CREATE TABLE employee (id VARCHAR PRIMARY KEY, first_name VARCHAR, last_name VARCHAR, is_disciplined BOOLEAN);
-- Borrowing
CREATE TABLE borrowing (id VARCHAR PRIMARY KEY, book_id VARCHAR, employee_id VARCHAR, borrowing_date DATE, return_date DATE);
```

## 🧪 Testing
- Import `KeyCloak.postman_collection.json` into Postman
- Use H2 console (`/h2-console`) for in‑memory DB inspection

## 🐳 Docker & K8s
- Each microservice has a `Dockerfile`
- Kubernetes manifests in `k8s/` directory

## 📝 Logging & Monitoring
- **Slf4j + Logback**
- **Axon Dashboard**, **Kafka Control Center**, **Eureka Dashboard**

## 🤝 Contributing
Fork, tạo branch, PR. Kiểm tra CI trước khi merge.

## 📄 License
Add your license here.

## 👨‍💻 Author
<YOUR_NAME>

---

*Template này dùng làm khởi đầu cho bất kỳ dự án microservice dựa trên Event Sourcing. Thay `<PLACEHOLDERS>` bằng thông tin dự án thực tế.*
