# 📚 Library Management System (Microservices)

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-green?style=for-the-badge&logo=springboot" alt="Spring Boot 3" />
  <img src="https://img.shields.io/badge/Axon_Framework-4.x-blue?style=for-the-badge&logo=axon" alt="Axon" />
  <img src="https://img.shields.io/badge/Apache_Kafka-3.x-black?style=for-the-badge&logo=apachekafka" alt="Kafka" />
  <img src="https://img.shields.io/badge/Keycloak-26.0-red?style=for-the-badge&logo=keycloak" alt="Keycloak" />
  <img src="https://img.shields.io/badge/React-18-blue?style=for-the-badge&logo=react" alt="React" />
</p>

---

## 📖 Tổng quan hệ thống

Hệ thống quản lý thư viện hiện đại được xây dựng dựa trên kiến trúc **Microservices** hướng sự kiện. Dự án áp dụng các triết lý thiết kế nâng cao gồm **Event Sourcing**, **CQRS** và điều phối giao dịch phân tán **Saga Orchestration** thông qua **Axon Framework** kết hợp luồng truyền tin thời gian thực **Apache Kafka**.

---

## 🏗️ Sơ đồ Kiến trúc & Luồng dữ liệu

```text
                                  ┌──────────────────┐
                                  │  React Dashboard │ (UI - Port 5173)
                                  └────────┬─────────┘
                                           │ Request (Port 8085)
                                           ▼
                                  ┌──────────────────┐
                                  │   API Gateway    │ (Spring Cloud Gateway)
                                  └────────┬─────────┘
                                           │
        ┌──────────────────────────────────┼──────────────────────────────────┐
        ▼ (Port 9001)                      ▼ (Port 9004)                      ▼ (Port 9005)
┌──────────────┐                  ┌──────────────┐                  ┌────────────────┐
│ Book Service │                  │ User Service │                  │ Borrow Service │
└──────┬───────┘                  └──────┬───────┘                  └────────┬───────┘
       │                                 │ Feign Client                      │
       │                                 ▼ (Port 9002)                       │
       │                          ┌──────────────┐                           │
       │                          │ Employee Svc │                           │
       │                          └──────┬───────┘                           │
       └─────────────────────────────────┼───────────────────────────────────┘
                                         │
                                         ▼ (Commands / Events)
                    ┌──────────────────────────────────────────┐
                    │  Axon Server (Event Store & Command Bus)  │ (Port 8124)
                    └────────────────────┬─────────────────────┘
                                         │
                                         │ Emit Events
                                         ▼
                    ┌──────────────────────────────────────────┐
                    │       Apache Kafka (Message Broker)      │ (Port 9092)
                    └────────────────────┬─────────────────────┘
                                         │ Subscribe Event
                                         ▼
                    ┌──────────────────────────────────────────┐
                    │       Notification Service (Email)       │ (Port 9003)
                    └──────────────────────────────────────────┘
```

---

## 🛠️ Công Nghệ Chủ Đạo

### 🧱 Backend Microservices
*   **Spring Boot 3.x & Cloud**: Nền tảng phát triển dịch vụ & định tuyến API.
*   **Axon Framework & Server**: Xử lý kiến trúc CQRS, Event Store lưu vết mọi lịch sử biến động dữ liệu, điều phối Saga.
*   **Apache Kafka**: Streaming tin nhắn bất đồng bộ phục vụ thông báo qua Email.
*   **Keycloak IAM**: Xác thực & phân quyền tập trung (OAuth2/OIDC).
*   **Redis**: Cấu hình cơ chế giới hạn lượt gọi (Rate Limiting).
*   **Cơ sở dữ liệu**: Đa cơ sở dữ liệu (MySQL cho dữ liệu nghiệp vụ, PostgreSQL cho Keycloak).

