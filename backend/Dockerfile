FROM gradle:8.10.2-jdk21-alpine AS build
WORKDIR /workspace

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN mkdir -p /data/wcpl/avatars

ENV WCPL_DATA_DIR=/data/wcpl
ENV SERVER_PORT=8080

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
