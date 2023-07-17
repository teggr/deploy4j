FROM eclipse-temurin:17-alpine

RUN mkdir /opt/app

COPY target/spring-boot-web-application-0.0.1-SNAPSHOT.jar /opt/app

CMD ["java", "-jar", "/opt/app/spring-boot-web-application-0.0.1-SNAPSHOT.jar"]