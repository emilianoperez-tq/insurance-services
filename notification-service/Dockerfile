FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Copiar POM parent
COPY pom.xml ./pom.xml

# Copiar módulo específico
COPY notification-service/pom.xml ./notification-service/pom.xml
COPY notification-service/src ./notification-service/src

# Maven wrapper
COPY notification-service/mvnw ./notification-service/mvnw
COPY notification-service/.mvn ./notification-service/.mvn

WORKDIR /workspace/notification-service

# Construir
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/notification-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]