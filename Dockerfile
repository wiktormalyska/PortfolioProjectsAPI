FROM gradle:8.12.1-jdk23 as build

WORKDIR /app

COPY . /app

RUN gradle build --no-daemon

FROM openjdk:23-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar", "--debug"]