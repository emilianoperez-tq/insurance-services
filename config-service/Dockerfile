FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Copiar POM parent
COPY pom.xml ./pom.xml

# Copiar módulo específico
COPY config-service/pom.xml ./config-service/pom.xml
COPY config-service/src ./config-service/src

# Maven wrapper
COPY config-service/mvnw ./config-service/mvnw
COPY config-service/.mvn ./config-service/.mvn

WORKDIR /workspace/config-service

# Construir
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/config-service/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
