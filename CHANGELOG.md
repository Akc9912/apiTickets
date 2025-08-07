# Changelog

Todos los cambios notables de este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### 📚 En Desarrollo

- Mejoras en la interfaz de usuario
- Optimizaciones de rendimiento
- Nuevas funcionalidades de reportes

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
- [1.0.0]: https://github.com/Akc9912/apiTickets/compare/v0.2.0...v1.0.0
- [0.2.0]: https://github.com/Akc9912/apiTickets/compare/v0.1.0...v0.2.0
- [0.1.0]: https://github.com/Akc9912/apiTickets/releases/tag/v0.1.0
