FROM gradle:8.12.1-jdk21 as build

WORKDIR /app

COPY . /app

RUN gradle build --no-daemon -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]