FROM ghcr.io/theborakompanioni/java-healthcheck:master@sha256:fba2caf06a8b1f324d18485dbf9389f435b9972ec5fa772f56c012ee9bb77c44  AS healthcheck

FROM openjdk:21-jdk-slim@sha256:7072053847a8a05d7f3a14ebc778a90b38c50ce7e8f199382128a53385160688
ARG JAR_FILE=demented/build/libs/*-boot.jar
COPY ${JAR_FILE} app.jar

COPY --from=healthcheck HealthCheck.java /HealthCheck.java

ENTRYPOINT ["java", "-jar", "/app.jar"]

HEALTHCHECK --interval=10s --timeout=5s --retries=20 CMD ["java", "HealthCheck.java", "http://localhost:9001/actuator/health"]
