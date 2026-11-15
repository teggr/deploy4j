FROM eclipse-temurin:24-jre-alpine
LABEL authors="robin"
LABEL service="teggr/${project.artifactId}:${project.version}"

# curl is required by the deploy4j container healthcheck
RUN apk add --no-cache curl

EXPOSE 8080

# Create a mountpoint for data
VOLUME /data

# Copy the Spring Boot jar into the image
COPY target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
