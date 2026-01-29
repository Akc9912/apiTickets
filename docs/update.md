# 🏗️ Plan de Refactorización: Arquitectura Modular

## 📋 Estado Actual del Proyecto

### Estructura Actual

```
com.poo.miapi/
├── config/                    # Configuraciones globales
├── controller/
│   ├── auth/                  # Autenticación
│   ├── core/                  # Controladores principales (6 archivos)
│   ├── estadistica/           # Estadísticas
│   ├── historial/             # Auditoría
│   └── notificacion/          # Notificaciones
├── dto/
│   ├── auth/
│   ├── estadistica/
│   ├── historial/
│   ├── notificacion/
│   ├── tecnico/
│   ├── ticket/
│   ├── trabajador/
│   └── usuario/
├── model/
│   ├── core/                  # Entidades principales (6 archivos)
│   ├── enums/
│   ├── historial/
│   └── notificacion/
├── repository/
│   ├── core/                  # Repositorios principales (6 archivos)
│   ├── historial/
│   └── notificacion/
├── service/
│   ├── auth/
│   ├── core/                  # Servicios principales (6 archivos)
│   ├── estadistica/
│   ├── historial/
│   ├── notificacion/
│   └── security/
├── security/                  # Filtros JWT
├── util/                      # Utilidades
└── exception/                 # Manejo de excepciones
```

### Problemas Identificados

1. ❌ **Mezcla de organización**: Por capas (controller/service) Y por módulos (auth/core/estadistica)
2. ❌ **Subcarpetas inconsistentes**: "core" agrupa entidades del dominio, pero otras están dispersas
3. ❌ **DTOs separados por entidad**: Dificulta el cambio de módulos
4. ❌ **Baja cohesión**: Un cambio en "Ticket" requiere editar 4 carpetas diferentes
5. ❌ **Difícil escalabilidad**: Agregar un módulo requiere crear subcarpetas en 5 lugares

---

## 🎯 Arquitectura Objetivo: Modular por Dominio

### Principios Aplicados

- ✅ **Alta cohesión**: Todo lo relacionado con un módulo está junto
- ✅ **Bajo acoplamiento**: Los módulos se comunican por interfaces
- ✅ **Separación de responsabilidades**: Cada módulo es independiente
- ✅ **Escalabilidad**: Fácil agregar/quitar módulos

### Nueva Estructura Propuesta

