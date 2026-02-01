# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### 📚 En Desarrollo

### � Changed

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
