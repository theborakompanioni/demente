FROM gradle:8.4-jdk21-alpine AS builder
WORKDIR /app/src

COPY ./ ./
RUN gradle clean bootJar

WORKDIR /app/build
RUN mv /app/src/demented/build/libs/*-boot.jar app.jar \
  && rm -rf /app/src

FROM openjdk:21-jdk-slim

RUN addgroup app \
  && adduser --ingroup app --gecos 'app user' app
USER app
WORKDIR /home/app

COPY --from=builder --chown=app:app /app/build/app.jar /home/app/app.jar

ENTRYPOINT ["java", "-jar", "/home/app/app.jar"]
