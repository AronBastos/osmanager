# Estágio 1: build (Maven + JDK 17)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# Estágio 2: runtime enxuto (JRE 17), usuário não-root
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --uid 1001 osmanager
COPY --from=build /app/target/osmanager-*.jar app.jar
USER osmanager
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
