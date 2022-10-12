# syntax=docker/dockerfile:1
FROM openjdk
WORKDIR /app

USER root

# copy source dir
COPY src/ src/

# copy gradle files
COPY gradle/ gradle/
COPY build.gradle .
COPY gradlew .
COPY settings.gradle .

# copy credencials
COPY run.sh .

EXPOSE 8080

# build and run project
RUN microdnf install findutils
ENTRYPOINT chmod +x ./run.sh && ./run.sh
