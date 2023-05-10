#
# Build stage
#
FROM maven:3.8.2-amazoncorretto-17 AS build
COPY . .
RUN mvn clean package
#
# Package stage
#
FROM openjdk:17
COPY --from=build /target/diamond-hotel-backend-0.0.1-SNAPSHOT.jar app.jar
# ENV PORT=8000
EXPOSE 8000
ENTRYPOINT ["java","-jar","/app.jar"]
