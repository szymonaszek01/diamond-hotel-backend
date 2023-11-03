# Build
FROM maven:3.8.2-amazoncorretto-17 AS build
COPY . .

# Build JAR with Maven
RUN mvn clean package
FROM openjdk:17
COPY --from=build /target/diamond-hotel-backend-0.0.1-SNAPSHOT.jar app.jar

# Copy the entire application directory into the Docker image
COPY . /app

# Set the working directory
WORKDIR /app

# Set Entrypoints
ENTRYPOINT ["java","-jar","/app.jar"]
