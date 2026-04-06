# Use Java image
FROM openjdk:21-jdk-slim

# Copy jar from target folder
COPY target/*.jar app.jar

# Run the app
ENTRYPOINT ["java","-jar","/app.jar"]