# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### 📚 En Desarrollo

- **⚠️ El build está roto.** `mvn compile` falla con 45 errores, **todos en `module/ticket`**,
  que sigue referenciando las subclases de usuario eliminadas (`Admin`, `Developer`, `Support`),
  los DTOs viejos y los roles `DEVELOPER`/`SUPPORT`. `module/users`, `module/auth` y `shared/`
  compilan limpios. `module/ticket` quedó explícitamente fuera de alcance por ahora.
- **⚠️ La aplicación no arranca sin `JWT_SECRET`** de 32+ caracteres: `JwtService` valida el
  largo del secreto al construir el bean, en vez de fallar en el primer login.
- **⚠️ Sin prueba end-to-end.** Con el build roto la app no levanta, así que `auth` está
  verificado con arneses aislados (130 checks: persistencia de tokens, reglas de rol, rutas,
  esquema contra MySQL real), no con el flujo corriendo contra la aplicación.
- **⚠️ Sin rate limiting.** `forgot-password` y `resend-code` son públicos y disparan envío de
  mails. Bloqueante para producción; los límites ya están decididos en `1.2.md`.

### ✨ Added

- **🔐 Módulo `auth` reconstruido** — misma estructura que `users` (`api/AuthApi` como contrato,
  DTOs en `api/dto/request|response`), con 9 endpoints bajo `/api/auth/v1`:
  - `register` (202, sin login automático) · `verify-email` · `resend-code` · `login` ·
    `refresh` · `forgot-password` · `reset-password` · `logout` 🔒 · `change-password` 🔒
  - **Access + refresh tokens emitidos por el backend.** Access: JWT HS256 de 15 min, sin
    persistir. Refresh: opaco de 256 bits, 30 días, en DB sólo su SHA-256, con **rotación en
    cada uso**. Reusar un refresh ya rotado **revoca todas las sesiones del usuario**
  - Verificación de cuenta con código de 6 dígitos, 15 min, máximo 3 intentos tras los cuales el
    código se quema. Recuperación de contraseña con token de 24 h
  - `forgot-password` y `resend-code` responden 202 exista o no la cuenta, y el login devuelve el
    mismo error para email inexistente y contraseña incorrecta: el módulo no se puede usar para
    descubrir qué emails están registrados
  - Entidades `AuthToken` y `RefreshToken` mapeando las tablas que el esquema ya tenía
  - `EmailSender` como puerto, con una implementación de desarrollo que escribe el código en el
    log. **No usar en producción**
  - `SecurityAuditLog` con los 5 eventos definidos en `1.3.md`, sin tokens ni secretos

- **🪪 `UserPrincipal`** en `module/auth/security`: implementa `UserDetails` envolviendo un
  `User`, para no acoplar la entidad JPA a Spring Security. Emite el authority como
  `ROLE_ + globalRole`. Cierra la decisión que bloqueaba los endpoints de `users`

- **🚪 Contrato de entrada al módulo users**
  - Nueva interfaz `module/users/api/UserApi` con los 15 métodos del módulo; `UserService`
    la implementa
  - `findById`, `findByEmail` y `save` exponen la entidad `User` como excepción documentada
    al contrato en DTOs: `Ticket` la referencia con `@ManyToOne` y `auth` la necesita para
    firmar el JWT
  - Pendiente: los consumidores todavía inyectan `UserService` en lugar de `UserApi`

- **🧑‍💼 Service y endpoints de users completos**
  - `UserService` implementado: alta, lecturas por id/email/rol/estado, búsqueda por
    nombre, update parcial, cambio de estado y baja lógica
  - `UserController` unificado con 9 endpoints: 2 de perfil propio (`/api/user/v1`) y 7 de
    administración (`/api/admin/v1/users`)
  - DTOs `UserResponse` y `UpdateUserRequest` en `module/users/api/dto`

