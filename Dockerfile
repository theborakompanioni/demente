FROM gradle:9.6-jdk21-alpine@sha256:615d75ef01f9c1aacab46fd022828a6d35738308fe89b64be3ec7e113af03c00 AS builder
WORKDIR /app/src

COPY ./ ./
RUN gradle clean bootJar

WORKDIR /app/build
RUN mv /app/src/demented/build/libs/*-boot.jar app.jar \
  && rm -rf /app/src

FROM ghcr.io/theborakompanioni/java-healthcheck:master@sha256:2421f2124482223f8548c74a03df06bcf6aba47c7cc74dd91ae3c8db1ed20e35 AS healthcheck

FROM azul/zulu-openjdk-alpine:21.0.11-jre-headless@sha256:dc52da042f62d09795ea8a312c8fffb797c91ed05538ccf2cd0149ef7f478dd8

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