```
com.poo.miapi/
├── shared/                          # 🔧 Infraestructura compartida
│   ├── config/                      # Configuraciones (Security, OpenAPI, etc.)
│   ├── security/                    # JWT, Filters, UserDetails
│   ├── exception/                   # GlobalExceptionHandler
│   └── util/                        # Helpers genéricos
│
├── module/                          # 📦 MÓDULOS DE NEGOCIO
│   ├── auth/                        # 🔐 Autenticación y autorización
│   │   ├── controller/
│   │   │   └── AuthController.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   └── JwtService.java
│   │   └── dto/
│   │       ├── LoginRequestDto.java
│   │       ├── LoginResponseDto.java
│   │       └── ChangePasswordDto.java
│   │
│   ├── user/                        # 👥 Gestión de usuarios
│   │   ├── controller/
│   │   │   ├── SuperAdminController.java
│   │   │   ├── AdminController.java
│   │   │   ├── TecnicoController.java
│   │   │   ├── TrabajadorController.java
│   │   │   └── UsuarioController.java
│   │   ├── service/
│   │   │   ├── SuperAdminService.java
│   │   │   ├── AdminService.java
│   │   │   ├── TecnicoService.java
│   │   │   ├── TrabajadorService.java
│   │   │   └── UsuarioService.java
│   │   ├── repository/
│   │   │   ├── SuperAdminRepository.java
│   │   │   ├── AdminRepository.java
│   │   │   ├── TecnicoRepository.java
│   │   │   ├── TrabajadorRepository.java
│   │   │   └── UsuarioRepository.java
│   │   ├── model/
│   │   │   ├── SuperAdmin.java
│   │   │   ├── Admin.java
│   │   │   ├── Tecnico.java
│   │   │   ├── Trabajador.java
│   │   │   ├── Usuario.java
│   │   │   └── Rol.java (enum)
│   │   └── dto/
│   │       ├── UsuarioRequestDto.java
│   │       ├── UsuarioResponseDto.java
│   │       ├── TecnicoResponseDto.java
│   │       ├── TrabajadorResponseDto.java
│   │       └── ...
│   │
│   ├── ticket/                      # 🎫 Gestión de tickets
│   │   ├── controller/
│   │   │   └── TicketController.java
│   │   ├── service/
│   │   │   └── TicketService.java
│   │   ├── repository/
│   │   │   └── TicketRepository.java
│   │   ├── model/
│   │   │   ├── Ticket.java
│   │   │   └── EstadoTicket.java (enum)
│   │   └── dto/
│   │       ├── TicketRequestDto.java
│   │       ├── TicketResponseDto.java
│   │       ├── EvaluarTicketDto.java
│   │       └── EstadoTicketResponseDto.java
│   │
│   ├── notification/                # 📧 Sistema de notificaciones
│   │   ├── controller/
│   │   │   └── NotificacionController.java
│   │   ├── service/
│   │   │   └── NotificacionService.java
│   │   ├── repository/
│   │   │   └── NotificacionRepository.java
│   │   ├── model/
│   │   │   └── Notificacion.java
│   │   └── dto/
│   │       └── NotificacionResponseDto.java
│   │
│   ├── audit/                       # 📊 Auditoría e historial
│   │   ├── controller/
│   │   │   └── AuditoriaController.java
│   │   ├── service/
│   │   │   ├── AuditoriaService.java
│   │   │   ├── TecnicoPorTicketService.java
│   │   │   ├── IncidenteTecnicoService.java
│   │   │   └── HistorialValidacionService.java
│   │   ├── repository/
│   │   │   ├── AuditoriaRepository.java
│   │   │   ├── TecnicoPorTicketRepository.java
│   │   │   ├── IncidenteTecnicoRepository.java
│   │   │   └── HistorialValidacionRepository.java
│   │   ├── model/
│   │   │   ├── Auditoria.java
│   │   │   ├── TecnicoPorTicket.java
│   │   │   ├── IncidenteTecnico.java
│   │   │   └── HistorialValidacionTrabajador.java
│   │   └── dto/
│   │       ├── TecnicoPorTicketResponseDto.java
│   │       ├── IncidenteTecnicoRequestDto.java
│   │       ├── IncidenteTecnicoResponseDto.java
│   │       ├── HistorialValidacionRequestDto.java
│   │       └── HistorialValidacionResponseDto.java
│   │
│   └── statistics/                  # 📈 Estadísticas y reportes
│       ├── controller/
│       │   └── EstadisticaController.java
│       ├── service/
│       │   └── EstadisticaService.java
│       └── dto/
│           └── EstadisticaDto.java
│
└── MiapiApplication.java           # Main class
```

---

## 📝 Plan de Migración (Paso a Paso)

### **FASE 0: Preparación** ⚙️

**Objetivo**: Asegurar que todo está bajo control de versiones y funcionando

- [ ] **0.1** - Hacer commit de todos los cambios actuales
- [ ] **0.2** - Crear rama de refactorización: `git checkout -b refactor/modular-architecture`
- [ ] **0.3** - Ejecutar tests actuales y verificar que todo funciona
- [ ] **0.4** - Documentar endpoints críticos en uso (para validación posterior)

---

### **FASE 1: Crear Estructura Base** 📁

**Objetivo**: Crear los paquetes sin mover código aún

#### 1.1 - Crear paquete `shared` (infraestructura compartida)

```bash
# Crear estructura shared
mkdir -p src/main/java/com/poo/miapi/shared/config
mkdir -p src/main/java/com/poo/miapi/shared/security
mkdir -p src/main/java/com/poo/miapi/shared/exception
mkdir -p src/main/java/com/poo/miapi/shared/util
```