- **🔒 Autorización por roles**
  - `@EnableMethodSecurity` habilitado en `SecurityConfig` — sin esto Spring ignoraba los
    `@PreAuthorize` en silencio y los endpoints de administración quedaban abiertos
  - `@PreAuthorize` por método: `hasAnyRole('ADMIN','SUPERADMIN')` en administración,
    `hasAnyRole('USER','ADMIN','SUPERADMIN')` en perfil propio
  - Regla por URL para `/api/admin/**` en `SecurityConfig` como defensa en profundidad

- **⏱️ Timestamps automáticos en `User`**
  - `@PrePersist`/`@PreUpdate`: `createdAt` y `updatedAt` son `NOT NULL` sin default, así
    que todo insert fallaba por constraint

### � Changed

- **⛔ Supabase Auth descartado.** `architecture.md`, `README.md`, `next_steps.md` y
  `docs/iteracion-01-.../fase-3-*` definían que Supabase emitiera los tokens y que el backend
  "NO genera tokens". Se decidió lo contrario: tokens locales con rotación. Los documentos
  quedaron marcados
- **🗑️ `DataInitializer` eliminado.** Sembraba 4 usuarios con las subclases borradas y una
  contraseña hardcodeada; se decidió no usarlo más
- **⬆️ jjwt `0.11.5` → `0.13.0`**, con la API vigente (`Jwts.parser().verifyWith(...)`,
  `Jwts.SIG.HS256`). La anterior estaba deprecada en bloque
- **🧭 Rutas de auth versionadas**: `/api/auth/...` → `/api/auth/v1/...`, consistente con
  `/api/user/v1/` y `/api/admin/v1/`. Los 3 endpoints anteriores dejan de existir

- **⬆️ Actualización de stack**
  - Spring Boot `3.5.3` → `4.1.1` (última estable; `4.2.0-M1` es milestone)
  - Java `24` → `21`
  - Hibernate ORM `6.6.x` → `7.4.5.Final` y Spring Security `6.5.x` → `7.1.1`, ambos vía
    el parent de Boot
  - springdoc-openapi `2.5.0` → `3.1.1`: la línea 2.x no soporta Spring Framework 7
  - Verificado que el upgrade no introdujo errores de compilación: el set de errores es
    idéntico al de Boot 3.5.3 sobre Java 21

- **🗄️ Repositorio de usuarios**
  - `findByRoleAndDeletedAtIsNull` → `findByGlobalRoleAndDeletedAtIsNull`: la propiedad se
    llama `globalRole`, el nombre viejo tumbaba el arranque de Spring
  - `findByStatus` → `findByStatusAndDeletedAtIsNull` y búsquedas por nombre unificadas en
    `searchActiveByName`: a las tres les faltaba el filtro de baja lógica
  - Búsquedas por id y email devuelven `Optional<User>`
  - Nuevo `existsByEmail`, que deliberadamente **no** filtra `deletedAt`: el `UNIQUE` de
    email aplica a la tabla completa, así que un usuario dado de baja sigue ocupando su
    email

- **🧭 Rutas de usuarios**
  - Eliminado el árbol `/api/superadmin/v1/users`: un solo árbol `/api/admin/v1/users`
    cubre ADMIN y SUPERADMIN
  - `/api/user/v1/profile` → `/api/user/v1/view-profile` y `/api/user/v1/update-profile`
  - Reemplazados `toggle-active`/`toggle-blocked`/`{id}/role` por
    `PUT /users/{id}/status/{status}`

### �🐞 Fixed

- **🚨 Cambio de contraseña sin autenticar (crítico).** `POST /api/auth/change-password` recibía
  el `userId` en el body, el controller ignoraba el `@AuthenticationPrincipal` que recibía, y
  `SecurityConfig` dejaba `/api/auth/**` entero en `permitAll`. Sumado, **cualquiera sin
  autenticarse podía cambiar la contraseña de cualquier usuario**. No era explotable porque el
  proyecto no compila. Ahora el usuario sale del token, se exige la contraseña actual, y la lista
  de rutas públicas vive en `shared/security/PublicEndpoints` con coincidencia exacta, compartida
  entre `SecurityConfig` y `JwtAuthenticationFilter` para que no haya dos copias que se
  desincronicen
