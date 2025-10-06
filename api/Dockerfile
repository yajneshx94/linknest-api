# Use an official Java 17 runtime as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and project file
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Copy the actual source code
COPY src ./src

# --- THIS IS THE FIX ---
# Ensure the maven wrapper is executable before running it
RUN chmod +x mvnw

# Build the project and create the JAR file
RUN ./mvnw package -DskipTests

# Tell Docker what port the application runs on
EXPOSE 8080

# The command to run when the container starts
ENTRYPOINT ["java","-jar","target/api-0.0.1-SNAPSHOT.jar"]