FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Copiar POM parent
COPY pom.xml ./pom.xml

# Copiar módulo específico
COPY policy-service/pom.xml ./policy-service/pom.xml
COPY policy-service/src ./policy-service/src

# Maven wrapper
COPY policy-service/mvnw ./policy-service/mvnw
COPY policy-service/.mvn ./policy-service/.mvn

WORKDIR /workspace/policy-service

# Construir
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/policy-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
