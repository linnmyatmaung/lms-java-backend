# --- Stage 1: Build the JAR file ---
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# --- Stage 2: Run the application ---
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/lms-java-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Set ENTRYPOINT for running Java app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