#### 1.2 - Crear módulos de negocio

```bash
# Estructura de módulos
mkdir -p src/main/java/com/poo/miapi/module/auth/{controller,service,dto}
mkdir -p src/main/java/com/poo/miapi/module/user/{controller,service,repository,model,dto}
mkdir -p src/main/java/com/poo/miapi/module/ticket/{controller,service,repository,model,dto}
mkdir -p src/main/java/com/poo/miapi/module/notification/{controller,service,repository,model,dto}
mkdir -p src/main/java/com/poo/miapi/module/audit/{controller,service,repository,model,dto}
mkdir -p src/main/java/com/poo/miapi/module/statistics/{controller,service,dto}
```

**Verificación**: Confirmar que las carpetas existen con `tree src/main/java/com/poo/miapi`

---

### **FASE 2: Migrar `shared` (Infraestructura)** 🔧

**Objetivo**: Mover configuraciones y utilidades comunes primero

#### 2.1 - Mover configuraciones

```bash
# Mover archivos de config/
git mv src/main/java/com/poo/miapi/config/* src/main/java/com/poo/miapi/shared/config/
```

**Archivos afectados**:

- `DataInitializer.java` → `shared/config/`
- `OpenApiConfig.java` → `shared/config/`
- `SecurityConfig.java` → `shared/config/`

**Cambios necesarios**:

- Actualizar `package` de:
  ```java
  package com.poo.miapi.config;
  ```
  a:
  ```java
  package com.poo.miapi.shared.config;
  ```

#### 2.2 - Mover seguridad

```bash
# Mover archivos de security/
git mv src/main/java/com/poo/miapi/security/* src/main/java/com/poo/miapi/shared/security/
```

**Archivos afectados**:

- `JwtAuthenticationFilter.java` → `shared/security/`

**Cambios**: Actualizar package a `com.poo.miapi.shared.security`

#### 2.3 - Mover excepciones

```bash
git mv src/main/java/com/poo/miapi/exception/* src/main/java/com/poo/miapi/shared/exception/
```

**Archivos afectados**:

- `GlobalExceptionHandler.java` → `shared/exception/`

#### 2.4 - Mover utilidades

```bash
git mv src/main/java/com/poo/miapi/util/* src/main/java/com/poo/miapi/shared/util/
```

**Verificación**: Compilar proyecto `./mvnw clean compile` (habrá errores, es esperado)

---

### **FASE 3: Migrar Módulo AUTH** 🔐

**Objetivo**: Migrar el módulo más simple primero (sin entidades propias)

#### 3.1 - Mover servicios de autenticación

```bash
# Mover AuthService.java
git mv src/main/java/com/poo/miapi/service/auth/AuthService.java \
  src/main/java/com/poo/miapi/module/auth/service/

# Mover JwtService.java (desde service/security/)
git mv src/main/java/com/poo/miapi/service/security/JwtService.java \
  src/main/java/com/poo/miapi/module/auth/service/
```

**Cambios**:

- Actualizar package a `com.poo.miapi.module.auth.service`
- Actualizar imports en ambos archivos

#### 3.2 - Mover controlador

```bash
git mv src/main/java/com/poo/miapi/controller/auth/AuthController.java \
  src/main/java/com/poo/miapi/module/auth/controller/
```

**Cambios**:

- Actualizar package a `com.poo.miapi.module.auth.controller`
- Actualizar imports de servicios

#### 3.3 - Mover DTOs de autenticación

```bash
git mv src/main/java/com/poo/miapi/dto/auth/* \
  src/main/java/com/poo/miapi/module/auth/dto/
```

**Archivos**:

- `LoginRequestDto.java`
- `LoginResponseDto.java`
- `ChangePasswordDto.java`

**Cambios**: Package a `com.poo.miapi.module.auth.dto`

**Verificación**: Buscar imports rotos de auth: `grep -r "com.poo.miapi.service.auth" src/`

---

### **FASE 4: Migrar Módulo USER** 👥

**Objetivo**: Consolidar toda la gestión de usuarios

