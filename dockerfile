# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Copy entire project (not just pom.xml!)
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY --from=build /app/target/takatuf-0.0.1-SNAPSHOT.jar takatuf.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "takatuf.jar"]
