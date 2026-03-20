# 🎫 ApiTickets - Sistema de Gestión de Tickets

<div align="center">

![Java](https://img.shields.io/badge/Java-24-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-green.svg)
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

**ApiTickets** es un sistema empresarial de gestión de tickets desarrollado con **Spring Boot 3.5.4** y **Java 24** que está siendo migrado hacia una arquitectura completamente modular con **PostgreSQL 16** y **Supabase Auth**.

El sistema atiende múltiples canales de comunicación (usuarios directos, sistemas externos integrados) con soporte para diferentes roles y niveles de escalamiento de prioridad.

### ✨ **Características Principales**

- 🏗️ **Arquitectura Modular**: Organización por dominios (auth, account, ticket, support, product*)
- 🔐 **Autenticación Supabase**: Identidad centralizada, sin gestión de contraseñas en backend
- 🆔 **UUID First**: IDs como UUID en toda la base de datos
- 👥 **Gestión de Roles**: Superadmin, Admin, Developer, Support (definidos en Supabase)
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
- **Autorización por Roles**: 4 roles con permisos específicos (SUPERADMIN, ADMIN, DEVELOPER, SUPPORT)
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
- **Jerarquía de Roles**: User → Admin, Superadmin, Developer, Support
- **Estados de Usuario**: Activo, Bloqueado, Cambio de contraseña requerido
- **CRUD por Roles**: Superadmin puede gestionar todos los usuarios
- **Servicios Especializados**: Un service por cada tipo de usuario

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
├── 👥 module/user/           # Gestión de usuarios
│   ├── controller/           # UserController, AdminController, etc.
│   ├── dto/                  # UserRequestDto, UserResponseDto
│   ├── enums/                # UserRole
│   ├── model/                # User, Admin, Superadmin, Developer, Support
│   ├── repository/           # UserRepository, AdminRepository, etc.
│   └── service/              # UserService, AdminService, etc.
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

**Importante:** A partir de esta versión, la gestión completa de usuarios se delega a **Supabase Auth**:

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
| **User** | Clase abstracta base | id, name, lastName, email, password, role, active, blocked |
| **Admin** | Administrador del sistema | Hereda de User |
| **Superadmin** | Super administrador | Hereda de User, permisos totales |
| **Developer** | Técnico que resuelve tickets | Hereda de User |
| **Support** | Usuario que crea tickets | Hereda de User |
| **Ticket** | Ticket de soporte | id, title, description, status, creator, developer |
| **TicketRefundRequest** | Solicitud de devolución | id, developer, ticket, reason, status |

---

## 🚀 Instalación Rápida

### **📋 Prerrequisitos**

- ☕ **Java 24** ([OpenJDK 24](https://jdk.java.net/24/))
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
psql apitickets < create_database.sql

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

#### **👑 Superadmin (`/api/superadmin/v1`)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/users` | Crear cualquier tipo de usuario |
| GET | `/users` | Listar todos los usuarios |
| GET | `/users/role/{role}` | Filtrar usuarios por rol |
| GET | `/users/active` | Listar usuarios activos |
| GET | `/users/blocked` | Listar usuarios bloqueados |
| PUT | `/users/{id}/activate` | Activar usuario |
| PUT | `/users/{id}/deactivate` | Desactivar usuario |
| DELETE | `/users/{id}` | Eliminar usuario |

#### **⚙️ Admin (`/api/admin/v1`)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| PUT | `/users/{id}` | Actualizar datos de usuario |
| PUT | `/users/{id}/status/toggle-active` | Toggle estado activo |
| PUT | `/users/{id}/status/toggle-blocked` | Toggle estado bloqueado |
| PUT | `/users/{id}/role` | Cambiar rol de usuario |
| GET | `/users` | Listar usuarios |
| GET | `/users/{id}` | Obtener usuario específico |
| GET | `/return-requests` | Listar solicitudes de devolución |
| POST | `/return-requests/{id}/process` | Procesar solicitud de devolución |

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

#### **👤 User (`/api/user/v1`)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/profile` | Ver perfil propio |
| PUT | `/profile` | Actualizar perfil propio |

---

## 🔐 Seguridad JWT

### **🛡️ Características de Seguridad**

- ✅ **JWT Authentication**: Tokens seguros con HS256
- ✅ **Spring Security 6.5**: Framework de seguridad robusto
- ✅ **BCrypt Password Hashing**: Contraseñas encriptadas
- ✅ **Bean Validation**: Validación de entrada en DTOs
- ✅ **SQL Injection Prevention**: JPA/Hibernate
- ✅ **Authorization por Roles**: `@AuthenticationPrincipal`

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
GET /api/user/v1/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 👥 Roles y Permisos

| Rol | Código | Permisos | Endpoints Principales |
|-----|--------|----------|----------------------|
| **Superadmin** | `SUPERADMIN` | 🔓 Total | `/api/superadmin/*` - CRUD completo de usuarios |
| **Admin** | `ADMIN` | 🔐 Gestión | `/api/admin/*` - Gestión de usuarios y solicitudes |
| **Developer** | `DEVELOPER` | 🔧 Técnico | `/api/developer/*` - Tomar y resolver tickets |
| **Support** | `SUPPORT` | 📝 Usuario | `/api/support/*` - Crear y evaluar tickets |

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Uso |
|------------|---------|-----|
| **Java** | 24 | Lenguaje principal |
| **Spring Boot** | 3.5.3 | Framework backend |
| **Spring Security** | 6.5.1 | Seguridad y autenticación |
| **Spring Data JPA** | 3.5.3 | Persistencia |
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
├── create_database.sql                # Script de base de datos
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
