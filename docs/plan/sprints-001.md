# Sprints y Backlog Inicial (ApiTickets)

## Sprint 1: Estructura y Configuración Base

- Crear estructura de carpetas bajo `src/main/java/com/empresa/`:
  - `modules/` (auth, users, ticketing)
  - `shared/`, `security/`, `config/`
- Configurar `pom.xml` con dependencias mínimas (Spring Boot, JPA, Security, MySQL, Lombok, Swagger)
- Configurar `application.properties` para MySQL y perfiles
- Eliminar código/configuración legacy de notificaciones/eventos
- Inicializar repositorio y pipeline de build/test

## Sprint 2: Módulo Auth

- Definir API pública en `modules/auth/api/AuthApi.java` y DTOs
- Implementar endpoints en `modules/auth/controller/AuthController.java`
- Implementar lógica de autenticación y JWT en `modules/auth/service/AuthServiceImpl.java`
- Configurar seguridad en `security/SecurityConfig.java`
- Crear entidad `User` y repositorio en `modules/auth/entity/` y `modules/auth/repository/`
- Pruebas unitarias y de integración para Auth

## Sprint 3: Módulo Users y Modelado de Roles

- Definir API pública en `modules/users/api/UsersApi.java` y DTOs
- Implementar endpoints en `modules/users/controller/UsersController.java`
- Implementar lógica de negocio en `modules/users/service/UsersServiceImpl.java`
- Crear entidad `UserProfile` con campo `UserRole` (enum) en `modules/users/entity/`
- Validar roles en los services, no con herencia
- Crear repositorio en `modules/users/repository/`
- Pruebas unitarias y de integración para Users

## Sprint 4: Módulo Ticketing

- Definir API pública en `modules/ticketing/api/TicketingApi.java` y DTOs
- Implementar endpoints en `modules/ticketing/controller/TicketingController.java`
- Implementar lógica de negocio en `modules/ticketing/service/TicketingServiceImpl.java`
- Crear entidades `Ticket`, `TicketState` en `modules/ticketing/entity/`
- Crear repositorio en `modules/ticketing/repository/`
- Pruebas unitarias y de integración para Ticketing

## Sprint 5: Integración, Documentación y Calidad

- Integrar módulos vía interfaces públicas (`api/`)
- Documentar endpoints con Swagger/OpenAPI
- Validar seguridad y acceso entre módulos solo vía API pública
- Eliminar cualquier referencia a notificaciones/eventos
- Validar DTOs con Jakarta Validation
- Mejorar cobertura de pruebas y CI/CD

---

> Cada sprint puede ajustarse en alcance según la velocidad del equipo y prioridades emergentes. Los entregables de cada sprint deben ser funcionales y probados.
