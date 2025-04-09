FROM ghcr.io/theborakompanioni/java-healthcheck:master@sha256:fba2caf06a8b1f324d18485dbf9389f435b9972ec5fa772f56c012ee9bb77c44 AS healthcheck

FROM azul/zulu-openjdk-alpine:21-jre-headless@sha256:c3312be85fb542362ac000df1112475dee65ad87021269a6a980f2c7fe9ea536

ARG JAR_FILE=demented/build/libs/*-boot.jar
COPY ${JAR_FILE} app.jar

COPY --from=healthcheck HealthCheck.java /HealthCheck.java

ENTRYPOINT ["java", "-jar", "/app.jar"]

HEALTHCHECK --interval=10s --timeout=5s --retries=20 CMD ["java", "HealthCheck.java", "http://localhost:9001/actuator/health"]
