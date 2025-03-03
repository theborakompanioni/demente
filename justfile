# This justfile requires https://github.com/casey/just

# Load environment variables from `.env` file.
set dotenv-load
# Fail the script if the env file is not found.
set dotenv-required

project_dir := justfile_directory()
build_dir := project_dir + "/demented/build/libs"
app_uber_jar_pattern := build_dir + "/*-boot.jar"

# print available targets
[group("project-agnostic")]
default:
    @just --list --justfile {{justfile()}}

# evaluate and print all just variables
[group("project-agnostic")]
evaluate:
    @just --evaluate

# print system information such as OS and architecture
[group("project-agnostic")]
system-info:
  @echo "architecture: {{arch()}}"
  @echo "os: {{os()}}"
  @echo "os family: {{os_family()}}"

# clean (remove) the build artifacts
[group("development")]
clean:
    @./gradlew clean

# compile the project
[group("development")]
build:
    @./gradlew build -x test

# list dependency tree of this project
[group("development")]
dependencies:
    @./gradlew dependencyTree

# run unit tests
[group("development")]
test:
    @./gradlew test

# run integration tests
[group("development")]
test-integration:
    @./gradlew integrationTest --rerun-tasks --no-parallel

# run all tests
[group("development")]
test-all:
    @./gradlew test integrationTest --rerun-tasks --no-parallel

# package the app to create an uber jar
[group("development")]
package:
    @./gradlew bootJar --rerun-tasks

# start the app
[group("development")]
start:
    #!/usr/bin/env bash
    declare -r JVM_ARGS="-XX:+UseZGC -XX:+ZGenerational"
    ./gradlew bootRun -Dspring-boot.run.jvmArguments="$JVM_ARGS"

# start the app via its packaged jar (requires 'package' step)
[group("development")]
start-jar:
    #!/usr/bin/env bash
    APP_JAR="{{build_dir}}/app.jar"
    if [ ! -f "$APP_JAR" ]; then
        just clean
        just package
        mv {{app_uber_jar_pattern}} "$APP_JAR"
    else
        echo "Using existing application uber jar at $APP_JAR."
        echo "If you want to recompile the uber jar, run \`./gradlew bootJar\` (or \`just package\`) manually."
    fi
    declare -r JVM_ARGS="-XX:+UseZGC -XX:+ZGenerational"
    java $JVM_ARGS -jar "$APP_JAR" -Dspring.profiles.active=development

# create a docker image
[group("docker")]
docker-build:
    @echo "Creating a docker image ..."
    @docker buildx build -t "$DOCKER_IMAGE_NAME":"$DOCKER_IMAGE_VERSION" .

# size of the docker image
[group("docker")]
docker-image-size:
    @docker images "$DOCKER_IMAGE_NAME"

# run the docker image
[group("docker")]
docker-run:
    @echo "Running container from docker image ..."
    @docker run --publish "$APP_PORT:$APP_PORT" "$DOCKER_IMAGE_NAME":"$DOCKER_IMAGE_VERSION"

# run the docker image and start shell
[group("docker")]
docker-run-shell:
    @echo "Running container from docker image with shell..."
    @docker run --rm --entrypoint="/bin/bash" -it "$DOCKER_IMAGE_NAME":"$DOCKER_IMAGE_VERSION"

# run the docker compose devel setup
[group("docker")]
docker-compose-devel-up:
    @docker compose -f docker-compose-devel.yml up --build

# stop the docker compose devel setup
[group("docker")]
docker-compose-devel-down:
    @docker compose -f docker-compose-devel.yml down --volumes
