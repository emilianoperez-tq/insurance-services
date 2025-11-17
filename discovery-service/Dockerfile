FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Copiar POM parent
COPY pom.xml ./pom.xml

# Copiar módulo específico
COPY discovery-service/pom.xml ./discovery-service/pom.xml
COPY discovery-service/src ./discovery-service/src

# Maven wrapper
COPY discovery-service/mvnw ./discovery-service/mvnw
COPY discovery-service/.mvn ./discovery-service/.mvn

WORKDIR /workspace/discovery-service

# Construir
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/discovery-service/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
