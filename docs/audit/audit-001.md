# Auditoría Rápida del Sistema - ApiTickets

## 1. Estructura General del Proyecto

```
. (raíz)
├── .agents/
├── .github/
├── .mvn/
├── .vscode/
├── docs/
├── logs/
├── scripts/
├── src/
│   └── main/
│       └── java/com/poo/miapi/
│           ├── config/
│           ├── controller/
│           ├── dto/
│           ├── exception/
│           ├── model/
│           ├── modules/
│           ├── repository/
│           ├── security/
│           ├── service/
│           ├── shared/
│           └── util/
│       └── resources/
│           └── application.properties
│   └── test/
│       └── java/com/poo/miapi/
│       └── resources/
├── target/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── README.md
├── AGENTS.md
└── ...otros archivos
```

## 2. Stack Tecnológico Detectado

- **Lenguaje principal:** Java 24+
- **Framework:** Spring Boot 3.5.3
- **ORM:** Spring Data JPA (Hibernate)
- **Seguridad:** Spring Security 6.5.1, JWT (io.jsonwebtoken)
- **Base de datos:** MySQL 8.0+ (driver: mysql-connector-j)
- **Build:** Maven 3.9+
- **Documentación:** Swagger/OpenAPI (springdoc-openapi)
- **Validación:** Hibernate Validator
- **Dotenv:** java-dotenv
- **Actuator:** Spring Boot Actuator
- **Contenedores:** Docker, Docker Compose

## 3. Configuración Principal (application.properties)

- Variables sensibles y de entorno gestionadas vía `.env` y placeholders en `application.properties`.
- Configuración de logs, perfiles, JWT, uploads, y endpoints de documentación.
- Seguridad reforzada: H2 deshabilitado, logs separados, perfiles activos.

## 4. Características Funcionales

- Gestión de tickets con roles jerárquicos (SuperAdmin, Admin, Técnico, Trabajador)
- Autenticación JWT segura
- Auditoría y logs de acciones
- Notificaciones automáticas
- Estadísticas en tiempo real
- API REST documentada y probada con Swagger

## 5. Arquitectura y Organización

- Arquitectura en capas (controller, service, repository, model, dto, config, security)
- Separación clara de responsabilidades
- Uso de DTOs para entrada/salida
- Configuración centralizada y desacoplada
- Soporte para pruebas unitarias y de integración

## 6. Observaciones

- El sistema está preparado para despliegue en Docker y orquestación futura (Kubernetes).
- Buenas prácticas de seguridad y manejo de secretos.
- Documentación técnica y de endpoints disponible y actualizada.

---

_Archivo generado automáticamente por QuickAudit el 2026-04-23._
