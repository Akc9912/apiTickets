<div align="center">

# 🎟️ API Tickets - Backend

### Plataforma de Gestión de Tickets con Soporte Modular

**Backend Spring Boot para Sistema de Tickets**

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=for-the-badge&logo=spring-boot)](https://spring.io/)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-Local-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)

**Estado actual: MySQL + JWT local + gestión de usuarios interna** | **Objetivo: PostgreSQL 16 + Supabase Auth** | Progreso migración: **35%**

</div>

---

## 📋 Tabla de Contenidos

- [🎯 Contexto y Objetivos](#-contexto-y-objetivos)
- [🔄 Estado de Migración](#-estado-de-migración)
- [🏗️ Arquitectura](#️-arquitectura)
- [🗄️ Esquema de Datos](#️-esquema-de-datos)
- [🔐 Autenticación](#-autenticación)
- [🛠️ Stack Tecnológico](#️-stack-tecnológico)
- [🚀 Instalación](#-instalación)
- [📋 Plan de Migración](#-plan-de-migración)
- [📚 Documentación Adicional](#-documentación-adicional)

---

## 🎯 Contexto y Objetivos

**apiTickets** es una plataforma de gestión centralizada de tickets de soporte que atiende múltiples canales de comunicación (usuarios directos, sistemas externos integrados). Este proyecto representa la **migración del stack** desde MySQL + JWT local hacia PostgreSQL 16 + Supabase Auth, junto con la **implementación de arquitectura modular** para garantizar escalabilidad y mantenibilidad.

### ¿Por qué mover a Supabase Auth?

```
✅ Gestión de identidad confiable y segura
✅ Eliminación de complejidad en backend (no reinventar rueda)
✅ Autenticación centralizada sin duplicación de contraseñas
✅ Compliance y auditoría integrada
✅ Soporte multi-factor (MFA) listo para usar
✅ Menor superficie de ataque en backend
```

### ¿Por qué UUID?

```
✅ Requerimiento de Supabase Auth (user_id es siempre UUID)
✅ Escalabilidad sin colisiones en distribución
✅ Mayor seguridad (imposible adivinar IDs secuenciales)
✅ Preparación para futura replicación/sharding
```

---

## 🔄 Estado de Migración

### Estado real auditado

Backend Spring Boot funcional con **MySQL + JWT local + gestión de usuarios interna**. PostgreSQL 16 y Supabase Auth son parte del **estado objetivo**, no del estado actual.

| Área                      | Estado actual                             | Progreso hacia objetivo | Evidencia                                                                          |
| ------------------------- | ----------------------------------------- | ----------------------- | ---------------------------------------------------------------------------------- |
| Build                     | ROTO                                      | ❌ 0%                   | `mvn compile` falla con 59 errores en `ticket`, `auth` y `DataInitializer`          |
| Base de datos (actual)    | MySQL 8 activo                            | ✅ 100%                 | `application.properties` con `com.mysql.cj.jdbc.Driver` + `script/database/00-init.sql` |
| Migración DB a PostgreSQL | No iniciada en código                     | ❌ 0%                   | No hay driver PostgreSQL en `pom.xml` ni datasource PostgreSQL activo              |
| Seguridad (actual)        | JWT local activo                          | ✅ 100%                 | `module/auth/service/JwtService.java` genera y valida tokens                       |
| Migración a Supabase Auth | No iniciada en código                     | ❌ 0%                   | `AuthService` sigue validando password local y gestiona reset/cambio de contraseña |
| Gestión de usuarios       | Interna, entidad única con rol global     | ✅ 100%                 | `module/users` compila y expone 9 endpoints vía `UserApi`                          |
| Refactor de `user`        | Completado como `users` unificado         | ✅ 100%                 | Subclases eliminadas, rol por enum `globalRole`, ids UUID, contrato `UserApi`       |
| Módulo Ticket             | No compila tras el refactor de `users`    | 🔴 30%                  | Usa `Admin`/`Developer`/`Support` y DTOs eliminados; pendiente de migrar           |
| Módulos Post-MVP          | Solo diseño                               | 🟡 10%                  | `support`, `product`, `notification` aún no existen en código                      |
| Tests Automatizados       | Parcial funcional                         | 🟡 35%                  | Suite presente, cobertura de servicios/controladores todavía incompleta            |

### Principales Cambios en Esta Iteración

1. ✅ **Línea base actual validada**: MySQL 8 + JWT local + usuarios internos
2. ✅ **Objetivo de migración definido**: PostgreSQL 16 + Supabase Auth
3. ✅ **Separación de estados**: documentación diferenciada entre ACTUAL y OBJETIVO
4. ✅ **Ruta técnica acordada**: migración incremental (DB, auth y módulos)
5. ✅ **Arquitectura modular objetivo**: límites y módulos definidos

### Bloqueadores Actuales

1. **Compilación**: `ticket`, `auth` y `DataInitializer` referencian las subclases
   eliminadas (`Admin`, `Developer`, `Support`, `Superadmin`), los DTOs viejos y los roles
   `DEVELOPER`/`SUPPORT`. 59 errores pendientes.
2. **Modelo del principal**: `User` no implementa `UserDetails` y nadie emite authorities
   `ROLE_*`, así que los 9 endpoints de `users` responden 403. Hay que decidir entre que la
   entidad implemente `UserDetails` o introducir un `UserPrincipal` aparte.
3. **Migrar consumidores a `UserApi`** en lugar de inyectar `UserService`.
4. Desacoplar `ticket` del usuario (referenciar por UUID en lugar de relación JPA).
5. Implementar módulos Post-MVP (`support`, `product`, `notification`).
6. Expandir tests de servicio/controlador.

### Reglas Operativas

1. **Estado actual**: autenticación y gestión de contraseñas se realizan en este backend (JWT local)
2. **Estado actual**: base en MySQL con IDs `INT`; objetivo es migrar a UUID en PostgreSQL
3. **Módulos**: sin imports directos entre módulos; comunicación vía contratos/eventos
4. **Documentación**: toda sección debe indicar explícitamente si describe ACTUAL u OBJETIVO

---

## 🧭 Arquitectura de Referencia

### 🎯 Arquitectura ACTUAL (Solo Backend)

```
┌──────────────────────────────────┐
│      Spring Boot Backend         │
│       (este repositorio)         │
│                                  │
│  • REST Controllers              │
│  • Business Logic                │
│  • Auth local (JWT propio)       │
│  • Gestión de usuarios local      │
│  • JPA + Repositories            │
└───────────────┬──────────────────┘
          │
        ┌──────┴──────────┐
        │                 │
        ↓                 ↓
    ┌────────────┐   ┌────────────┐
    │ JWT HS256  │   │  MySQL 8   │
    │ (backend)  │   │ tickets_system |
    └────────────┘   └────────────┘
```

**Estado:** ✅ **BACKEND ACTUAL EN OPERACIÓN**

- El repositorio contiene únicamente el backend Spring Boot
- El backend genera y valida JWT localmente (`jwt.secret`)
- La gestión de usuarios se realiza en tablas internas (`user`, `admin`, `support`, `developer`)
- La base transaccional actual es MySQL 8 (`tickets_system`)

### 🏗️ Arquitectura OBJETIVO (Backend Modular Consolidado)

```
┌──────────────────────────────────────────────┐
│          Spring Boot Backend Modular         │
│                                              │
│  module/auth      module/account             │
│  module/ticket    module/support             │
│  module/product   module/notification        │
│                 + shared                     │
└───────────────┬───────────────────┬──────────┘
          │                   │
          ↓                   ↓
      ┌────────────┐       ┌────────────┐
      │ Supabase   │       │ PostgreSQL │
      │ Auth       │       │    16      │
      └────────────┘       └────────────┘
```

**Estado actual:** Prototipo backend en migración

- Backend operativo en MySQL 8 y JWT local
- `module/auth`, `module/user` y `module/ticket` activos en producción técnica actual
- Objetivo `module/account` definido, aún no implementado como reemplazo completo de `module/user`
- Módulos `support`, `product` y `notification` en etapa de diseño
- Migración a PostgreSQL 16 + Supabase Auth pendiente en código

---

## 🏗️ Arquitectura

### 📦 Módulos ACTUALES (Implementados hoy)

| #   | Módulo            | Tablas DB actuales                                                                                           | Responsabilidad                                           |
| --- | ----------------- | ------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------- |
| 1   | **Auth Module**   | `user` (indirecto)                                                                                           | Login, emisión/validación JWT, cambio/reset de contraseña |
| 2   | **Users Module**  | `users` (entidad única, rol en `global_role`)                                                                | Gestión de usuarios, roles y ciclo de vida                |
| 3   | **Ticket Module** | `ticket`, `ticket_refund_requests`, `ticket_evaluation_history`, `developer_by_ticket`, `developer_incident` | Operación y flujo de tickets                              |

### 🧩 Módulos OBJETIVO (Post-Migración)

| #   | Módulo                  | Tablas DB objetivo                           | Responsabilidad                            | Prioridad   |
| --- | ----------------------- | -------------------------------------------- | ------------------------------------------ | ----------- |
| 1   | **Auth Module**         | auth.users (Supabase)                        | Validación de token externo y autorización | 🔥 MVP      |
| 2   | **Account Module**      | user_profiles                                | Perfil e información del usuario           | 🔥 MVP      |
| 3   | **Ticket Module**       | tickets, ticket_comments, ticket_attachments | Gestión de tickets de soporte              | 🔥 MVP      |
| 4   | **Support Module**      | support_teams, support_assignments           | Gestión de equipos de soporte              | 🟡 Post-MVP |
| 5   | **Product Module**      | products, product_integrations               | Gestión de productos y sistemas integrados | 🟡 Post-MVP |
| 6   | **Notification Module** | notifications, notification_subscriptions    | Sistema de notificaciones y eventos        | 🟡 Post-MVP |

### Estructura de Código

#### Estado ACTUAL (implementado hoy)

```
src/main/java/com/poo/miapi/
│
├── module/
│   ├── auth/                     → login + JWT local
│   ├── users/                    → gestión de usuarios y roles (contrato: api/UserApi)
│   └── ticket/                   → operación de tickets
│
└── shared/
    ├── config/
    ├── exception/
    ├── security/
    └── util/
```

#### Estado OBJETIVO (post-migración)

```
src/main/java/com/poo/miapi/
│
├── module/
│   ├── auth/                     → validación de token Supabase
│   ├── account/                  → perfil de usuario (sin credenciales)
│   ├── ticket/                   → ticketing core
│   ├── support/                  → asignaciones y operación
│   ├── product/                  → integraciones externas
│   └── notification/             → eventos y notificaciones
│
└── shared/
    ├── contracts/
    ├── events/
    ├── exception/
    └── util/
```

### Ventajas de Esta Arquitectura

| Ventaja              | Descripción                                                   |
| -------------------- | ------------------------------------------------------------- |
| **🔒 Encapsulación** | Cada módulo es independiente, con sus propias capas           |
| **📦 Cohesión**      | Todo lo relacionado a un dominio está junto                   |
| **🔄 Reutilización** | Código compartido en /shared evita duplicación                |
| **🧪 Testabilidad**  | Cada módulo se puede testear independientemente               |
| **👥 Equipos**       | Diferentes equipos pueden trabajar en módulos separados       |
| **↗️ Escalabilidad** | Módulos organizados, opcionalmente separables si es necesario |

### Dependencias Entre Módulos

**Reglas:**

- ✅ Módulos pueden usar código de `/shared`
- ✅ Módulos pueden comunicarse vía interfaces/eventos
- ❌ NO debe haber imports directos entre módulos
- ❌ NO circular dependencies

**Ejemplo de comunicación entre módulos:**

```java
// ❌ EVITAR: Import directo del service de otro módulo
import com.poo.miapi.module.users.service.UserService;

// ✅ CORRECTO: depender del contrato del módulo
import com.poo.miapi.module.users.api.UserApi;
```

**Implementado hoy:** `module/users/api/UserApi.java` es el contrato de entrada al módulo
users, y `UserService` lo implementa. Los consumidores deben inyectar `UserApi`, no
`UserService`.

Tres métodos del contrato (`findById`, `findByEmail`, `save`) todavía exponen la entidad
`User` en vez de un DTO. Es una excepción documentada y deliberada: `Ticket` referencia al
usuario con `@ManyToOne` y necesita la entidad JPA real, y `auth` la necesita para firmar
el JWT. Se pueden sacar cuando `ticket` pase a referenciar por UUID y `auth` tenga sus
propias operaciones de password en el contrato.

> ⚠️ Pendiente: `AuthService`, `TicketService`, `TicketRefundRequestService` y
> `TicketController` siguen inyectando `UserService` directamente. Funciona porque es el
> mismo bean, pero el límite modular no se respeta hasta que migren a `UserApi`.

### Escalabilidad

La arquitectura modular permite:

- ✅ **Escalar el monolito completo** (vertical u horizontal)
- ✅ **Cachear por módulo** (caché independiente para ticket vs account)
- ✅ **Optimizar por módulo** (queries específicas, índices personalizados)
- 🔒 **Opción futura:** Extraer módulo a servicio separado (solo si es necesario)

> **Nota:** Microservicios NO son parte del plan. El monolito modular es suficiente para la mayoría de casos. La arquitectura simplemente está preparada por si en el futuro lejano se necesita separar algo por razones de escala extrema.

---

## 🗄️ Esquema de Datos

### Base de Datos ACTUAL (MySQL 8)

El esquema actual está en [script/database/00-init.sql](script/database/00-init.sql) y corresponde a
MySQL 8. Los tipos de `users` son exactamente los que genera Hibernate 7 desde la entidad
(`UUID` → `binary(16)`, `LocalDateTime` → `datetime(6)`, enums de Java → `ENUM` nativo),
porque `ddl-auto=update` está activo y reconcilia diferencias.

Las tablas del módulo ticket están en
[script/database/10-ticket-pending.sql](script/database/10-ticket-pending.sql) y **no se
aplican**: sus entidades usan ids `int` que no pueden referenciar `users.id` (`binary(16)`),
y apuntan a las clases `Developer`/`Admin` que el refactor eliminó.

**Entidades principales implementadas hoy:**

- Usuarios y roles: `users` (entidad única; el rol vive en la columna `global_role`)
- Tickets: `ticket`, `ticket_refund_requests`, `ticket_evaluation_history`
- Operación de developers: `developer_by_ticket`, `developer_incident`

**Características actuales:**

```
✅ IDs INT AUTO_INCREMENT
✅ Relaciones por FK a tablas de usuarios locales
✅ Contraseñas persistidas en tabla user (hash en backend)
✅ Modelo de autenticación acoplado al backend
```

### Base de Datos OBJETIVO (PostgreSQL 16 + UUID)

Objetivo de migración (aún no implementado en código):

- IDs UUID en entidades de dominio
- Separación de identidad hacia Supabase Auth
- Modelo modular con `account`, `ticket`, `support`, `product`, `notification`

### Adaptación para Spring Boot (Plan de migración)

- Cambiar driver MySQL por PostgreSQL
- Migrar IDs `INT` a `UUID`
- Ajustar mapeos JPA y enums
- Desacoplar identidad de tablas locales de usuario

---

## 🔐 Autenticación

### Estado ACTUAL (implementado hoy)

La autenticación actual es **local en este backend**:

```
1. Usuario → /api/auth/login
2. Backend → valida email/password contra tablas locales (module/user)
3. Backend → genera JWT propio (module/auth/service/JwtService)
4. Cliente consumidor → usa token Bearer en requests
5. Backend → valida JWT con jwt.secret
```

**Hoy el backend sí hace gestión de identidad:**

- ✅ Genera tokens JWT
- ✅ Valida tokens JWT
- ✅ Gestiona contraseñas (cambio y reset)

### Estado OBJETIVO (post-migración)

Migrar a Supabase Auth como proveedor de identidad:

- Backend valida tokens emitidos por Supabase
- Backend deja de gestionar contraseñas
- Backend desacopla identidad de `module/user` hacia `module/account`

---

## 🛠️ Stack Tecnológico

### Backend (ACTUAL)

| Tecnología            | Versión | Propósito                |
| --------------------- | ------- | ------------------------ |
| **Spring Boot**       | 4.1.1   | Framework principal      |
| **Java**              | 21      | Lenguaje                 |
| **Spring Framework**  | 7.0.9   | Núcleo (vía Boot)        |
| **Spring Security**   | 7.1.1   | Seguridad (vía Boot)     |
| **Hibernate ORM**     | 7.4.5   | JPA provider (vía Boot)  |
| **Spring Data JPA**   | -       | ORM y repositories       |
| **MySQL Connector/J** | Runtime | Conexión a MySQL         |
| **Spring Security**   | -       | Seguridad y JWT          |
| **jjwt**              | 0.11.5  | Emisión y validación JWT |
| **Maven**             | -       | Build tool               |

### Stack OBJETIVO (post-migración)

| Tecnología          | Estado   | Propósito                |
| ------------------- | -------- | ------------------------ |
| **PostgreSQL 16**   | Objetivo | Base de datos principal  |
| **Supabase Auth**   | Objetivo | Proveedor de identidad   |
| **UUID en dominio** | Objetivo | Identificadores globales |

### Consumidores (Fuera de este repositorio)

| Tipo de consumidor             | Estado    | Propósito                  |
| ------------------------------ | --------- | -------------------------- |
| **Web App (externa)**          | Activo    | Consumir endpoints REST    |
| **Integraciones externas**     | En diseño | Crear y actualizar tickets |
| **Clientes internos de admin** | En diseño | Operación y seguimiento    |

---

## 🚀 Instalación

### Prerrequisitos

```bash
✅ Java 21
✅ Maven 3.x
✅ MySQL 8.x
✅ JWT_SECRET configurado
```

### 1. Configurar Base de Datos

**MySQL local (estado actual)**

```bash
# Ejecutar el script de init en tu instancia local
mysql -u root -p apiticket < script/database/00-init.sql
```

### 2. Configurar Variables de Entorno

Crear `.env` o `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/tickets_system
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JWT local
jwt.secret=TU_JWT_SECRET_DE_32_PLUS_CARACTERES
jwt.expiration-ms=36000000

# Server
server.port=8080
```

> Nota: La configuración de PostgreSQL/Supabase aplica al estado objetivo y se activará al iniciar esa fase de migración.

### 3. Build y Run

```bash
# Compilar
./mvnw -DskipTests compile

# Ejecutar tests
./mvnw test

# Run
./mvnw spring-boot:run
```

---

## 📋 Plan de Migración

Ver documentacion de iteracion: **[Iteracion 01 - Migracion Backend MVP](docs/iteracion-01-migracion-stack-y-arquitectura/README.md)**

### Resumen de Fases

#### 🔥 Fase 1: Baseline Actual (cerrada)

- Confirmar operación en MySQL + JWT local
- Auditar módulos actuales (`auth`, `user`, `ticket`)
- Consolidar configuración y seguridad vigente

#### 🟡 Fase 2: Migración de Base de Datos

- Introducir PostgreSQL 16 en entorno de migración
- Diseñar y ejecutar estrategia INT → UUID
- Adaptar mapeos JPA y scripts de inicialización

#### 🟢 Fase 3: Migración de Identidad

- Integrar validación de tokens de Supabase Auth
- Retirar emisión local de JWT del backend
- Desacoplar gestión de contraseñas de `module/user`

#### ⚡ Fase 4: Consolidación Modular

- Completar transición `user` → `account`
- Implementar módulos `support`, `product`, `notification`
- Alinear consumidores al contrato REST final

---

## 📊 Estado Actual del Código

### ⚠️ Situación Actual

Este repositorio contiene:

- Backend funcional con módulos `auth`, `user` y `ticket`
- Autenticación local activa (JWT generado por backend)
- Gestión de usuarios interna (tablas `user` y derivadas)
- Configuración MySQL activa en datasource y driver
- Script de base de datos MySQL para inicialización (`script/database/00-init.sql`)

**Necesita:**

- Planificar migración de MySQL a PostgreSQL sin romper operación
- Migrar identidad local hacia Supabase Auth por etapas
- Ejecutar transición `module/user` → `module/account`
- Definir implementación de `support`, `product` y `notification`
- Aumentar cobertura de tests de servicio/controlador

---

## 📚 Documentación Adicional

Este proyecto cuenta con documentación detallada para guiar el desarrollo:

### 📋 [Documentacion de Iteracion](docs/iteracion-01-migracion-stack-y-arquitectura/README.md)

Estado detallado del plan de migración modular, dividido en 4 fases con tareas ejecutables:

- **Fase 1:** Setup y configuración modular
- **Fase 2:** MVP - Módulos core
- **Fase 3:** Módulos complementarios
- **Fase 4:** Producción

### 🏗️ [ARCHITECTURE.md](ARCHITECTURE.md)

Guía completa de arquitectura modular:

- Principios de diseño y patrones
- Estructura detallada de módulos
- Comunicación entre módulos (interfaces y eventos)
- Convenciones de código y nomenclatura
- Estrategias de testing
- Ejemplos prácticos de implementación

### 📝 Otros Documentos

- **[script/database/00-init.sql](script/database/00-init.sql)** - Esquema MySQL actual (`users` + auth)
- **[script/database/10-ticket-pending.sql](script/database/10-ticket-pending.sql)** - Tablas de ticket, pendientes de migración

---

## 🎯 Próximos Pasos

### Para Continuar con el Desarrollo

1. **Leer documentación:**
   - ✅ Este README (overview general)
   - ✅ [Documentacion de iteracion](docs/iteracion-01-migracion-stack-y-arquitectura/README.md) (fases y estado actual)
   - ✅ [ARCHITECTURE.md](ARCHITECTURE.md) (patrones y estructura)

2. **Completar módulo Account:**

- Diseñar contrato de transición desde `module/user`
- Definir alcance funcional mínimo de `module/account`
- Preparar compatibilidad temporal durante migración

3. **Consolidar contrato de Ticket modular:**

- Estandarizar DTOs de ticket
- Implementar validaciones de negocio
- Testing completo

4. **Avanzar módulos MVP restantes y testing:**

- Tests unitarios de `AuthService`, `UserService` y `TicketService`
- Tests de integración sobre MySQL actual
- Tests E2E de endpoints críticos antes de iniciar migración de stack

Ver estado de tareas en [Documentacion de iteracion](docs/iteracion-01-migracion-stack-y-arquitectura/README.md).

---

## 🤝 Contribuir

### Reglas para Módulos

- ❌ NO imports directos entre módulos
- ✅ Compartir utilidades comunes en `shared/*`
- ✅ Definir contratos/eventos formales al avanzar a la arquitectura objetivo
- ✅ Testing > 80% coverage por módulo
- ✅ Documentar APIs con Swagger

Ver detalles completos en [ARCHITECTURE.md](ARCHITECTURE.md).

---

<div align="center">

**Hecho con ❤️ para la comunidad de usuarios**

[🗄️ Ver Schema DB](script/database/00-init.sql) · [📋 Documentacion](docs/iteracion-01-migracion-stack-y-arquitectura/README.md) · [🏗️ Arquitectura](ARCHITECTURE.md)

</div>
