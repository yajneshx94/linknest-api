# --- Stage 1: Build the application ---
FROM maven:3.8.5-openjdk-17 AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and project file
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Grant execute permission to the mvnw script
RUN chmod +x mvnw

# Copy the rest of the source code
COPY src ./src

# Build the project and create the JAR file. Skip tests to speed up the build.
RUN ./mvnw package -DskipTests


# --- Stage 2: Create the final, lightweight runtime image ---
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy only the compiled JAR file from the builder stage
COPY --from=builder /app/target/api-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port the app runs on
EXPOSE 8080

# The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]