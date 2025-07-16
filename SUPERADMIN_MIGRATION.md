# Implementación SuperAdmin - Sistema de Tickets

## 📋 Resumen de Cambios

Se ha implementado un nuevo tipo de usuario **SuperAdmin** que actúa como el "dueño" del sistema, mientras que **Admin** pasa a ser un usuario más del sistema con permisos administrativos limitados.

## 🗃️ Base de Datos

### Archivo Principal

- **Ubicación**: `init_ticket_system.sql`
- **Estado**: ✅ **Listo para primer despliegue**
- **Incluye**: Estructura completa con soporte SuperAdmin integrado

### Credenciales por Defecto del SuperAdmin

```
Email: superadmin@sistema.com
Password: secret
Rol: SUPER_ADMIN
```

⚠️ **IMPORTANTE**: Cambiar la contraseña después del primer login

### Instrucciones de Despliegue

```sql
-- 1. Crear la base de datos
CREATE DATABASE apiticket;

-- 2. Ejecutar script completo
source init_ticket_system.sql;

-- 3. Verificar instalación
SELECT rol, COUNT(*) as cantidad FROM usuario GROUP BY rol;
```

## 🔧 Cambios Técnicos Implementados

### 1. **Nuevas Clases Creadas**

#### SuperAdmin.java

- **Ubicación**: `src/main/java/com/poo/miapi/model/core/SuperAdmin.java`
- **Propósito**: Entidad JPA que representa el usuario con máximos privilegios del sistema
- **Características**: Extiende de Usuario usando herencia de tabla única

#### SuperAdminController.java

- **Ubicación**: `src/main/java/com/poo/miapi/controller/core/SuperAdminController.java`
- **Endpoints**: `/api/superadmin/**`
- **Funcionalidades**:
  - Gestión completa de usuarios (CRUD)
  - Promoción y degradación de administradores
  - Gestión de tickets a nivel sistema
  - Estadísticas completas del sistema

#### SuperAdminService.java

- **Ubicación**: `src/main/java/com/poo/miapi/service/core/SuperAdminService.java`
- **Funcionalidades**:
  - Operaciones CRUD para todos los tipos de usuario
  - Validaciones de seguridad (no eliminar último SuperAdmin)
  - Gestión de roles y permisos
  - Estadísticas avanzadas del sistema

### 2. **Actualizaciones en Clases Existentes**

#### UserRole.java

- **Nuevo rol**: `SUPER_ADMIN = "SUPER_ADMIN"`
- **Nuevos métodos**:
  - `isSuperAdmin(String role)`: Verifica si es SuperAdmin
  - `isAdminRole(String role)`: Verifica si tiene permisos administrativos

#### SecurityConfig.java

- **Nueva configuración de seguridad**:
  ```java
  .requestMatchers("/api/superadmin/**").hasRole("SUPER_ADMIN")
  .requestMatchers("/api/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
  ```

#### Repositorios Actualizados

- **UsuarioRepository**: Agregados métodos para conteos y filtros por rol
- **TicketRepository**: Agregados métodos para estadísticas

### 3. **Correcciones de Código Aplicadas**

- ✅ Eliminadas anotaciones `@Autowired` redundantes en constructores
- ✅ Estandarizados tipos de ID de `int` a `Long`
- ✅ Corregidas rutas de seguridad de `/auth/**` a `/api/auth/**`
- ✅ Limpiados imports no utilizados
- ✅ Implementada inyección de dependencias por constructor

## 🗃️ Migración de Base de Datos

### Archivo de Migración

- **Ubicación**: `migration_superadmin.sql`
- **Ejecutar después de**: `init_ticket_system.sql`

### Cambios en la Base de Datos

1. **Nueva tabla**: `super_admin`
2. **Cambio de columna**: `tipo_usuario` → `rol`
3. **Nuevos índices**: Para optimización de consultas por rol
4. **Datos por defecto**: Usuario SuperAdmin inicial

