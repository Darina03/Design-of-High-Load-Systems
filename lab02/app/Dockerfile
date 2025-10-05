FROM maven:3.9.1-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/lab02-0.0.1-SNAPSHOT-jar-with-dependencies.jar app.jar
ENV HZ_ADDRESSES=hz1:5701,hz2:5701,hz3:5701

CMD ["java", "-jar", "/app/app.jar"]
