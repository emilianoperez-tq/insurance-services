FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Copiar POM parent
COPY pom.xml ./pom.xml

# ⚠️ AGREGAR ESTO: Copiar módulo common
COPY common/pom.xml ./common/pom.xml
COPY common/src ./common/src

# Copiar módulo específico
COPY auth-service/pom.xml ./auth-service/pom.xml
COPY auth-service/src ./auth-service/src

# Maven wrapper
COPY auth-service/mvnw ./auth-service/mvnw
COPY auth-service/.mvn ./auth-service/.mvn

WORKDIR /workspace/auth-service

# Construir
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/auth-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
