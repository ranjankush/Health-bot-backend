
# Use lightweight JDK base image
FROM eclipse-temurin:17-jdk-jammy

# Set working directory inside container
WORKDIR /app

# Copy the jar file from your build context
COPY target/GetWellSoon-0.0.1-SNAPSHOT.jar app.jar

# Expose default Spring Boot port
EXPOSE 8081

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