#### 4.1 - Mover modelos (entidades)

```bash
# Mover entidades de usuarios desde model/core/
git mv src/main/java/com/poo/miapi/model/core/Usuario.java \
  src/main/java/com/poo/miapi/module/user/model/
git mv src/main/java/com/poo/miapi/model/core/SuperAdmin.java \
  src/main/java/com/poo/miapi/module/user/model/
git mv src/main/java/com/poo/miapi/model/core/Admin.java \
  src/main/java/com/poo/miapi/module/user/model/
git mv src/main/java/com/poo/miapi/model/core/Tecnico.java \
  src/main/java/com/poo/miapi/module/user/model/
git mv src/main/java/com/poo/miapi/model/core/Trabajador.java \
  src/main/java/com/poo/miapi/module/user/model/

# Mover enum Rol
git mv src/main/java/com/poo/miapi/model/enums/Rol.java \
  src/main/java/com/poo/miapi/module/user/model/
```

**Cambios**: Package a `com.poo.miapi.module.user.model`

#### 4.2 - Mover repositorios

```bash
git mv src/main/java/com/poo/miapi/repository/core/*Repository.java \
  src/main/java/com/poo/miapi/module/user/repository/
# (excepto TicketRepository.java)
```

**Archivos**:

- `UsuarioRepository.java`
- `SuperAdminRepository.java`
- `AdminRepository.java`
- `TecnicoRepository.java`
- `TrabajadorRepository.java`

**Cambios**: Package a `com.poo.miapi.module.user.repository`

#### 4.3 - Mover servicios

```bash
git mv src/main/java/com/poo/miapi/service/core/UsuarioService.java \
  src/main/java/com/poo/miapi/module/user/service/
# Repetir para SuperAdmin, Admin, Tecnico, Trabajador
```

**Cambios**: Package a `com.poo.miapi.module.user.service`

#### 4.4 - Mover controladores

```bash
git mv src/main/java/com/poo/miapi/controller/core/UsuarioController.java \
  src/main/java/com/poo/miapi/module/user/controller/
# Repetir para SuperAdmin, Admin, Tecnico, Trabajador
```

**Cambios**: Package a `com.poo.miapi.module.user.controller`

#### 4.5 - Mover DTOs

```bash
# Consolidar DTOs de usuarios
git mv src/main/java/com/poo/miapi/dto/usuario/* \
  src/main/java/com/poo/miapi/module/user/dto/
git mv src/main/java/com/poo/miapi/dto/tecnico/* \
  src/main/java/com/poo/miapi/module/user/dto/
git mv src/main/java/com/poo/miapi/dto/trabajador/* \
  src/main/java/com/poo/miapi/module/user/dto/
```

**Cambios**: Package a `com.poo.miapi.module.user.dto`

**Verificación**: Compilar módulo user: buscar errores relacionados con imports

---

### **FASE 5: Migrar Módulo TICKET** 🎫

**Objetivo**: Aislar toda la lógica de tickets

#### 5.1 - Mover modelo

```bash
git mv src/main/java/com/poo/miapi/model/core/Ticket.java \
  src/main/java/com/poo/miapi/module/ticket/model/
git mv src/main/java/com/poo/miapi/model/enums/EstadoTicket.java \
  src/main/java/com/poo/miapi/module/ticket/model/
```

**Cambios**: Package a `com.poo.miapi.module.ticket.model`

#### 5.2 - Mover repositorio

```bash
git mv src/main/java/com/poo/miapi/repository/core/TicketRepository.java \
  src/main/java/com/poo/miapi/module/ticket/repository/
```

#### 5.3 - Mover servicio

```bash
git mv src/main/java/com/poo/miapi/service/core/TicketService.java \
  src/main/java/com/poo/miapi/module/ticket/service/
```

#### 5.4 - Mover controlador

```bash
git mv src/main/java/com/poo/miapi/controller/core/TicketController.java \
  src/main/java/com/poo/miapi/module/ticket/controller/
```

#### 5.5 - Mover DTOs