- **🚨 `resetPassword` fijaba la contraseña al id del usuario** (`String.valueOf(user.getId())`),
  un valor que la propia API expone. Reemplazado por recuperación con token por mail
- **🚨 El filtro JWT escribía el access token completo en el log**, en nivel INFO. Eliminado; el
  resto de su log ruidoso pasó a DEBUG
- **🔧 Contador de intentos y revocación en cascada se perdían por rollback.**
  `consumeVerificationCode` y `rotateRefreshToken` guardaban su efecto y **después** lanzaban la
  excepción, así que con el rollback por defecto de `@Transactional` ni el límite de 3 intentos
  ni la defensa contra el reuso de refresh tokens hacían nada. Resuelto con
  `noRollbackFor = IllegalArgumentException.class`, verificado con un contexto Spring real
- **🔧 `JwtService` ignoraba `jwt.expiration-ms`** y hardcodeaba 10 horas en un campo. Ahora lee
  la configuración, y **valida el largo del secreto al arrancar** en vez de fallar en el primer
  login: con menos de 32 caracteres la aplicación no levanta

- **🗄️ Scripts SQL reescritos** — ninguno de los dos anteriores pasaba de su primera tabla,
  verificado contra MySQL 8.0.46 real:
  - `00-init.sql` fallaba en la línea 1 con `ERROR 4028` (`CREATE TABLE tickets_system
    CHARACTER SET…`, donde iba `CREATE DATABASE`)
  - `init.sql` fallaba en la línea 22 con `ERROR 1064` (`id UUID`: MySQL no tiene tipo
    `UUID`), y además tenía `ENUM("A","B")` con comillas dobles y una coma faltante
  - Tipos de `users` alineados con lo que genera Hibernate 7 desde la entidad:
    `binary(16)` para el UUID (**no** `char(36)`), `datetime(6)` para los timestamps y
    `ENUM` nativo para los enums. Verificado con `hbm2ddl=validate` contra MySQL real
  - `password_hash` de `varchar(50)` a `varchar(255)`: un hash BCrypt ocupa 60 caracteres
    y se truncaba
  - `last_name` y `phone` pasan a NULL-ables, como la entidad
  - Eliminada sintaxis de PostgreSQL que MySQL rechaza: `CREATE INDEX IF NOT EXISTS` e
    índices parciales (`… WHERE used = FALSE`)
  - Índices rehechos según las consultas reales de `UserRepository`
    (`(status, deleted_at)`, `(global_role, deleted_at)`); eliminados los redundantes
  - `init.sql` renombrado a `10-ticket-pending.sql`: **no se aplica**, porque las entidades
    de ticket usan ids `int` que no pueden referenciar `users.id` (`binary(16)`) y apuntan a
    las clases `Developer`/`Admin` eliminadas. Las FKs hacia usuarios quedan comentadas con
    la decisión documentada
  - Eliminadas las 3 vistas y los 2 procedimientos almacenados: referenciaban columnas y
    tablas inexistentes (`u.name`, `u.active`, `d.warnings`, `developer`, `admin`)
  - Eliminado el `DROP DATABASE IF EXISTS` del encabezado

- **🐳 `docker-compose.yml`**: montaba `./init_ticket_system.sql`, que no existe en el
  repositorio — el contenedor arrancaba con la base vacía. Ahora monta
  `./script/database/00-init.sql`

- **🔧 Dialecto de Hibernate**: `MySQL8Dialect` fue eliminado en Hibernate 7 y hacía fallar
  el arranque. Se quitó la property: Hibernate autodetecta el dialecto desde la metadata
  JDBC
- **🔧 `mainClass` en el pom**: estaba configurado en `maven-compiler-plugin`, que no
  conoce ese parámetro (warning en cada build). Movido a `spring-boot-maven-plugin`
