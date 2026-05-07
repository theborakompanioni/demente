FROM ghcr.io/theborakompanioni/java-healthcheck:master@sha256:2421f2124482223f8548c74a03df06bcf6aba47c7cc74dd91ae3c8db1ed20e35 AS healthcheck

FROM azul/zulu-openjdk-alpine:21.0.11-jre-headless@sha256:65182054459de6205fd7709eb7fa28337ee9c9a124b9ebd6765069d86511c1bb

ARG JAR_FILE=demented/build/libs/*-boot.jar
COPY ${JAR_FILE} /app.jar

COPY --from=healthcheck HealthCheck.java /HealthCheck.java

ENTRYPOINT ["java", "-jar", "/app.jar"]

HEALTHCHECK --interval=10s --timeout=5s --retries=20 CMD ["java", "HealthCheck.java", "http://localhost:9001/actuator/health"]