```bash
git mv src/main/java/com/poo/miapi/dto/ticket/* \
  src/main/java/com/poo/miapi/module/ticket/dto/
```

---

### **FASE 6: Migrar Módulo NOTIFICATION** 📧

#### 6.1 - Mover modelo

```bash
git mv src/main/java/com/poo/miapi/model/notificacion/Notificacion.java \
  src/main/java/com/poo/miapi/module/notification/model/
```

#### 6.2 - Mover repositorio

```bash
git mv src/main/java/com/poo/miapi/repository/notificacion/NotificacionRepository.java \
  src/main/java/com/poo/miapi/module/notification/repository/
```

#### 6.3 - Mover servicio

```bash
git mv src/main/java/com/poo/miapi/service/notificacion/NotificacionService.java \
  src/main/java/com/poo/miapi/module/notification/service/
```

#### 6.4 - Mover controlador y DTOs

```bash
git mv src/main/java/com/poo/miapi/controller/notificacion/* \
  src/main/java/com/poo/miapi/module/notification/controller/
git mv src/main/java/com/poo/miapi/dto/notificacion/* \
  src/main/java/com/poo/miapi/module/notification/dto/
```

---

### **FASE 7: Migrar Módulo AUDIT** 📊

#### 7.1 - Mover modelos

```bash
git mv src/main/java/com/poo/miapi/model/historial/* \
  src/main/java/com/poo/miapi/module/audit/model/
```

**Archivos**:

- `Auditoria.java`
- `TecnicoPorTicket.java`
- `IncidenteTecnico.java`
- `HistorialValidacionTrabajador.java`

#### 7.2 - Mover repositorios

```bash
git mv src/main/java/com/poo/miapi/repository/historial/* \
  src/main/java/com/poo/miapi/module/audit/repository/
```

#### 7.3 - Mover servicios

```bash
git mv src/main/java/com/poo/miapi/service/historial/* \
  src/main/java/com/poo/miapi/module/audit/service/
```

#### 7.4 - Mover controlador y DTOs

```bash
git mv src/main/java/com/poo/miapi/controller/historial/* \
  src/main/java/com/poo/miapi/module/audit/controller/
git mv src/main/java/com/poo/miapi/dto/historial/* \
  src/main/java/com/poo/miapi/module/audit/dto/
```

---

### **FASE 8: Migrar Módulo STATISTICS** 📈

#### 8.1 - Mover servicio

```bash
git mv src/main/java/com/poo/miapi/service/estadistica/EstadisticaService.java \
  src/main/java/com/poo/miapi/module/statistics/service/
```

#### 8.2 - Mover controlador y DTOs

```bash
git mv src/main/java/com/poo/miapi/controller/estadistica/EstadisticaController.java \
  src/main/java/com/poo/miapi/module/statistics/controller/
git mv src/main/java/com/poo/miapi/dto/estadistica/* \
  src/main/java/com/poo/miapi/module/statistics/dto/
```

---

### **FASE 9: Migrar CustomUserDetailsService** 🔐

**Ubicación especial**: Este servicio usa entidades de User pero es parte de Auth

```bash
git mv src/main/java/com/poo/miapi/service/security/CustomUserDetailsService.java \
  src/main/java/com/poo/miapi/module/auth/service/
```

**Cambios**:

- Package a `com.poo.miapi.module.auth.service`
- Imports correctos de `module.user.model.Usuario`
- Imports de `module.user.repository.UsuarioRepository`

---

### **FASE 10: Limpieza y Validación** ✅

#### 10.1 - Eliminar carpetas antiguas vacías

