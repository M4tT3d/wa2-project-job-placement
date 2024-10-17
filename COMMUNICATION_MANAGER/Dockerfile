FROM gradle:8.5.0-alpine AS build
MAINTAINER "Group 3"
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build --no-daemon -x test

FROM openjdk:17-jdk-alpine
EXPOSE 8081
RUN addgroup -S user && adduser -S user -G user
USER user
RUN mkdir /home/user/app
COPY --from=build /home/gradle/src/build/libs/*.jar /home/user/app/spring-boot-application.jar
#run the app
ENTRYPOINT ["java", "-jar","/home/user/app/spring-boot-application.jar"]