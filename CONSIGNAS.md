# Microservices Insurance Application - Step-by-Step Implementation Guide

## Phase 1: Basic Microservices ✅

_Services to Implement_:
### member-service (Port: 8081)
Entities: Member (id, name, email, age)

POST /api/members - Create member
GET /api/members/{id} - Get member
GET /api/members - List all members

Simple Logic: Only stores policy records, no validation.

### claim-service (Port: 8083)
Entities: Claim (id, policyId, amount, status)

POST /api/claims - Create claim
GET /api/claims/{id} - Get claim
PUT /api/claims/{id}/status - Update status

Simple Logic: Basic CRUD, no calculations.
Deliverable: Three independent Spring Boot apps with minimal logic.

## Phase 2: Service Discovery & API Gateway ✅
discovery-service (Port: 8761)
Technology: Netflix Eureka Server

api-gateway (Port: 8080)
Technology: Spring Cloud Gateway

Tasks:
Add Eureka client to all services
All services register automatically
Test accessing services through gateway

Deliverable: Gateway routing to services via Eureka.

## Phase 3: Centralized Configuration ✅
config-service (Port: 8888)
Technology: Spring Cloud Config Server
Git Repository Structure:

config-repo/
├── application.yml          # Shared config
├── member-service.yml
├── claim-service.yml
└── gateway-service.yml

Move These to Config:
- Database URLs
- Server ports
- Eureka URLs
- Simple properties (e.g., app.message=Hello)

## Phase 4: Asynchronous Communication
notification-service
Purpose: Listen to events and act accordingly
Existen varias librerias para lograr esto, RabbitMQ es el más fácil de implementar pero cosas ya más complejas como Kafka son las que más se usa en los proyectos productivos.
Y la idea sería que se creen algún evento de cuando se crea una claim o cuando cambia de estado y que le vaya notificando al usuario por mail o telefono los cambios.

## Phase 5: File Upload (Single Service)
document-service (Port: 8085)
Purpose: Handle file uploads
Entities: Document (id, filename, fileType, uploadDate, filePath)
Endpoints:

POST /api/documents/upload - Upload file (MultipartFile)
GET /api/documents/{id}/download - Download file
GET /api/documents - List all documents

Integration: claim-service calls document-service to attach files to claims

## Phase 6: Distributed Tracing
Zipkin Setup (Port: 9411)

Tasks:
Add Spring Cloud Sleuth to all services
Configure Zipkin URL
Make a request through gateway → member-service → policy-service
View trace in Zipkin UI

## Phase 7: Centralized Security ✅
security-service
Technology: Spring Security + JWT

Simple Implementation:
Single endpoint: POST /auth/login (username/password)
Returns JWT token
Hardcoded users: admin/admin, user/user
JWT Token Contains: username, role (ADMIN/USER)

Update API Gateway
Add JWT validation filter:
Extract token from Authorization header
Validate token
Forward request with user info in headers

## Phase 8: API Documentation
Technology: SpringDoc OpenAPI

Tasks:
Add dependency to all services
Add annotations to controllers
Configure in gateway to aggregate all service docs

Include:
Request/Response examples
Error codes
Authentication requirements

## Phase 9: Dockerization ✅

Task:
Create docker-compose.yml

## Phase 10: Health Checks & Resilience ✅

Tasks:
Add  Spring Boot Actuator to all services
Health Checks: Database connectivity, RabbitMQ/Kafka connectivity, Disk space
Access: http://localhost:assignedPort/actuator/health

## Phase 11: Circuit Breaker (Resilience4j)

Tasks:
Add to policy-service when calling member-service:

Example:
```java
@CircuitBreaker(name = "memberService", fallbackMethod = "memberFallback")
public Member getMember(String memberId) {
return memberServiceClient.getMember(memberId);
}

public Member memberFallback(String memberId, Exception e) {
return new Member(memberId, "Unknown", "unavailable@email.com", 0);
}
```