- **🔧 `UpdateUserRequest` no se podía deserializar**: `@Builder` suprime el constructor sin
  argumentos y Jackson devolvía 500 en todo `PUT /update-profile`. Agregados
  `@NoArgsConstructor`/`@AllArgsConstructor`
- **🔧 Nombres explícitos en `@PathVariable`/`@RequestParam`**: sin ellos el ruteo depende
  del flag `-parameters` del compilador

- **🗂️ Reestructuración de Módulo History**
  - Módulo `history` eliminado para mejorar arquitectura
  - `DeveloperIncident` y `DeveloperByTicket` movidos a `module/user/model/`
  - `TicketEvaluationHistory` movido a `module/ticket/model/`
  - `DeveloperIncidentResponseDto` movido a `module/user/dto/`
  - Agregada anotación `@Entity` a todos los modelos de historial
  - Agregada anotación `@Id` y `@GeneratedValue` a `DeveloperByTicket`
  - Creados repositorios: `DeveloperIncidentRepository`, `DeveloperByTicketRepository`, `TicketEvaluationHistoryRepository`
  - Eliminado acoplamiento circular entre módulos
  - Imports actualizados en `Developer.java`

### �🐞 Fixed

## [1.0.0] - 2026-02-01

### ✨ Added

- **🏗️ Arquitectura Modular Completa**
  - Reorganización del proyecto por módulos de dominio (auth, user, ticket, history)
  - Separación clara de responsabilidades por módulo
  - Estructura escalable y mantenible

- **🎫 Sistema de Solicitudes de Devolución de Tickets**
  - Modelo `TicketRefundRequest` para gestionar devoluciones
  - Repository y Service completos para el flujo de devolución
  - Workflow: Developer solicita → Admin aprueba/rechaza
  - Estados: PENDING, APPROVED, REJECTED
  - DTOs: `TicketRefundRequestResponseDto`, `ProcessRefundRequestDto`

- **👥 Sistema de Usuarios Refactorizado**
  - Jerarquía clara: User (abstract) → Admin, Superadmin, Developer, Support
  - Services especializados por tipo de usuario
  - Repositories específicos para cada rol
  - Controllers dedicados para cada tipo de usuario

### 🔄 Changed

- **🌍 Renombrado de Modelos a Inglés**
  - `Tecnico` → `Developer`
  - `Trabajador` → `Support`
  - `SuperAdmin` → `Superadmin`
  - `Usuario` → `User`
  - `EstadoTicket` → `TicketStatus`
  - `Rol` → `UserRole`

- **📝 API Endpoints en Inglés**
  - Todos los endpoints renombrados con nombres descriptivos en inglés
  - Rutas versionadas: `/v1/` en todos los endpoints
  - Nomenclatura RESTful consistente
  - Documentación Swagger actualizada al inglés

- **🏛️ Controllers Refactorizados**
  - `AuthController`: Login, change password, reset password
  - `UserController`: Gestión de perfil personal
  - `AdminController`: Gestión de usuarios y solicitudes de devolución
  - `SuperadminController`: CRUD completo de usuarios
  - `DeveloperController`: Tomar, resolver y devolver tickets
  - `SupportController`: Evaluar tickets
  - `TicketController`: CRUD de tickets con autorización

- **⚙️ Services Simplificados**
  - `AuthService`: Responsabilidad única de autenticación
  - `UserService`: Operaciones CRUD de usuarios
  - `TicketService`: Gestión completa de tickets
  - `TicketRefundRequestService`: Workflow de devoluciones
  - Services especializados: `AdminService`, `SuperadminService`, `DeveloperService`, `SupportService`

- **🗄️ Modelo de Datos Mejorado**
  - Campo `developer` agregado a `Ticket` con relación `@ManyToOne`
  - Anotaciones JPA completas en `TicketRefundRequest`
  - Métodos getter/setter consistentes en todos los modelos
  - Eliminación de código duplicado

### 🗑️ Removed

