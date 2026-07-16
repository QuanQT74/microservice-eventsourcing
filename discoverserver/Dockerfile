# ============================
# Stage 1: Build ứng dụng bằng Maven
# ============================
FROM maven:3.9-eclipse-temurin-21 AS build

# Thư mục làm việc bên trong container
WORKDIR /app

# Copy source code của discoverserver vào container
COPY ./discoverserver ./discoverserver

# Chuyển đến thư mục chứa pom.xml
WORKDIR /app/discoverserver

# Build project và bỏ qua test
RUN mvn clean package -DskipTests

# ============================
# Stage 2: Chạy ứng dụng bằng JRE
# ============================
FROM eclipse-temurin:21-jre-alpine

# Thư mục làm việc
WORKDIR /app

# Copy file jar đã build từ stage build sang image runtime
COPY --from=build /app/discoverserver/target/*.jar discoverserver.jar

# Mở cổng Eureka Server
EXPOSE 8761

# Khởi động ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "discoverserver.jar"]