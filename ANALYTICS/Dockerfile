FROM gradle:8.5.0-alpine AS build
MAINTAINER "Group 3"
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build --no-daemon -x test

FROM openjdk:17-jdk-alpine
EXPOSE 8087
RUN addgroup -S analyticsuser && adduser -S analyticsuser -G analyticsuser
USER analyticsuser
RUN mkdir /home/analyticsuser/app
COPY --from=build /home/gradle/src/build/libs/*.jar /home/analyticsuser/app/spring-boot-application.jar
#run the app
ENTRYPOINT ["java", "-jar","/home/analyticsuser/app/spring-boot-application.jar"]
