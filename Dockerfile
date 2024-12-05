FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/omni-health-app-0.0.1.jar app.jar

# Expose the port your application will run on
EXPOSE 8080

# Define the command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]
