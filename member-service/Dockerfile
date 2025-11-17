FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Copiar POM parent
COPY pom.xml ./pom.xml

# Copiar módulo específico
COPY member-service/pom.xml ./member-service/pom.xml
COPY member-service/src ./member-service/src

# Maven wrapper
COPY member-service/mvnw ./member-service/mvnw
COPY member-service/.mvn ./member-service/.mvn

WORKDIR /workspace/member-service

# Construir
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/member-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]