FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/log-transform-service-0.0.1-SNAPSHOT.jar app.jar

# Use non-root user
USER 1001
# Expose necessary ports
EXPOSE 8080 443

ENTRYPOINT ["java","-Dspring.profiles.active=local", "-jar", "app.jar"]