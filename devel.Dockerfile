FROM openjdk:21-jdk-slim
ARG JAR_FILE=demented/build/libs/*-boot.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
