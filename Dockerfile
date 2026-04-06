# Use Java image
FROM eclipse-temurin:21-jdk

# Copy jar from target folder
COPY target/*.jar app.jar

# Run the app
ENTRYPOINT ["java","-jar","/app.jar"]