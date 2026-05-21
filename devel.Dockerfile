FROM ghcr.io/theborakompanioni/java-healthcheck:master@sha256:2421f2124482223f8548c74a03df06bcf6aba47c7cc74dd91ae3c8db1ed20e35 AS healthcheck

FROM azul/zulu-openjdk-alpine:21.0.11-jre-headless@sha256:fd692ae3e801f365dc878fe1215c7ae4c6e2252eb8107f51abbdda10dfc5f27e

ARG JAR_FILE=demented/build/libs/*-boot.jar
COPY ${JAR_FILE} /app.jar

COPY --from=healthcheck HealthCheck.java /HealthCheck.java

ENTRYPOINT ["java", "-jar", "/app.jar"]

HEALTHCHECK --interval=10s --timeout=5s --retries=20 CMD ["java", "HealthCheck.java", "http://localhost:9001/actuator/health"]
