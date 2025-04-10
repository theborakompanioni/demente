FROM gradle:8.4-jdk21-alpine@sha256:039614406f658505c65357c71056442e65b30b3f19400199d96265c88261ecc6 AS builder
WORKDIR /app/src

COPY ./ ./
RUN gradle clean bootJar

WORKDIR /app/build
RUN mv /app/src/demented/build/libs/*-boot.jar app.jar \
  && rm -rf /app/src

FROM ghcr.io/theborakompanioni/java-healthcheck:master@sha256:fba2caf06a8b1f324d18485dbf9389f435b9972ec5fa772f56c012ee9bb77c44 AS healthcheck

FROM azul/zulu-openjdk-alpine:21-jre-headless@sha256:c3312be85fb542362ac000df1112475dee65ad87021269a6a980f2c7fe9ea536

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
