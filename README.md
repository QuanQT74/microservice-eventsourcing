# Library Management System - Microservices with Event Sourcing & CQRS

Dự án Hệ thống Quản lý Thư viện xây dựng trên kiến trúc **Microservices**, áp dụng các pattern tiên tiến như **Event Sourcing**, **CQRS (Command Query Responsibility Segregation)**, và **Saga Pattern** để quản lý các giao dịch phân tán.

## 🏗️ Kiến Trúc Hệ Thống (Architecture)

```text
                       ┌─────────────────┐
                       │  React Frontend │ (Dashboard/UI)
                       └────────┬────────┘
                                │ (Port 8085)
                                ▼
                       ┌─────────────────┐
                       │   API Gateway   │ (Spring Cloud Gateway + Redis Rate Limiter)
                       └────────┬────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        ▼                       ▼                       ▼
┌──────────────┐        ┌──────────────┐        ┌──────────────┐
│ Book Service │        │ User Service │        │ Borrow Service│
│  (Port 9001) │        │  (Port 9004) │        │  (Port 9005) │
└──────┬───────┘        └──────┬───────┘        └──────┬───────┘
       │                       │                       │
       │               ┌───────▼────────┐              │
       │               │Employee Service│              │
       │               │  (Port 9002)   │              │
       │               └───────┬────────┘              │
       │                       │                       │
       └───────────────────────┼───────────────────────┘
                               │
                               ▼
        ┌───────────────────────────────────────────────┐
        │  Axon Server (Event Store & Command Bus)      │ (Port 8124)
        └───────────────────────────────────────────────┘
                               ▲
                               │ Saga Orchestration
                               ▼
        ┌───────────────────────────────────────────────┐
        │           Apache Kafka (Event Streaming)      │ (Port 9092)
        └───────────────────────┬───────────────────────┘
                                │
                                ▼
                        ┌──────────────┐
                        │ Notification │ (Email Service - Port 9003)
                        └──────────────┘
```

---

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

### Backend (Microservices)
*   **Java 17** & **Spring Boot 3.x**
*   **Spring Cloud Netflix Eureka** (Service Registry)
*   **Spring Cloud Gateway** (API Gateway)
*   **Axon Framework & Axon Server** (Event Sourcing, CQRS & Saga Orchestration)
*   **Apache Kafka & Zookeeper** (Event Streaming / Message Broker)
*   **Keycloak** (IAM - OAuth2 / OpenID Connect Identity Provider)
*   **Redis** (Rate Limiter cho API Gateway)
*   **MySQL & PostgreSQL** (Cơ sở dữ liệu - Keycloak dùng Postgres, các service dùng MySQL)
*   **Spring Data JPA & Hibernate**

### Frontend (Dashboard)
*   **React 18** (Vite + TypeScript)
*   **Tailwind CSS & Shadcn UI** (Styling)
*   **Lucide React** (Icons)
*   **TanStack React Query** (State management & caching)
*   **Recharts** (Visualizations)

---

## 📦 Danh Sách Các Services (Services Directory)

| Service Name | Port | Description |
| :--- | :--- | :--- |
| `discoveryservice` | `8761` | Service Registry (Eureka Server) đăng ký các service |
| `apigateway` | `8085` | API Gateway điều phối request, định tuyến, rate limiting bằng Redis |
| `axonserver` | `8024`/`8124` | Event Store lưu trữ sự kiện và điều phối Command/Event |
| `bookservice` | `9001` | Quản lý sách (Thêm, sửa, xóa, tìm kiếm sách) dùng Axon CQRS |
| `employeeservice` | `9002` | Quản lý nhân viên thư viện |
| `userservice` | `9004` | Quản lý người dùng, tích hợp Keycloak authentication |
| `borrowingservice` | `9005` | Quản lý mượn/trả sách, điều phối Saga khi mượn sách, phát Kafka event |
| `notificationservice` | `9003` | Consumer lắng nghe Kafka event từ Borrowing service gửi email thông báo |
| `keycloak` | `8180` | Quản lý tài khoản, phân quyền OAuth2 |
| `control-center` | `9021` | Giao diện quản lý Apache Kafka |
| `frontend` | `5173` | React Dashboard quản trị hệ thống |

---

## 🚀 Hướng Dẫn Khởi Chạy (Getting Started)

### Yêu Cầu Hệ Thống (Prerequisites)
*   [Docker & Docker Compose](https://www.docker.com/products/docker-desktop/)
*   [JDK 17](https://oracle.com/java/technologies/downloads/)
*   [Maven 3.x](https://maven.apache.org/download.cgi)
*   [Node.js (v18+) & npm](https://nodejs.org/)

### Bước 1: Khởi chạy hạ tầng qua Docker Compose
Chạy lệnh sau tại thư mục gốc để dựng tất cả các service nền tảng (Axon Server, Kafka, Redis, MySQL, Keycloak, Postgres):

```bash
docker-compose up -d axonserver redis zookeeper broker control-center mysql keycloak keycloak-db
```

Đợi các container khởi động hoàn toàn (đặc biệt là `axonserver`, `mysql` và `keycloak`).

### Bước 2: Build & Chạy các Spring Boot Services
Dùng Maven để build các project và chạy độc lập hoặc build docker image chạy qua compose:

1. **Build toàn bộ project Java**:
   ```bash
   mvn clean install -DskipTests
   ```
2. **Khởi chạy các service**:
   Có thể start từ IDE của bạn hoặc dùng docker-compose để chạy toàn bộ:
   ```bash
   docker-compose up -d --build
   ```

### Bước 3: Khởi chạy Frontend
Di chuyển vào thư mục `frontend`, cài đặt dependencies và chạy môi trường dev:

```bash
cd frontend
npm install
npm run dev
```
Truy cập Dashboard tại: `http://localhost:5173`

---

## 🔄 Luồng Nghiệp Vụ Chính (Key Workflows)

### 1. Luồng Mượn Sách (Borrow Book Saga)
Áp dụng **Saga Pattern (Orchestration)** để đảm bảo tính nhất quán dữ liệu (Eventual Consistency) qua các bước:
1. `BorrowingService` nhận Command tạo yêu cầu mượn sách -> Phát sự kiện `BorrowingCreatedEvent`.
2. **BorrowingSaga** bắt đầu -> Gửi Command tới `BookService` để kiểm tra & cập nhật trạng thái sách (trừ đi 1 cuốn có sẵn).
3. Nếu sách khả dụng -> Gửi Command tới `UserService` để kiểm tra thông tin chi tiết và quyền mượn của thành viên.
4. Nếu cả 2 bước thành công -> Hoàn thành giao dịch Saga, cập nhật trạng thái mượn thành `APPROVED`. Phát Kafka event gửi mail thông báo.
5. **Cơ chế đền bù (Compensating Transactions)**: Nếu bước kiểm tra người dùng thất bại -> Gửi Command phục hồi trạng thái sách (cộng lại sách), cập nhật trạng thái mượn thành `REJECTED`.

### 2. Gửi Email Thông Báo (Kafka Integration)
*   Khi giao dịch mượn/trả sách hoàn tất, `BorrowingService` phát event lên Kafka topic.
*   `NotificationService` tiêu thụ (consume) message này và xử lý gửi email thông báo mượn/trả sách thành công tới khách hàng.
