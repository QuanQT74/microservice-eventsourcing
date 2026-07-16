FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY ./notificationservice/ ./notificationservice
COPY ./commonservice/ ./commonservice

#Lưu trong repository local của Maven để các module khác có thể sử dụng
WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/notificationservice
RUN mvn clean package -DskipTests

#STAGE 2: Chạy ứng dụng bằng JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/notificationservice/target/*.jar notificationservice.jar
EXPOSE 9003
ENTRYPOINT ["java", "-jar", "notificationservice.jar"]