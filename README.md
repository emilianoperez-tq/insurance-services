# Arquitectura de Microservicios - Roadmap

## Fase 1: Microservicios Básicos ✅

### member-service (Puerto: 8081)
**Entidades:** Member (id, name, email, age)

- POST /api/members → Crear miembro
- GET /api/members/{id} → Obtener miembro
- GET /api/members → Listar todos los miembros

**Lógica simple:** Solo almacena registros de pólizas, sin validación.

---

### policy-service (Puerto: 8082)
**Entidades:** Policy (id, memberId, type, premium)

- POST /api/policies → Crear póliza
- GET /api/policies/{id} → Obtener póliza
- GET /api/policies/member/{memberId} → Listar pólizas por miembro

**Lógica simple:** CRUD básico, sin cálculos.

---

### claim-service (Puerto: 8083)
**Entidades:** Claim (id, policyId, amount, status)

- POST /api/claims → Crear reclamo
- GET /api/claims/{id} → Obtener reclamo
- PUT /api/claims/{id}/status → Actualizar estado

**Lógica simple:** CRUD básico, sin cálculos.  
**Entregable:** Tres aplicaciones independientes de Spring Boot con lógica mínima.

---

## Fase 2: Descubrimiento de Servicios y API Gateway ✅

### discovery-service (Puerto: 8761)
**Tecnología:** Netflix Eureka Server

### api-gateway (Puerto: 8080)
**Tecnología:** Spring Cloud Gateway

**Tareas:**
- Agregar cliente Eureka a todos los servicios
- Todos los servicios se registran automáticamente
- Probar acceso a servicios a través del gateway

**Entregable:** Gateway enruta a los servicios vía Eureka.

---

## Fase 3: Configuración Centralizada ✅

### config-service (Puerto: 8888)
**Tecnología:** Spring Cloud Config Server

**Estructura del repositorio Git:**

```text
config-repo/
├── application.yml          # Shared config
├── member-service.yml
├── claim-service.yml
└── gateway-service.yml
```

**Mover a Config:**
- URLs de base de datos
- Puertos de servidor
- URLs de Eureka
- Propiedades simples (ej: app.message=Hello)

**Resolución:**
- Se creó un repositorio en Github del cuál obtiene la configuración centralizada el config-server
- Se agregó la dependencia de Spring Cloud Config Server en el `pom.xml` del config-service
- Se configuró el `application.yml` del config-service para que apunte al repositorio

---

## Fase 4: Comunicación Asíncrona

### notification-service ✅
**Propósito:** Escuchar eventos y actuar en consecuencia.

RabbitMQ es el más fácil de implementar, pero en proyectos productivos se usa más Kafka.  
La idea es crear eventos cuando se genera un reclamo o cambia de estado, notificando al usuario por correo o teléfono.

**Resolución:**
- Se creó el servicio notification-service que se encarga de escuchar los eventos de creación y actualización de reclamos
- Se agregó la dependencia de Spring for RabbitMQ en el `pom.xml` del notification-service
- Se configuró RabbitMQ en el `application.yml` del notification-service
- Se creó un listener que escucha los eventos de creación y actualización de reclamos

---

## Fase 5: Subida de Archivos (Servicio Único)

### document-service (Puerto: 8085)
**Propósito:** Manejar subida de archivos

**Entidades:** Document (id, filename, fileType, uploadDate, filePath)

**Endpoints:**
- POST /api/documents/upload → Subir archivo (MultipartFile)
- GET /api/documents/{id}/download → Descargar archivo
- GET /api/documents → Listar todos los documentos

**Integración:** claim-service llama a document-service para adjuntar archivos a reclamos.

---

## Fase 6: Trazabilidad Distribuida

### Zipkin Setup (Puerto: 9411)

**Tareas:**
- Agregar Spring Cloud Sleuth a todos los servicios
- Configurar URL de Zipkin
- Hacer una petición vía gateway → member-service → policy-service
- Ver trazas en la interfaz de Zipkin

---

## Fase 7: Seguridad Centralizada ✅

### Security-service
**Tecnología:** Spring Security + JWT

**Implementación simple:**
- Endpoint único: POST /auth/login (username/password)
- Devuelve token JWT
- Usuarios hardcodeados: admin/admin, user/user
- Token JWT contiene: username, role (ADMIN/USER)

**Actualizar API Gateway:**
- Agregar filtro de validación JWT:
    - Extraer token del header Authorization
    - Validar token
    - Reenviar petición con info de usuario en headers

**Resolucíón**:
- Para agregar autenticación se creó el contenedor para levantar el servicio de Keycloak
  - Básicamente, Keycloak es un servidor de autenticación y autorización que soporta OAuth2 y OpenID Connect
  - Se configuró un realm, un cliente y dos usuarios (admin y user) con roles correspondientes
    - Es necesario quitar cualquier acción pendiente de los usuarios para que se genere el token JWT correctamente
- Luego se configuró el API Gateway para que valide los tokens JWT emitidos por Keycloak
- Se agregaron las dependencias necesarias y se configuró el filtro de seguridad para validar los tokens en cada petición
  - Se agregó la dependencia de Spring Security y Spring Security OAuth2 Resource Server dentro del `pom.xml` del API Gateway
  - Se configuró el `application.yml` del API Gateway para que apunte al servidor de Keycloak
    - Es necesario que se apunte al contenedor y al puerto _del contenedor_ y no al puerto expuesto por Docker

---

## Fase 8: Documentación de API

**Tecnología:** SpringDoc OpenAPI

**Tareas:**
- Agregar dependencia a todos los servicios
- Anotar controladores
- Configurar en gateway para agregar documentación de todos los servicios

**Incluir:**
- Ejemplos de Request/Response
- Códigos de error
- Requisitos de autenticación

---

## Fase 9: Dockerización ✅

**Tarea:**  
Crear `docker-compose.yml`

**Resolución:**:
- Se creo un archivo `docker-compose.yml` donde se definen los servicios de la aplicación
- Estos incluyen:
  - Los microservicios
  - La base de datos
  - El servidor de configuración
    - Para este servicio se creó un repositorio aparte donde está la configuración de cada micorservicio
    - Esto para poder centralizar la configuración y que cada servicio pueda obtener su configuración desde este servidor
  - El servicio de descubrimiento (Eureka)
  - El API Gateway

---

## Fase 10: Health Checks y Resiliencia

**Tareas:**
- Agregar Spring Boot Actuator a todos los servicios
- Health Checks: conectividad a base de datos, RabbitMQ/Kafka, espacio en disco
- Acceso: `http://localhost:assignedPort/actuator/health`

---

## Fase 11: Circuit Breaker (Resilience4j)

**Tareas:**  
Agregar a policy-service cuando llame a member-service:

```java
@CircuitBreaker(name = "memberService", fallbackMethod = "memberFallback")
public Member getMember(String memberId) {
    return memberServiceClient.getMember(memberId);
}

public Member memberFallback(String memberId, Exception e) {
    return new Member(memberId, "Desconocido", "unavailable@email.com", 0);
}
```