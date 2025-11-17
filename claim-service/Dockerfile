FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Copiar POM parent
COPY pom.xml ./pom.xml

# Copiar módulo específico
COPY claim-service/pom.xml ./claim-service/pom.xml
COPY claim-service/src ./claim-service/src

# Maven wrapper
COPY claim-service/mvnw ./claim-service/mvnw
COPY claim-service/.mvn ./claim-service/.mvn

WORKDIR /workspace/claim-service

# Construir
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/claim-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