- **🔇 Eliminación de Logs**
  - Removido Logger y LoggerFactory de todos los archivos
  - Eliminados statements de logging (info, error, debug)
  - Código más limpio y enfocado en lógica de negocio

- **🧹 Limpieza de Dependencias**
  - Eliminadas dependencias innecesarias en services
  - AuthService ya no depende de múltiples repositories
  - TicketService simplificado sin dependencias de auditoría

- **📊 Módulos No Implementados**
  - Referencias a sistema de auditoría eliminadas
  - Referencias a sistema de notificaciones eliminadas
  - Referencias a sistema de estadísticas eliminadas

### 🔧 Fixed

- **✅ Consistencia de DTOs**
  - `@JsonTypeName` actualizado para coincidir con UserRole enum
  - `SupportResponseDto`: "TRABAJADOR" → "SUPPORT"
  - `DeveloperResponseDto`: "TECNICO" → "DEVELOPER"
  - `SuperadminResponseDto`: "SUPER_ADMIN" → "SUPERADMIN"

- **🔐 Autenticación Mejorada**
  - `@AuthenticationPrincipal User` en lugar de `Authentication`
  - Validación de roles simplificada
  - Eliminación de casting complejo de UserDetails

### 🔐 Security

- **🛡️ Autorización por Roles**
  - Validación de roles en cada endpoint
  - `AccessDeniedException` para accesos no autorizados
  - Permisos granulares por tipo de operación

### 🏗️ Technical

- **🔄 DataInitializer Actualizado**
  - Métodos en inglés: `createSuperadmin()`, `createAdmin()`, `createDeveloper()`, `createSupport()`
  - Eliminación de logs
  - Emails actualizados: `developer@sistema.com`, `support@sistema.com`
  - Métodos setter actualizados: `setChangePassword()` en lugar de `setCambiarPass()`

- **📖 Documentación Completa**
  - README.md completamente reescrito
  - Arquitectura modular documentada
  - Todos los endpoints documentados con ejemplos
  - Guía de instalación actualizada
  - Stack tecnológico detallado

### 📊 Statistics

- **Archivos Modificados**: 30+ archivos
- **Controllers Refactorizados**: 7 controllers
- **Services Refactorizados**: 10+ services
- **DTOs Actualizados**: 15+ DTOs
- **Nuevos Archivos**: TicketRefundRequest, TicketRefundRequestService, TicketRefundRequestRepository
- **Líneas de Código**: ~3000+ líneas refactorizadas

## [1.0.1] - 2025-08-08

### 🐞 Fixed

- Problema de CORS resuelto: ahora las peticiones desde frontend funcionan correctamente.
- Filtro JWT ajustado: autenticación y autorización robusta en endpoints protegidos.

## [1.0.0] - 2025-08-07

### ✨ Added

- **🚀 Inicialización automática del SuperAdmin**
  - Creación automática del usuario SuperAdmin al iniciar la aplicación
  - Configuración inicial de datos del sistema
  - Credenciales por defecto: `superadmin@sistema.com` / `secret`
  - Logging detallado del proceso de inicialización con emojis

### 🔧 Fixed

- **🛠️ Corrección del sistema de discriminadores JPA**

  - Unificación de valores discriminadores para entidades Usuario
  - Corrección de inconsistencias entre "SUPER_ADMIN" y "SUPERADMIN"
  - Limpieza automática de registros corruptos en base de datos
  - Uso de consultas nativas SQL para evitar conflictos de mapeo

- **🗄️ Mejoras en repositorios**
  - Nuevos métodos con consultas nativas para operaciones robustas
  - Método `existsByEmailNative()` para verificaciones sin conflictos de discriminador
  - Método `deleteByEmail()` con consulta nativa y transaccional

### 🔐 Security

- **⚡ Configuración mejorada de roles y permisos**
  - Validación consistente de roles de usuario
  - Corrección en métodos de verificación de SuperAdmin
  - Mejor manejo de errores en inicialización de datos

