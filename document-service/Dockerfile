FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Instalar Maven
RUN apk add --no-cache maven

# Copiar todo el proyecto
COPY . .

# Compilar desde el pom padre
RUN mvn clean install -DskipTests

# Imagen final
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=build /workspace/document-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
