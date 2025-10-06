# --- Stage 1: Build the application ---
# Use an official Maven image which includes Java 17
FROM maven:3.8.5-openjdk-17 AS builder

# Set the initial working directory
WORKDIR /app

# Copy the entire repository content into the container.
# This will create an 'api' sub-folder inside /app.
COPY . .

# Change the working directory to the project folder where pom.xml is located.
WORKDIR /app/api

# Run the Maven build command. Using 'mvn' directly is more reliable inside this official Maven image.
RUN mvn package -DskipTests


# --- Stage 2: Create the final, lightweight runtime image ---
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy only the compiled JAR file from the builder stage.
# The path must now include the 'api' sub-folder where the build happened.
COPY --from=builder /app/api/target/api-0.0.1-SNAPSHOT.jar ./app.jar

# Expose the port the app runs on
EXPOSE 8080

# The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]