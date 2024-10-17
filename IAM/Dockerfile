FROM gradle:8.5.0-alpine AS build
LABEL MAINTAINER="Group 3"
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build --no-daemon -x test

FROM openjdk:17-jdk-alpine
EXPOSE 8080
RUN addgroup -S iamuser && adduser -S iamuser -G iamuser
USER iamuser
RUN mkdir /home/iamuser/app
COPY --from=build /home/gradle/src/build/libs/*.jar /home/iamuser/app/spring-boot-application.jar
#run the app
ENTRYPOINT ["java", "-jar","/home/iamuser/app/spring-boot-application.jar"]