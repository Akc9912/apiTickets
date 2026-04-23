# Backend Architecture (Java/Spring Boot)

## Propósito

Este documento define la arquitectura backend oficial para sistemas Java usando Spring Boot, basada en Modular Monolith, organización Package by Feature y Vertical Slices.

El objetivo es construir sistemas:

- mantenibles
- escalables
- evolutivos
- desacoplados
- testeables

## Alcance Del Documento

Esta arquitectura es la base para todos los sistemas Java/Spring Boot.

Las decisiones de implementación específicas por tecnología se documentan en:

- [java-backend-architecture-adaptation.md](java-backend-architecture-adaptation.md)

La arquitectura seleccionada es:

> Modular Monolith con organización Package by Feature y Vertical Slices.

---

# Principios Arquitectónicos

## Modularidad y Feature-Driven

El sistema se organiza en módulos de dominio (features), cada uno autocontenido y alineado a una capacidad de negocio independiente.

Ejemplos:

- tickets
- usuarios
- notificaciones
- estadisticas

Cada módulo contiene su propia estructura interna, siguiendo la convención Java/Spring Boot:

```text
src/main/java/com/empresa/
	modulo_xxx/
		api/         # Contrato público (Facade, DTOs)
		controller/  # Endpoints REST
		service/     # Lógica de negocio
		repository/  # Acceso a datos (Spring Data JPA)
		entity/      # Modelos JPA
		enum/        # Enumeraciones
```

---

## Arquitectura por Capas Adaptada (Java)

Cada módulo contiene las siguientes capas, adaptadas a convenciones Spring Boot:

- **api/**: Contrato público del módulo (Facade, DTOs)
- **controller/**: Presentación (REST Controllers)
- **service/**: Lógica de negocio y orquestación
- **repository/**: Acceso a datos (Spring Data JPA)
- **entity/**: Modelos de dominio (JPA Entities)
- **enum/**: Enumeraciones específicas

La separación de responsabilidades es obligatoria. No se permite lógica de negocio en controllers ni acceso directo a repositorios fuera de service.

---

# Estructura Base de un Módulo (Java/Spring Boot)

```text
modulo_xxx/
  api/
    XxxFacade.java
    dto/
  controller/
  service/
  repository/
  entity/
  enum/
```

---

# Responsabilidades por Capa (Java/Spring Boot)

## api/

- Contrato público del módulo (Facade, DTOs)
- Punto de acceso para otros módulos

## controller/

- Recibir requests HTTP
- Validar input
- Devolver responses
- Prohibido: lógica de negocio y acceso directo a repositorios

## service/

- Orquestar casos de uso
- Manejar transacciones (`@Transactional`)
- Coordinar repositorios
- Aplicar reglas de negocio
- Emitir eventos de dominio si corresponde

## repository/

- Acceso a base de datos (Spring Data JPA)
- Queries y mapping ORM

## entity/

- Entidades JPA
- Modelos de dominio

## enum/

- Enumeraciones específicas del módulo

# Flujo de Request (Java/Spring Boot)

1. Controller recibe request
2. Valida DTO
3. Llama a Service del módulo
4. Service ejecuta lógica de negocio y casos de uso
5. Interactúa con repositorios
6. Aplica reglas de dominio
7. Emite eventos si corresponde
8. Retorna resultado al controller
9. Controller construye response

---

# Comunicación entre Módulos (Java/Spring Boot)

Permitido:

- Llamadas vía interfaces públicas en `api/` de cada módulo
- Eventos de dominio

Prohibido:

- Acceso directo a service, entity o repository de otro módulo
- Dependencia de infraestructura externa

---

# Manejo de Transacciones

Las transacciones se definen en:

- Service Layer (`@Transactional` en clases/métodos de service)
  Nunca en controllers o repositorios.

---

# Estrategia de Eventos

Cuando el dominio crece:

- Introducir Domain Events (Java events, ApplicationEventPublisher)
- Luego Integration Events
- Luego Outbox Pattern

Objetivo:

- Desacoplar módulos
- Permitir evolución futura

---

# Evolución a Arquitectura Asíncrona

Se introduce cuando:

- Procesos largos
- Integraciones externas lentas
- Necesidad de resiliencia

Se agregan:

- Colas (RabbitMQ, etc.)
- Workers (Spring @Async, schedulers)
- Retry policies

---

# Evolución a Microservicios

Ocurre cuando:

- Bounded contexts claros
- Equipos independientes
- Necesidad de deploy separado

Proceso:

1. Identificar módulo candidato
2. Extraer base de datos
3. Exponer API
4. Introducir API Gateway

---

# Regla de Adaptación por Lenguaje o Framework

Esta adaptación Java/Spring Boot extiende la arquitectura base, alineando las capas y convenciones a las mejores prácticas del ecosistema Java.

---

presentation → application → domain
domain → infrastructure  
domain → persistence  
presentation → persistence

# Reglas de Dependencia (Java/Spring Boot)

Permitido:
controller → service → entity/repository/enum/api
service → repository/entity/enum/api

Prohibido:
entity → repository/service
controller → repository/entity
acceso cruzado entre módulos fuera de api/

---

# Testing Strategy (Java/Spring Boot)

entity/service: unit tests obligatorios
controller: integration tests (MockMvc)
repository: DataJpaTest para queries custom
infraestructura: tests de integración externa

---

# Objetivo Final

El sistema debe poder evolucionar:

Monolith → Event Driven → Microservices

Sin reescrituras masivas.

La arquitectura debe facilitar esta transición desde el inicio, con convenciones alineadas a Java/Spring Boot y modularidad real por feature.
