# Plan de Implementación: Arquitectura y Stack Java/Spring Boot/MySQL

## Enfoque Recomendado

Se implementará una arquitectura Modular Monolith basada en Package by Feature y Vertical Slices, usando Java 21+, Spring Boot, MySQL 8+, y las convenciones definidas en docs/engineering. Los módulos iniciales serán auth, users y ticketing. Todo lo relacionado a notificaciones/eventos queda fuera del alcance y debe eliminarse si existe.

## Fases y Roadmap

### Fase 1: Estructura Base y Configuración

- Crear estructura de carpetas bajo `src/main/java/com/empresa/`:
  - `modules/` (contendrá los módulos: `auth`, `users`, `ticketing`)
  - `shared/` (utilidades y clases comunes)
  - `security/` (configuración y utilidades de seguridad)
  - `config/` (configuración general del proyecto)
    Dentro de `modules/`, crear carpetas por feature: `auth`, `users`, `ticketing`.
- Configurar `pom.xml` con dependencias mínimas: Spring Boot, Spring Data JPA, Spring Security, MySQL, Lombok, Swagger.
- Configurar `application.properties` para MySQL y perfiles.
- Eliminar/ignorar cualquier código, paquete o configuración de notificaciones/eventos.

### Fase 2: Módulo Auth

- Definir API pública en `auth/api/AuthApi.java` y DTOs.
- Implementar endpoints en `auth/controller/AuthController.java`.
- Implementar lógica de autenticación y generación de JWT en `auth/service/AuthServiceImpl.java`.
- Configurar seguridad en `auth/config/SecurityConfig.java`.
- Crear entidades necesarias (`User`, etc.) en `auth/entity/`.
- Crear repositorios en `auth/repository/`.
- Pruebas unitarias y de integración.

### Fase 3: Módulo Users

- Definir API pública en `users/api/UsersApi.java` y DTOs.
- Implementar endpoints en `users/controller/UsersController.java`.
- Implementar lógica de negocio en `users/service/UsersServiceImpl.java`.
- Crear entidades (`UserProfile`, etc.) en `users/entity/`.
- Crear repositorios en `users/repository/`.
- Pruebas unitarias y de integración.

### Fase 4: Módulo Ticketing

- Definir API pública en `ticketing/api/TicketingApi.java` y DTOs.
- Implementar endpoints en `ticketing/controller/TicketingController.java`.
- Implementar lógica de negocio en `ticketing/service/TicketingServiceImpl.java`.
- Crear entidades (`Ticket`, `TicketState`, etc.) en `ticketing/entity/`.
- Crear repositorios en `ticketing/repository/`.
- Pruebas unitarias y de integración.

### Fase 5: Integración y Documentación

- Integrar los módulos vía interfaces públicas (`api/`).
- Documentar endpoints con Swagger/OpenAPI.
- Validar seguridad y acceso entre módulos solo vía API pública.
- Eliminar cualquier referencia a notificaciones/eventos.

## Ejemplo de Estructura de Carpetas

```
src/main/java/com/empresa/
  modules/
    auth/
      api/
      controller/
      service/
      repository/
      entity/
      dto/
    users/
      api/
      controller/
      service/
      repository/
      entity/
      dto/
    ticketing/
      api/
      controller/
      service/
      repository/
      entity/
      dto/
  shared/
  security/
  config/
```

## Consideraciones de Integración

- No debe haber dependencias cruzadas directas entre módulos, solo vía interfaces en `api/`.
- No se implementan ni mantienen eventos ni notificaciones en esta versión.
- Validar DTOs con Jakarta Validation.
- Usar pruebas unitarias (service) e integración (controller/repository).

## Riesgos y Mitigaciones

- **Riesgo:** Persistencia de código legacy de notificaciones/eventos.
  - **Mitigación:** Eliminar completamente cualquier referencia, clase o configuración relacionada.
- **Riesgo:** Acoplamiento entre módulos.
  - **Mitigación:** Forzar comunicación solo vía interfaces públicas.

## Criterios de Éxito

- El sistema compila y pasa los tests.
- Los módulos auth, users y ticketing funcionan de forma independiente y desacoplada.
- No existen referencias a notificaciones/eventos en el código ni configuración.
- La documentación refleja la arquitectura y stack definidos.

### Nota sobre Modelado de Usuarios y Roles

- Se elimina el uso de herencia en la jerarquía de usuarios.
- Se implementa un modelo único de usuario (`User`/`UserProfile`) con un campo `UserRole` (enum) para distinguir los roles.
- La lógica de validación y autorización por rol se realiza en los `service`, no mediante herencia ni subclases.
- Esto permite flexibilidad y evita duplicación de lógica entre roles que comparten comportamiento.
- Los roles concretos a utilizar se definirán más adelante, pero el sistema debe estar preparado para soportar múltiples roles en el enum `UserRole`.
