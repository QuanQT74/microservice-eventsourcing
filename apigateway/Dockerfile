# ============================
# Stage 1: Build ứng dụng bằng Maven
# ============================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY ./apigateway ./apigateway
WORKDIR /app/apigateway
RUN mvn clean package -DskipTests

# ============================
# Stage 2: Chạy ứng dụng bằng JRE
# ============================

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/apigateway/target/*.jar apigateway.jar
EXPOSE 8085
ENTRYPOINT ["java" , "-jar" , "apigateway.jar"]