### 🖥️ Frontend Administration
*   **Vite + React 18 + TypeScript**: Tối ưu tốc độ tải trang và trải nghiệm người dùng.
*   **Shadcn UI + Tailwind CSS**: Thiết kế giao diện Dashboard trực quan.
*   **TanStack Query**: Quản lý trạng thái client-side và cache dữ liệu từ API.

---

## 📂 Tổng quan về Service

| Service | Port | Database | Nhiệm vụ chính |
| :--- | :--- | :--- | :--- |
| 🛡️ `apigateway` | `8085` | Redis | Điểm truy cập duy nhất, xác thực Token JWT, Rate Limit |
| 🔍 `discoveryservice` | `8761` | - | Đăng ký và phát hiện dịch vụ tự động (Eureka Server) |
| ⚡ `axonserver` | `8124` | Axon Store | Lưu trữ Event Log, điều phối command/query |
| 📚 `bookservice` | `9001` | MySQL | Quản lý danh mục sách (CQRS) |
| 👥 `userservice` | `9004` | MySQL | Quản lý bạn đọc, tích hợp Keycloak |
| 💼 `employeeservice` | `9002` | MySQL | Quản lý nhân sự thủ thư |
| 🔄 `borrowingservice` | `9005` | MySQL | Xử lý mượn/trả sách, điều phối Saga |
| ✉️ `notificationservice`| `9003` | - | Lắng nghe Kafka event để gửi Email tự động |
| 🔑 `keycloak` | `8180` | PostgreSQL | Identity Provider quản lý User/Role |

---

## 🔄 Quy trình Nghiệp vụ Nổi bật

### 🔁 Saga Pattern trong Mượn Sách

Khi người dùng thực hiện mượn sách, một chuỗi giao dịch phân tán (Saga) được kích hoạt để đảm bảo tính nhất quán dữ liệu:

```text
[BorrowingService]                  [BookService]                   [UserService]
        │                                 │                               │
        │── Create Borrowing Request ────>│                               │
        │                                 │── Reserve Book (Qty - 1) ────>│
        │                                 │                               │── Check User & Limit ──┐
        │                                 │                               │                        │
        │<── APPROVED ────────────────────┴───────────────────────────────┴<───────────────────────┘
        │
        ▼ (Publish Kafka event)
[NotificationService] ──> Send Email Notification
```

*   **Trường hợp lỗi (Rollback / Compensating Transaction)**: Nếu `UserService` từ chối (người dùng vi phạm nội quy/hết hạn thẻ), Saga sẽ gửi lệnh đền bù tới `BookService` để khôi phục số lượng sách ban đầu (+1) và hủy yêu cầu mượn.

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Dự Án

### 📋 Yêu cầu hệ thống
*   Docker & Docker Desktop
*   Java Development Kit (JDK) 17
*   Maven 3.x
*   Node.js (phiên bản 18 trở lên)

### Bước 1: Khởi chạy các container cơ sở hạ tầng
Chạy lệnh sau tại thư mục gốc chứa file `docker-compose.yml`:
```bash
docker-compose up -d axonserver redis zookeeper broker control-center mysql keycloak keycloak-db
```
*Đảm bảo các cổng hệ thống như `8124`, `9092`, `3307`, `8180` không bị chiếm dụng trước khi chạy.*

### Bước 2: Biên dịch và chạy Backend Services
1. **Biên dịch mã nguồn Java**:
   ```bash
   mvn clean install -DskipTests
   ```
2. **Khởi chạy toàn bộ services Spring Boot**:
   ```bash
   docker-compose up -d --build
   ```

### Bước 3: Khởi chạy Giao diện Frontend
1. Truy cập thư mục frontend:
   ```bash
   cd frontend
   ```
2. Cài đặt các thư viện cần thiết:
   ```bash
   npm install
   ```
3. Chạy ứng dụng ở chế độ nhà phát triển:
   ```bash
   npm run dev
   ```
Giao diện quản trị sẽ sẵn sàng tại địa chỉ: `http://localhost:5173`.