### Instrucciones de Migración

```sql
-- 1. Ejecutar script de migración
source migration_superadmin.sql;

-- 2. Verificar migración exitosa
SELECT rol, COUNT(*) FROM usuario GROUP BY rol;
```

## 🔐 Jerarquía de Usuarios

### SuperAdmin (Dueño del Sistema)

- **Permisos**: Acceso total al sistema
- **Restricciones**: No puede ser bloqueado, siempre debe existir al menos uno activo
- **Funcionalidades exclusivas**:
  - Crear/eliminar otros SuperAdmins
  - Promover/degradar Administradores
  - Ver estadísticas completas del sistema
  - Reabrir tickets cerrados
  - Eliminar tickets del sistema

### Admin (Administrador Regular)

- **Permisos**: Gestión de usuarios regulares y tickets
- **Restricciones**: No puede gestionar SuperAdmins
- **Funcionalidades**:
  - Gestionar Técnicos y Trabajadores
  - Asignar tickets a técnicos
  - Ver estadísticas limitadas

## 📊 Endpoints del SuperAdmin

### Gestión de Usuarios

- `GET /api/superadmin/usuarios` - Listar todos los usuarios
- `POST /api/superadmin/usuarios` - Crear nuevo usuario
- `GET /api/superadmin/usuarios/{id}` - Ver usuario específico
- `PUT /api/superadmin/usuarios/{id}` - Editar usuario
- `DELETE /api/superadmin/usuarios/{id}` - Eliminar usuario
- `PUT /api/superadmin/usuarios/{id}/activar` - Activar usuario
- `PUT /api/superadmin/usuarios/{id}/desactivar` - Desactivar usuario
- `PUT /api/superadmin/usuarios/{id}/bloquear` - Bloquear usuario
- `PUT /api/superadmin/usuarios/{id}/desbloquear` - Desbloquear usuario
- `PUT /api/superadmin/usuarios/{id}/resetear-password` - Resetear contraseña

### Gestión de Administradores

- `GET /api/superadmin/administradores` - Listar administradores
- `PUT /api/superadmin/usuarios/{id}/promover-admin` - Promover a Admin
- `PUT /api/superadmin/usuarios/{id}/degradar-admin` - Degradar Admin

### Gestión del Sistema

- `GET /api/superadmin/tickets` - Listar todos los tickets
- `PUT /api/superadmin/tickets/{id}/reabrir` - Reabrir ticket
- `DELETE /api/superadmin/tickets/{id}` - Eliminar ticket

### Estadísticas

- `GET /api/superadmin/estadisticas/usuarios` - Estadísticas de usuarios
- `GET /api/superadmin/estadisticas/tickets` - Estadísticas de tickets
- `GET /api/superadmin/estadisticas/sistema` - Estadísticas completas

## 🛡️ Consideraciones de Seguridad

### Validaciones Implementadas

1. **Protección del último SuperAdmin**: No se puede eliminar o desactivar si es el único
2. **SuperAdmins no bloqueables**: Los SuperAdmins no pueden ser bloqueados
3. **Verificación de roles**: Validación estricta en cambios de rol
4. **Autenticación JWT**: Los endpoints requieren token válido con rol apropiado

### Recomendaciones

- Cambiar la contraseña por defecto del SuperAdmin inicial
- Mantener al menos 2 SuperAdmins activos en producción
- Monitorear los logs de acceso a endpoints de SuperAdmin

## 🚀 Próximos Pasos

1. **Ejecutar migración de base de datos**
2. **Probar endpoints con usuario SuperAdmin**
3. **Verificar restricciones de seguridad**
4. **Documentar credenciales del SuperAdmin inicial**

## 📝 Notas Adicionales

- Todos los cambios son retrocompatibles
- La funcionalidad existente de Admin no se ve afectada
- Se mantiene la estructura de herencia JPA existente
- Los DTOs existentes funcionan sin cambios
