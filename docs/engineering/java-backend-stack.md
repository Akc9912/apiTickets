# Java Backend Stack

## Propósito

Este documento define el stack tecnológico backend oficial para proyectos Java 21+, Spring Boot, MySQL 8+, organizados como Modular Monolith, Package by Feature y Vertical Slices. Las tecnologías se introducen progresivamente según presión arquitectónica real.

El objetivo no es utilizar todas las tecnologías desde el inicio, sino establecer un stack completo disponible cuya adopción se realiza de manera evolutiva.

---

# Stack Base Obligatorio (Nivel 0 — Foundation)

Estas tecnologías son obligatorias en todos los proyectos backend.

## Lenguaje y Runtime

- Java 21+

### Justificación

- estabilidad
- madurez del ecosistema
- excelente performance
- soporte enterprise
- compatibilidad a largo plazo

---

## Framework Principal

- Spring Boot

### Responsabilidades

- construcción de APIs REST
- inyección de dependencias
- configuración del sistema
- lifecycle management
- integración con módulos

---

## Arquitectura

- Modular Monolith
- Vertical Slices (Feature-Driven)
- Package by Feature

---

## Persistencia Principal

- MySQL 8.0+

### Implementación

- MySQL local o gestionado (desarrollo y producción)
- Supabase solo si se requiere prototipado rápido (opcional)

---

## ORM

- Spring Data JPA
- Hibernate

---

## Autenticación

- Supabase Auth (JWT)

### Evolución futura

- Spring Security + Identity Provider externo

---

## Validación

- Jakarta Validation
- Zod (lado frontend como contrato compartido)

---

## Build Tool

- Maven

---

## Contenerización

- Docker

### Objetivo

- reproducibilidad
- entornos consistentes
- simplificación de despliegues

---

# Stack Nivel 1 — Presión de Performance

Se introduce cuando aparecen:

- endpoints lentos
- consultas repetitivas
- aumento de tráfico
- problemas de latencia

## Cache

- Redis

### Usos permitidos

- rate limiting
- caching de consultas
- sesiones temporales
- almacenamiento efímero

---

## Rate Limiting

- Bucket4j o Redis-based limiter

---

## Optimización de Persistencia

- índices avanzados
- query tuning
- pagination obligatoria
- projections
- native queries estratégicas

---

# Stack Nivel 2 — Presión de Escalabilidad

Se introduce cuando:

- jobs largos bloquean requests
- procesamiento en background necesario
- integración con sistemas externos
- aumento significativo de usuarios

## Async Processing

- Spring Async
- CompletableFuture
- Scheduled Jobs

---

## Messaging

- RabbitMQ

### Usos

- colas de procesamiento
- retry mechanisms
- desacoplamiento interno

---

# Stack Nivel 3 — Presión de Complejidad de Dominio

Se introduce cuando:

- múltiples módulos interactúan fuertemente
- lógica distribuida
- necesidad de trazabilidad de acciones

## Event Driven Architecture

- Domain Events
- Integration Events
- Outbox Pattern

---

## Event Streaming (opcional)

- Apache Kafka

### Solo cuando existe:

- alto throughput
- múltiples consumidores
- analytics en tiempo real

---

# Stack Nivel 4 — Presión Organizacional

Se introduce cuando:

- múltiples equipos trabajan en paralelo
- releases independientes necesarias
- bounded contexts claramente definidos

## Microservices

- Spring Boot Services independientes
- API Gateway
- Service Discovery

---

## API Gateway

- Spring Cloud Gateway

---

## Config Server

- Spring Cloud Config

---

# Stack Nivel 5 — Presión Operacional

Se introduce cuando:

- debugging complejo
- fallas en producción difíciles de rastrear
- necesidad de SLA reales

## Observabilidad

- Micrometer
- Prometheus
- Grafana

---

## Logging

- Structured Logging
- ELK Stack

---

## Distributed Tracing

- OpenTelemetry
- Jaeger / Zipkin

---

# Stack Nivel 6 — Presión de Seguridad

Se introduce cuando:

- producto monetizado
- datos sensibles
- compliance requerido

## Seguridad Avanzada

- OAuth2 Provider
- Keycloak / Auth0
- Secrets Manager
- Rate limit avanzado
- Audit Logs

---

# Stack Nivel 7 — Presión de Escala Global

Se introduce cuando:

- tráfico internacional
- alta disponibilidad requerida

## Infraestructura Evolutiva

- Kubernetes
- Horizontal Pod Autoscaling
- Multi-region DB
- CDN
- Read Replicas

---

# Reglas de Introducción Tecnológica

Una tecnología se introduce solo si:

- existe una métrica que justifique su adopción
- existe un problema técnico observable
- existe impacto real en negocio o producto

---

# Regla de Oro

El stack completo está definido desde el inicio.

Pero su implementación es progresiva.

No se construyen sistemas complejos.
Se permiten que evolucionen hacia la complejidad necesaria.