### 🏗️ Technical

- **📋 DataInitializer mejorado**
  - Manejo robusto de errores durante la inicialización
  - Logging comprensivo con información detallada del estado del sistema
  - Verificaciones pre y post creación de usuarios
  - Constructor injection para mejor testabilidad

## [0.2.0] - 2025-07-16

### ✨ Added

- **📖 Documentación API completa con Swagger/OpenAPI**

  - Documentación automática de todos los endpoints
  - Interfaz interactiva Swagger UI en `/swagger-ui/index.html`
  - Documentación detallada de parámetros y respuestas
  - Ejemplos de request/response para cada endpoint
  - Categorización por funcionalidad (Auth, Usuarios, Tickets, etc.)

- **🏷️ Anotaciones Swagger en todos los controladores**:
  - `AuthController` - Autenticación y gestión de contraseñas
  - `UsuarioController` - Gestión de datos de usuarios
  - `TicketController` - CRUD completo de tickets
  - `TecnicoController` - Gestión de tickets por técnicos
  - `AdminController` - Funciones administrativas
  - `TrabajadorController` - Creación y seguimiento de tickets
  - `SuperAdminController` - Gestión completa del sistema
  - `EstadisticaController` - Métricas y estadísticas
  - `NotificacionController` - Sistema de notificaciones
  - `AuditoriaController` - Logs de auditoría

### 🔧 Technical

- Configuración simplificada de OpenAPI para evitar conflictos de versiones
- Integración completa con Spring Boot 3.5.3
- Documentación accesible en desarrollo y producción

## [0.1.0] - 2025-07-16

### ✨ Added

- **Sistema completo de gestión de tickets**

  - Autenticación JWT con roles y permisos
  - Gestión de usuarios (SuperAdmin, Admin, Técnico, Trabajador)
  - Creación, asignación y seguimiento de tickets
  - Sistema de notificaciones internas
  - Estadísticas y reportes básicos
  - API REST completa

- **Jerarquía de usuarios**:

  - **SuperAdmin**: Dueño del sistema con acceso total
  - **Admin**: Gestión de usuarios y tickets del sistema
  - **Técnico**: Resolución de tickets asignados
  - **Trabajador**: Creación y seguimiento de tickets

- **Endpoints principales**:
  - `/api/auth/*` - Autenticación y gestión de contraseñas
  - `/api/superadmin/*` - Gestión completa del sistema
  - `/api/admin/*` - Administración de usuarios y tickets
  - `/api/tickets/*` - Gestión de tickets
  - `/api/notificaciones/*` - Sistema de notificaciones

### 🔐 Security

- Autenticación y autorización JWT
- Encriptación de contraseñas con BCrypt
- Roles y permisos configurables
- Validaciones de seguridad avanzadas

### 🗄️ Database

- Base de datos MySQL con estructura optimizada
- Modelo de entidades JPA con herencia
- Repositorios Spring Data
- Script de inicialización incluido
- **Credenciales por defecto**: superadmin@sistema.com / secret

---

## Tipos de Cambios

- `Added` para nuevas funcionalidades
- `Changed` para cambios en funcionalidades existentes
- `Deprecated` para funcionalidades que se eliminarán pronto
- `Removed` para funcionalidades eliminadas
- `Fixed` para correcciones de bugs
- `Security` para cambios relacionados con seguridad
- `Database` para cambios en base de datos
- `Technical` para cambios técnicos y de infraestructura

## Enlaces

- [Unreleased]: https://github.com/Akc9912/apiTickets/compare/v1.0.0...HEAD
- [1.0.0]: https://github.com/Akc9912/apiTickets/releases/tag/v1.0.0
- [1.0.1]: https://github.com/Akc9912/apiTickets/compare/v1.0.0...v1.0.1
- [0.2.0]: https://github.com/Akc9912/apiTickets/compare/v0.1.0...v0.2.0
- [0.1.0]: https://github.com/Akc9912/apiTickets/releases/tag/v0.1.0
