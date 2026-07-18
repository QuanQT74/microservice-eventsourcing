FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY ./borrowingservice/ ./borrowingservice/
COPY ./commonservice/ ./commonservice/

WORKDIR /app/commonservice
RUN mvn clean install -DskipTests

WORKDIR /app/borrowingservice
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
EXPOSE 9005
COPY --from=build /app/borrowingservice/target/*.jar borrowingservice.jar
ENTRYPOINT ["java", "-jar", "borrowingservice.jar"]