# ============================
# Stage 1: Build ứng dụng bằng Maven
# ============================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY ./bookservice/ ./bookservice
COPY ./commonservice/ ./commonservice

#Lưu trong repository local của Maven để các module khác có thể sử dụng
WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/bookservice
RUN mvn clean package -DskipTests

# ============================
# Stage 2: Chạy ứng dụng bằng JRE
# ============================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/bookservice/target/*.jar bookservice.jar
EXPOSE 9001
ENTRYPOINT ["java", "-jar", "bookservice.jar"]