```bash
# Verificar que están vacías primero
ls -la src/main/java/com/poo/miapi/config
ls -la src/main/java/com/poo/miapi/controller
ls -la src/main/java/com/poo/miapi/dto
ls -la src/main/java/com/poo/miapi/model
ls -la src/main/java/com/poo/miapi/repository
ls -la src/main/java/com/poo/miapi/service
ls -la src/main/java/com/poo/miapi/security
ls -la src/main/java/com/poo/miapi/util
ls -la src/main/java/com/poo/miapi/exception

# Si están vacías, eliminar
rm -rf src/main/java/com/poo/miapi/config
rm -rf src/main/java/com/poo/miapi/controller
rm -rf src/main/java/com/poo/miapi/dto
rm -rf src/main/java/com/poo/miapi/model
rm -rf src/main/java/com/poo/miapi/repository
rm -rf src/main/java/com/poo/miapi/service
rm -rf src/main/java/com/poo/miapi/security
rm -rf src/main/java/com/poo/miapi/util
rm -rf src/main/java/com/poo/miapi/exception
```

#### 10.2 - Compilar proyecto completo

```bash
./mvnw clean compile
```

**Solucionar errores**:

- Buscar imports no actualizados: `grep -r "com.poo.miapi.controller.core" src/`
- Buscar imports no actualizados: `grep -r "com.poo.miapi.service.core" src/`
- Ejecutar búsqueda global por packages antiguos

#### 10.3 - Ejecutar tests

```bash
./mvnw test
```

#### 10.4 - Levantar aplicación y verificar endpoints

```bash
./mvnw spring-boot:run
```

**Validar**:

- Swagger UI funciona: http://localhost:8080/swagger-ui/index.html
- Login funcional: POST /api/auth/login
- Endpoints críticos operativos

#### 10.5 - Actualizar documentación

- Actualizar `TECHNICAL_DOCS.md` con la nueva estructura
- Actualizar diagramas de arquitectura (si existen)
- Documentar packages en README

---

## 🎯 Beneficios de la Nueva Arquitectura

### ✅ **Alta Cohesión Modular**

- Todo lo de "usuarios" está en `module/user/`
- Todo lo de "tickets" está en `module/ticket/`
- Cambiar un módulo no afecta a otros

### ✅ **Facilita el Testing**

- Cada módulo puede testearse independientemente
- Mocks claros entre módulos

### ✅ **Escalabilidad**

- Agregar un módulo nuevo:
  ```bash
  mkdir -p module/nuevo/{controller,service,repository,model,dto}
  ```
- No requiere tocar estructura existente

### ✅ **Onboarding Rápido**

- Desarrolladores nuevos entienden rápido: "trabajo en el módulo ticket"
- No necesitan navegar entre 5 carpetas para un cambio

### ✅ **Potencial Microservicios**

- Cada módulo puede convertirse en microservicio fácilmente
- Dependencias claras entre módulos

---

## 📦 Estructura Final (Compacta)

```
com.poo.miapi/
├── shared/              # Infraestructura (config, security, exceptions, utils)
├── module/
│   ├── auth/            # 🔐 Autenticación (controller, service, dto)
│   ├── user/            # 👥 Usuarios (controller, service, repository, model, dto)
│   ├── ticket/          # 🎫 Tickets (controller, service, repository, model, dto)
│   ├── notification/    # 📧 Notificaciones (controller, service, repository, model, dto)
│   ├── audit/           # 📊 Auditoría (controller, service, repository, model, dto)
│   └── statistics/      # 📈 Estadísticas (controller, service, dto)
└── MiapiApplication.java
```

**Sin subcarpetas innecesarias**: Solo `controller/`, `service/`, `repository/`, `model/`, `dto/` dentro de cada módulo.

---

## 🚨 Checklist Final

- [ ] Todos los packages actualizados
- [ ] Compilación sin errores: `./mvnw clean compile`
- [ ] Tests pasando: `./mvnw test`
- [ ] Aplicación levanta correctamente
- [ ] Swagger UI accesible y funcional
- [ ] Endpoints críticos validados
- [ ] Documentación actualizada
- [ ] Commit y merge a main

---

## 📚 Referencias

- **Arquitectura Hexagonal**: Similar separación entre dominio y infraestructura
- **Domain-Driven Design (DDD)**: Módulos = Bounded Contexts
- **Spring Boot Modular**: https://spring.io/projects/spring-modulith

---

**Fecha de creación**: 2026-01-29  
**Estado**: 📋 Pendiente de implementación
