#
# Build stage
#
FROM maven:3.6.0-jdk-8-slim AS build
COPY src /src
COPY pom.xml .
#RUN mvn -f pom.xml clean package
RUN  mvn -f pom.xml package -Dmaven.test.skip=true
#
# Package Stage
#
FROM java:8-jdk
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
COPY --from=build target/*-SNAPSHOT.jar app.jar

ENV JAVA_HOME              /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH                   $PATH:$JAVA_HOME/bin
ENV JAVA_OPTIONS    -Xmx256m
ENV TIME_ZONE Asia/Singapore

ENV SPRING_PROFILES_ACTIVE dt

RUN echo "$TIME_ZONE" > /etc/timezone
RUN dpkg-reconfigure -f noninteractive tzdata

WORKDIR /app/gateway

#TESTING
EXPOSE 8010

ENTRYPOINT ["java","-jar","/app.jar","-Djava.security.egd=file:/dev/./urandom"]

#"-Dspring.profiles.active=dt"

