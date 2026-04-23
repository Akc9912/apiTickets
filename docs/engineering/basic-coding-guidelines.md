# Guía Básica de Colaboración y Convenciones (Naming & Rules)

Esta guía aplica a proyectos Java 21+, Spring Boot, MySQL 8+, organizados como Modular Monolith, Package by Feature y Vertical Slices.
Para mantener una base de código cohesionada y facilitar la colaboración entre equipos, seguiremos estas reglas básicas de estilo, nombres y arquitectura.

---

## 1. Convenciones de Nombramiento (Naming Conventions)

### 1.1 Clases y Archivos

- **Clases e Interfaces:** `PascalCase`. Ej: `ServiceOffering`, `ProfessionalApi`, `AppointmentController`.
- **Sufijos por responsabilidad:**
  - Interfaces de API Pública (Contrato entre módulos): `{Feature}Api` (ej: `ProfessionalApi`).
  - Controladores REST: `{Entity}Controller` (ej: `ServiceOfferingController`).
  - Servicios (Lógica): `{Feature}ServiceImpl` para la implementación (ej: `ProfessionalServiceImpl`).
  - Entidades JPA: `{Entity}` (ej: `Professional`).
  - Repositorios: `{Entity}Repository` (ej: `ProfessionalRepository`).
  - DTOs: `{Accion}{Entity}Request` o `{Entity}Response` (ej: `CreateServiceRequest`, `ServiceResponse`).

### 1.2 Métodos y Variables

- **Métodos y Variables:** `camelCase`. Ej: `getProfile()`, `professionalId`, `supabaseAuthId`.
- **Nombres descriptivos:** Un método debe indicar su acción claramente. Usa verbos como `create`, `find`, `update`, `delete`, `calculate`, `register`. Evita nombres vagos como `processData()`.

#### Convenciones para Métodos

- **Métodos principales (públicos en servicios, controladores, apis):**
  - Usar verbos claros y específicos: `createUser`, `updateTicketStatus`, `findAllByUserId`, `deleteNotification`.
  - Para operaciones de consulta, usar prefijos como `find`, `get`, `list`, `exists`, `count`.
  - Para comandos, usar `create`, `update`, `delete`, `assign`, `register`, `change`.
- **Helpers y utilidades (privados o package-private):**
  - Prefijo `_` no se usa en Java, pero sí se recomienda nombres que indiquen propósito: `buildJwtToken`, `mapToDto`, `validateEmailFormat`, `parseDate`.
  - Si el método es solo para uso interno, documentar con comentario `// helper` o `// internal`.
- **Evitar:**
  - Nombres genéricos como `handle`, `process`, `doWork`, `execute` salvo que sean parte de un patrón claro.
  - Abreviaturas poco claras.

### 1.3 Endpoints REST (Controllers)

- **Rutas base:** En plural y en minúsculas. Ej: `/api/professionals`, `/api/services`.
- **Identificadores:** Se pasan por `@PathVariable`. Ej: `GET /api/services/{id}`.
- **Acciones:** Evitar verbos en las rutas, usar métodos HTTP correspondientes (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`). Si es una acción no estándar, usar un sub-recurso. Ej: `POST /api/appointments/{id}/cancel`.

### 1.4 Paquetes

- Nombres de paquetes siempre en **minúsculas**, en singular, sin guiones bajos ni separaciones, si es posible. Ej: `com.turnos.modules.serviceoffering` (en lugar de `service_offering`).

---

## 2. Reglas de Implementación

### 2.1 Inyección de Dependencias

- **Usar Constructor Injection:** Evitar el uso de `@Autowired` en propiedades. Usar la etiqueta `@RequiredArgsConstructor` de Lombok combinada con atributos `private final`.

```java
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentApi {
    private final AppointmentRepository repository; // Inyectado por constructor automáticamente
}
```

### 2.2 IDs y Tipos de Datos

- Usar **`UUID`** como identificador primario para evitar exponer IDs secuenciales en las APIs.
- Usar **`BigDecimal`** para manejar dinero y precios (nunca usar `Double` o `Float`).
- Usar **`LocalDateTime`** o `Instant` para fechas y horas.

### 2.3 Excepciones y Validaciones

- **Validación Temprana:** Usar anotaciones de Jakarta Validation (`@NotNull`, `@NotBlank`, `@Min`, etc.) en los DTOs de entrada (`Request`). No validar formato en el `Service`.
- **Excepciones de Negocio:** Lanzar excepciones propias que hereden de `DomainException` (ej: `ResourceNotFoundException`, `InvalidAppointmentStateException`).
- **ExceptionHandler:** Las excepciones lanzadas son capturadas automáticamente por `GlobalExceptionHandler` devolviendo un `ApiErrorResponse`. No hagas bloques `try-catch` para retornar un `ResponseEntity` manual en el controlador si puedes evitarlo.

### 2.4 Controladores y Responsabilidad

- El controlador **SOLO** debe recibir la petición (`Request DTO`), llamar a la interfaz de API del módulo y devolver la respuesta (`Response DTO`). **Nunca poner lógica de negocio en el controlador.**

---

## 3. Prácticas de Colaboración (Git & PRs)

### 3.1 Commits

- Usar _Conventional Commits_: `feat: add schedule module`, `fix: correct typo in professional entity`, `refactor: simplify exception handler`, `docs: update readme`.
- Mensajes en inglés o español, pero consistentes.

### 3.2 Pull Requests (PRs)

- Mantenlos **pequeños y enfocados**. Un PR = una feature o un bugfix.
- Verifica que el código compile (`mvn clean compile`) y que los tests pasen (`mvn test`) antes de abrir un PR.
- Siempre agrega pruebas unitarias (Service) o de integración (Controller) a la lógica nueva que implementes. No aprobar PRs de features críticas (ej. cobros, reservas de turnos) sin cobertura.
