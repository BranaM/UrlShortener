FROM openjdk:17-jdk-slim
LABEL authors="38164"

WORKDIR /app

COPY target/urlshortener-0.0.1-SNAPSHOT.jar urlshortener.jar

ENTRYPOINT ["java", "-jar", "urlshortener.jar"]