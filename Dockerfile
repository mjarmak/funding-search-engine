# Use an official OpenJDK runtime as a parent image
FROM openjdk:17.0.2-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY build/libs/funding-search-engine.jar /app/

# Expose the port the application runs on
EXPOSE 8080

# Define environment variables (optional)
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application when the container starts
CMD ["java", "-jar", "funding-search-engine.jar"]
