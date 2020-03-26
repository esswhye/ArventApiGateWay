FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/zuul-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar


ENV TIME_ZONE              Asia/Singapore
ENV SPRING_PROFILES_ACTIVE dt

ENTRYPOINT ["java","-jar","/app.jar"]

