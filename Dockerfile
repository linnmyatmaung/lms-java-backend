# --- Stage 1: Build the JAR file ---
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory inside the container
WORKDIR /app

# Copy Maven project files (pom.xml first for cache)
COPY pom.xml .
COPY src ./src

# Build the application and skip running tests
RUN mvn clean package -DskipTests

# --- Stage 2: Run the application ---
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy the built JAR file from the previous build stage
COPY --from=build /app/target/lms-java-backend-0.0.1-SNAPSHOT.jar app.jar



# Start the Java application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
