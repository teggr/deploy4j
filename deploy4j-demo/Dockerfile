FROM eclipse-temurin:24-jre-alpine
LABEL authors="robin"
LABEL service="teggr/${project.artifactId}:${project.version}"

EXPOSE 8080

# Create a mountpoint for data
VOLUME /data

# Copy the Spring Boot jar into the image
COPY target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
