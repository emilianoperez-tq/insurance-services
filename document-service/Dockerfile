FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Copiar POM parent
COPY pom.xml ./pom.xml

# Copiar módulo específico
COPY document-service/pom.xml ./document-service/pom.xml
COPY document-service/src ./document-service/src

# Maven wrapper
COPY document-service/mvnw ./document-service/mvnw
COPY document-service/.mvn ./document-service/.mvn

WORKDIR /workspace/document-service

# Construir
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/document-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
