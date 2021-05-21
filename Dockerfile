FROM gradle:latest as builder
COPY build.gradle /gradle/build.gradle
COPY src /gradle/src/
WORKDIR /gradle/

RUN gradle clean build
RUN gradle shadowJar

FROM openjdk:8-jre-alpine
COPY --from=builder /gradle/build/libs/ /app/

ENTRYPOINT [ "java", "-jar", "/app/gradle-1.0-SNAPSHOT-all.jar"]