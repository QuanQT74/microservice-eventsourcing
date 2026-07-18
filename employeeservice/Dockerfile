# ============================
# Stage 1: Build ứng dụng bằng Maven
# ============================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY ./employeeservice/ ./employeeservice/
COPY ./commonservice/ ./commonservice/

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/employeeservice
RUN mvn clean package -DskipTests

# ============================
# Stage 2: Chạy ứng dụng bằng JRE
# ============================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/employeeservice/target/employeeservice-0.0.1-SNAPSHOT.jar employeeservice.jar
EXPOSE 9002
ENTRYPOINT ["java", "-jar", "employeeservice.jar"]


