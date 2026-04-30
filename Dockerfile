FROM gradle:9.5-jdk21-alpine@sha256:a97d4df49b5ae46e42ad7470d551dd393911c79c190f03949a61779f1cb21b11 AS builder
WORKDIR /app/src

COPY ./ ./
RUN gradle clean bootJar

WORKDIR /app/build
RUN mv /app/src/demented/build/libs/*-boot.jar app.jar \
  && rm -rf /app/src

FROM ghcr.io/theborakompanioni/java-healthcheck:master@sha256:fba2caf06a8b1f324d18485dbf9389f435b9972ec5fa772f56c012ee9bb77c44 AS healthcheck

FROM azul/zulu-openjdk-alpine:21.0.11-jre-headless@sha256:b7a736aa28af4d9350f5cedfe44dac75c83b29de5f64770241d289504cbb1c06

RUN addgroup --system --gid 1000 app \
  && adduser --system --uid 1000 --ingroup app --disabled-password app
USER app
WORKDIR /home/app

COPY --from=builder --chown=app:app /app/build/app.jar /home/app/app.jar
COPY --from=healthcheck --chown=app:app HealthCheck.java /home/app/HealthCheck.java

LABEL org.opencontainers.image.title=demente
LABEL org.opencontainers.image.description="A powerful, extensible, open source nostr relay"
LABEL org.opencontainers.image.url=https://github.com/theborakompanioni/demente
LABEL org.opencontainers.image.documentation=https://github.com/theborakompanioni/demente
LABEL org.opencontainers.image.vendor="theborakompanioni"
LABEL org.opencontainers.image.licenses=Apache-2.0
LABEL org.opencontainers.image.source="https://github.com/theborakompanioni/demente"

ENTRYPOINT ["java", "-jar", "/home/app/app.jar"]

HEALTHCHECK --interval=10s --timeout=5s --retries=20 CMD ["java", "/home/app/HealthCheck.java", "http://localhost:9001/actuator/health"]
