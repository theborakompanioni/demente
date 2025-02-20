FROM gradle:8.4-jdk21-alpine@sha256:039614406f658505c65357c71056442e65b30b3f19400199d96265c88261ecc6 AS builder
WORKDIR /app/src

COPY ./ ./
RUN gradle clean bootJar

WORKDIR /app/build
RUN mv /app/src/demented/build/libs/*-boot.jar app.jar \
  && rm -rf /app/src

FROM openjdk:21-jdk-slim@sha256:7072053847a8a05d7f3a14ebc778a90b38c50ce7e8f199382128a53385160688

RUN addgroup app \
  && adduser --ingroup app --gecos 'app user' app
USER app
WORKDIR /home/app

COPY --from=builder --chown=app:app /app/build/app.jar /home/app/app.jar

ENTRYPOINT ["java", "-jar", "/home/app/app.jar"]
