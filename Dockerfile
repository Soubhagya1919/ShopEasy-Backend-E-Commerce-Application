# Stage 1: Build the JAR file
FROM maven:3.9.9-eclipse-temurin-22-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline  # Cache dependencies
COPY src ./src
RUN mvn clean package -DskipTests  # Build the application

# Stage 2: Create the final image with just the built JAR
FROM eclipse-temurin:22-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

#FROM openjdk:22-jdk
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]
