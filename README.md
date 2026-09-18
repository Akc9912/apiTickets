# 🎫 ApiTickets - Sistema de Gestión de Tickets

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-green.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791.svg)
![Supabase Auth](https://img.shields.io/badge/Supabase-Auth-3ECF8E.svg)
![Maven](https://img.shields.io/badge/Maven-3.9+-purple.svg)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203.0-yellow.svg)
![Migration](https://img.shields.io/badge/Migraci%C3%B3n-35%25-yellow.svg)
![Version](https://img.shields.io/badge/Version-1.0.0--migration-blue.svg)

**Sistema de gestión de tickets en migración hacia arquitectura modular con PostgreSQL 16 y Supabase Auth**

⚠️ **Estado Actual:** Realizando migración de stack (MySQL → PostgreSQL) y refactorización modular. Ver [Arquitectura](./architecture.md) y [Plan de Migración](./docs/iteracion-01-migracion-stack-y-arquitectura/README.md)

[🚀 Inicio Rápido](#-instalación-rápida) • [📖 API Docs](#-documentación-de-la-api) • [🏗️ Arquitectura](#️-arquitectura-modular) • [🔐 Seguridad](#-seguridad-jwt)

</div>

---

## 📋 Tabla de Contenidos

- [🎯 Descripción](#-descripción)
- [🌟 Características Principales](#-características-principales)
- [🏗️ Arquitectura Modular](#️-arquitectura-modular)
- [🚀 Instalación Rápida](#-instalación-rápida)
- [📖 Documentación de la API](#-documentación-de-la-api)
- [🔐 Seguridad JWT](#-seguridad-jwt)
- [👥 Roles y Permisos](#-roles-y-permisos)
- [🛠️ Stack Tecnológico](#️-stack-tecnológico)
- [🤝 Contribución](#-contribución)
- [📄 Licencia](#-licencia)

---

## 🎯 Descripción

**ApiTickets** es un sistema empresarial de gestión de tickets desarrollado con **Spring Boot 4.1.1** y **Java 21** que está siendo migrado hacia una arquitectura completamente modular con **PostgreSQL 16** y **Supabase Auth**.

El sistema atiende múltiples canales de comunicación (usuarios directos, sistemas externos integrados) con soporte para diferentes roles y niveles de escalamiento de prioridad.

### ✨ **Características Principales**

- 🏗️ **Arquitectura Modular**: Organización por dominios (auth, account, ticket, support, product*)
- 🔐 **Autenticación Supabase**: Identidad centralizada, sin gestión de contraseñas en backend
- 🆔 **UUID First**: IDs como UUID en toda la base de datos
- 👥 **Gestión de Roles**: Superadmin, Admin, User (enum `globalRole` en la entidad `User`)
- 📝 **API RESTful**: Documentación automática con Swagger/OpenAPI 3.0
- 🎫 **Workflow de Tickets**: Prioridades, asignaciones, escalamientos automáticos
- 🔌 **Integración de Sistemas**: Recepción de mensajes desde sistemas externos
- 🔍 **Código Limpio**: Patrones SOLID, límites modulares automatizados en CI
- 📊 **Plan futuro**: Notificaciones en tiempo real, eventos, auditoría

### 📍 **Estado de la Migración**

| Aspecto | Progreso | Estado |
|--------|----------|--------|
| Stack (MySQL → PostgreSQL) | ████░░░░░░ 40% | 🟡 En curso |
| Autenticación (JWT local → Supabase Auth) | ██████░░░░ 60% | 🟡 En integración |
| Arquitectura (legacy → modular) | ███░░░░░░░ 30% | 🟡 Reorganización |
| Tests automatizados | ████░░░░░░ 35% | 🟡 Expansión |
| **Migración General** | ███░░░░░░░ **35%** | 🟡 **En Progreso** |

Ver [Plan Detallado de Migración](./docs/iteracion-01-migracion-stack-y-arquitectura/README.md) para más información.

### 🌐 **Acceso Rápido**
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🌟 Características Principales

### 🔐 **Seguridad y Autenticación**
- **JWT Authentication**: Tokens seguros para autenticación de usuarios
- **Autorización por Roles**: 3 roles globales (SUPERADMIN, ADMIN, USER) vía `@PreAuthorize`
- **BCrypt Password Hashing**: Contraseñas encriptadas con algoritmo BCrypt
- **Validación de Entrada**: Bean Validation en todos los DTOs
- **Spring Security**: Configuración robusta de seguridad

### 🎫 **Gestión de Tickets**
- **CRUD Completo**: Crear, leer, actualizar tickets
- **Estados del Ticket**: NOT_ATTENDED, ATTENDED, RESOLVED, FINALIZED, REOPENED
- **Asignación de Desarrolladores**: Tickets asignados a developers específicos
- **Solicitudes de Devolución**: Workflow completo para devolver tickets
- **Reapertura de Tickets**: Comentarios y justificaciones

### 👥 **Gestión de Usuarios**
- **Entidad Única**: sin subclases; el rol es el enum `globalRole`
- **Estados de Usuario**: PENDING_VERIFICATION, ACTIVE, INACTIVE, SUSPENDED, DELETED
- **Baja Lógica**: `deletedAt` como fuente de verdad; el email sigue ocupado
- **Contrato de Módulo**: `UserApi` como única entrada desde otros módulos

### 📚 **Documentación y API**
- **Swagger UI Interactivo**: Prueba endpoints desde el navegador
- **OpenAPI 3.0**: Especificación completa de la API
- **DTOs Documentados**: Esquemas de request/response
- **Ejemplos de Uso**: Casos de uso documentados

---

## 🏗️ Arquitectura Modular

### **📐 Estructura por Módulos**

El proyecto sigue una arquitectura modular por dominios:

```
src/main/java/com/poo/miapi/
├── 🔐 module/auth/           # Autenticación y autorización
│   ├── controller/           # AuthController
│   ├── dto/                  # LoginDto, ChangePasswordDto
│   └── service/              # AuthService, JwtService
│
├── 👥 module/users/          # Gestión de usuarios
│   ├── api/                  # UserApi (contrato de entrada al módulo)
│   │   └── dto/              # CreateUserRequest, UpdateUserRequest, UserResponse
│   ├── controller/           # UserController (perfil propio + administración)
│   ├── enums/                # UserRole, UserStatus
│   ├── model/                # User (entidad única)
│   ├── repository/           # UserRepository
│   └── service/              # UserService implements UserApi
│
├── 🎫 module/ticket/         # Gestión de tickets
│   ├── controller/           # TicketController
│   ├── dto/                  # TicketRequestDto, TicketResponseDto
│   ├── enums/                # TicketStatus, RefundRequestStatus
│   ├── model/                # Ticket, TicketRefundRequest
│   ├── repository/           # TicketRepository, TicketRefundRequestRepository
│   └── service/              # TicketService, TicketRefundRequestService
│
├── 📜 module/history/        # Historial (pendiente)
│
└── 🔧 shared/                # Componentes compartidos
    ├── config/               # SecurityConfig, DataInitializer
    ├── exception/            # GlobalExceptionHandler
    ├── security/             # JwtAuthenticationFilter
    └── util/                 # PasswordHelper, Utilities
```

### **🗂️ Modelo de Datos**

### **🗣️ Gestión de Identidad: Supabase Auth**

> ⚠️ **Esto quedó descartado.** El backend emite y rota sus propios tokens (access + refresh),
> y sigue siendo dueño de las contraseñas. El bloque de abajo describe un objetivo que no se va
> a implementar. Diseño vigente: sección de Autenticación de [endpoints.md](./endpoints.md).

**Objetivo original (no implementado):** delegar la gestión de usuarios a **Supabase Auth**:

```
🔑 Supabase Auth (Autoridad de Identidad)
  √ Usuarios y contraseñas
  √ Tokens JWT (emitidos por Supabase)
  √ MFA y recuperación
  √ Roles y claims
      → Backend (Consumidor)
         ✔️ Valida tokens
         ✔️ Mantiene perfil local
         ✔️ Autoriza operaciones
         × NO genera tokens
         × NO gestiona contraseñas
```

| Entidad | Descripción | Campos Clave |
|---------|-------------|--------------|
| **User** | Entidad única de usuario. Sin subclases: el rol es un enum. | id (UUID), firstName, lastName, email, passwordHash, phone, globalRole, status, createdAt, updatedAt, deletedAt |

Las subclases `Admin`, `Superadmin`, `Developer` y `Support` **fueron eliminadas**: el rol
vive en `globalRole` (`SUPERADMIN` \| `ADMIN` \| `USER`) y el ciclo de vida en `status`
(`PENDING_VERIFICATION` \| `ACTIVE` \| `INACTIVE` \| `SUSPENDED` \| `DELETED`). La baja es
lógica vía `deletedAt`.

| Entidad | Descripción | Campos Clave |
|---------|-------------|--------------|
| **Ticket** | Ticket de soporte | id, title, description, status, creator, developer |
| **TicketRefundRequest** | Solicitud de devolución | id, developer, ticket, reason, status |

---

## 🚀 Instalación Rápida

### **📋 Prerrequisitos**

- ☕ **Java 21** ([OpenJDK 21](https://jdk.java.net/21/))
- 📦 **Maven 3.9+**
- � **PostgreSQL 16**
- 🔑 **Cuenta Supabase** (para Auth)

### **⚡ Pasos de Instalación**

```bash
# 1. Clonar el repositorio
git clone https://github.com/Akc9912/apiTickets.git
cd apiTickets

# 2. Crear base de datos PostgreSQL
createdb apitickets

# 3. Ejecutar schema DDL
mysql -u root -p apiticket < script/database/00-init.sql

# 4. Configurar variables de entorno
cp .env.example .env
# Editar .env con credenciales de Supabase y PostgreSQL

# 5. Compilar y ejecutar
./mvnw clean install
./mvnw spring-boot:run
```

**Configuración Mínima (`application.properties`):**

```properties
# Database PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/apitickets
spring.datasource.username=postgres
spring.datasource.password=tu_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Supabase Auth
supabase.url=${SUPABASE_URL:https://your-project.supabase.co}
supabase.anon.key=${SUPABASE_ANON_KEY:your-anon-key}
supabase.jwt.secret=${SUPABASE_JWT_SECRET:your-jwt-secret}

# JWT
jwt.secret=tu_clave_secreta_jwt_minimo_256_bits
jwt.expiration=86400000

# App
app.default-password=default123
```

```bash
# 4. Compilar y ejecutar
./mvnw clean install
./mvnw spring-boot:run
```

### **✅ Verificar Instalación**

El sistema crea automáticamente usuarios por defecto:

| Email | Password | Rol |
|-------|----------|-----|
| `superadmin@sistema.com` | `secret` | Superadmin |
| `admin@sistema.com` | `secret` | Admin |
| `developer@sistema.com` | `secret` | Developer |
| `support@sistema.com` | `secret` | Support |

**Acceder a Swagger:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 📖 Documentación de la API

### **🌐 Swagger UI Interactivo**

La API incluye documentación completa generada automáticamente con Swagger/OpenAPI 3.0:

**Acceso:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### **📋 Endpoints Principales**

#### **🔐 Autenticación (`/api/auth`)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login con email y password |
| POST | `/api/auth/change-password` | Cambiar contraseña propia |
| POST | `/api/auth/reset-password` | Resetear contraseña (Admin) |

#### **⚙️ Administración de usuarios (`/api/admin/v1`)**

Requiere `ADMIN` o `SUPERADMIN`. No hay árbol `/api/superadmin`: con rol global en una
entidad única, un solo árbol cubre los dos roles.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/users?includeDeleted=` | Listar usuarios (activos, u opcionalmente todos) |
| GET | `/users/{id}` | Obtener usuario por UUID |
| GET | `/users/filter/role/{role}` | Filtrar por rol global |
| GET | `/users/filter/status/{status}` | Filtrar por estado |
| GET | `/users/search?name=` | Buscar por nombre o apellido |
| PUT | `/users/{id}/status/{status}` | Cambiar estado (`DELETED` no; usar DELETE) |
| DELETE | `/users/{id}` | Baja lógica (soft delete), 204 |
| GET | `/return-requests` | Listar solicitudes de devolución |
| POST | `/return-requests/{id}/process` | Procesar solicitud de devolución |

> 🚫 El alta de usuarios **no tiene endpoint**: el registro vive en `module/auth`.

#### **🎫 Tickets (`/api/tickets/v1`)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/all` | Listar todos los tickets (Admin) |
| GET | `/support/my-tickets` | Mis tickets (Support) |
| GET | `/support/tickets-to-evaluate` | Tickets para evaluar (Support) |
| POST | `/create` | Crear nuevo ticket |
| POST | `/{id}/reopen` | Reabrir ticket |

#### **🔧 Developer (`/api/developer/v1`)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/tickets/{ticketId}/take` | Tomar ticket |
| POST | `/tickets/{ticketId}/resolve` | Resolver ticket |
| POST | `/tickets/{ticketId}/return` | Solicitar devolución de ticket |

#### **💼 Support (`/api/support/v1`)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/tickets/{ticketId}/evaluate` | Evaluar solución de ticket |

#### **👤 Perfil propio (`/api/user/v1`)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/view-profile` | Ver perfil propio |
| PUT | `/update-profile` | Actualizar perfil propio (parcial: firstName, lastName, phone) |

---

## 🔐 Seguridad JWT

### **🛡️ Características de Seguridad**

- ✅ **JWT Authentication**: Tokens seguros con HS256
- ✅ **Spring Security 7.1**: Framework de seguridad robusto
- ✅ **BCrypt Password Hashing**: Contraseñas encriptadas
- ✅ **Bean Validation**: Validación de entrada en DTOs
- ✅ **SQL Injection Prevention**: JPA/Hibernate
- ✅ **Authorization por Roles**: `@PreAuthorize` + `@EnableMethodSecurity`

### **🔑 Flujo de Autenticación**

```bash
# 1. Login
POST /api/auth/login
{
  "email": "superadmin@sistema.com",
  "password": "secret"
}

# Response
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": { ... }
}

# 2. Usar token en requests
GET /api/user/v1/view-profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 👥 Roles y Permisos

| Rol | Código | Permisos | Endpoints Principales |
|-----|--------|----------|----------------------|
| **Superadmin** | `SUPERADMIN` | 🔓 Total | `/api/admin/*` - Administración de usuarios |
| **Admin** | `ADMIN` | 🔐 Gestión | `/api/admin/*` - Usuarios y solicitudes |
| **User** | `USER` | 👤 Propio | `/api/user/*` - Su propio perfil |

Los roles `DEVELOPER` y `SUPPORT` fueron eliminados al unificar las subclases de usuario en
una entidad única con `globalRole`. Las secciones de Developer y Support de esta
documentación describen código pendiente de migrar.

> ⚠️ `hasAnyRole` espera los authorities con prefijo: `ROLE_ADMIN`, no `ADMIN`.

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Uso |
|------------|---------|-----|
| **Java** | 21 | Lenguaje principal |
| **Spring Boot** | 4.1.1 | Framework backend |
| **Spring Framework** | 7.0.9 | Núcleo (vía Boot) |
| **Spring Security** | 7.1.1 | Seguridad y autenticación |
| **Hibernate ORM** | 7.4.5 | JPA provider (vía Boot) |
| **Spring Data JPA** | - | Persistencia |
| **MySQL** | 8.0+ | Base de datos |
| **JWT (jjwt)** | 0.12.6 | Tokens de autenticación |
| **Swagger/OpenAPI** | 2.7.0 | Documentación API |
| **Maven** | 3.9+ | Gestión de dependencias |
| **HikariCP** | - | Connection pooling |

---

## 📁 Estructura del Proyecto

```
apiTickets/
├── src/main/java/com/poo/miapi/
│   ├── MiapiApplication.java          # Clase principal
│   │
│   ├── module/
│   │   ├── auth/                      # Autenticación
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   └── service/
│   │   │
│   │   ├── user/                      # Usuarios
│   │   │   ├── controller/            # 5 controllers (User, Admin, etc.)
│   │   │   ├── dto/
│   │   │   ├── enums/
│   │   │   ├── model/                 # User, Admin, Developer, etc.
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   ├── ticket/                    # Tickets
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── enums/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   └── history/                   # Historial (futuro)
│   │
│   └── shared/                        # Compartido
│       ├── config/                    # SecurityConfig, DataInitializer
│       ├── exception/                 # GlobalExceptionHandler
│       ├── security/                  # JwtAuthenticationFilter
│       └── util/                      # Utilidades
│
├── src/main/resources/
│   ├── application.properties         # Configuración
│   └── META-INF/
│
├── script/database/                   # Esquema SQL (00-init.sql aplicado, 10-ticket-pending.sql no)
├── pom.xml                            # Dependencias Maven
└── README.md                          # Este archivo
```

---

## 🤝 Contribución

### **Cómo Contribuir**

1. **Fork** el repositorio
2. **Crear rama**: `git checkout -b feature/nueva-funcionalidad`
3. **Commit**: `git commit -m 'Add: nueva funcionalidad'`
4. **Push**: `git push origin feature/nueva-funcionalidad`
5. **Pull Request**: Abrir PR con descripción detallada

### **Convenciones de Código**

- **Java**: Camel case, nombres descriptivos
- **Endpoints**: Nombres en inglés, RESTful
- **DTOs**: Request/Response suffixes
- **Código Limpio**: Sin logging innecesario

---

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Ver [LICENSE](LICENSE) para más detalles.

---

## 👨‍💻 Autor

**Sebastian Conde**
- GitHub: [@Akc9912](https://github.com/Akc9912)
- Email: akc9912@gmail.com

---

<div align="center">

**⭐ Si este proyecto te es útil, considera darle una estrella en GitHub ⭐**

[🔝 Volver al inicio](#-apitickets---sistema-de-gestión-de-tickets)

**📖 [Swagger UI](http://localhost:8080/swagger-ui/index.html) • 🐛 [Issues](https://github.com/Akc9912/apiTickets/issues) • 💡 [Discussions](https://github.com/Akc9912/apiTickets/discussions)**

</div>
