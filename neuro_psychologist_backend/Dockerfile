# Dockerfile
FROM eclipse-temurin:21-jre-alpine

# Create application directory
WORKDIR /app

# Copy the JAR file from target directory
COPY target/neuro_psychologist_backend-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app runs on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]