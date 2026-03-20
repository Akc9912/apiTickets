# API Tickets - Documentación de Endpoints

## Índice
1. [Autenticación](#autenticación)
2. [Usuario - Perfil](#usuario---perfil)
3. [SuperAdmin](#superadmin)
4. [Admin](#admin)
5. [Developer](#developer)
6. [Support](#support)
7. [Tickets](#tickets)

---

## Autenticación

### 🔓 POST `/api/auth/login`
**Descripción:** Autentica un usuario y devuelve un token JWT.

**Acceso:** Público (no requiere autenticación)

**Request Body:**
```json
{
  "email": "admin@tickets.com",
  "password": "password123"
}
```

**Campos requeridos:**
- `email` (String): Email del usuario. Formato válido requerido.
- `password` (String): Contraseña del usuario.

**Respuestas:**
- `200 OK`: Login exitoso
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "email": "admin@tickets.com",
    "role": "ADMIN"
  }
  ```
- `401 Unauthorized`: Credenciales inválidas
- `400 Bad Request`: Datos inválidos

---

### 🔒 POST `/api/auth/change-password`
**Descripción:** Permite a un usuario cambiar su contraseña actual.

**Acceso:** Usuarios autenticados

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "userId": 1,
  "newPassword": "newPassword123"
}
```

**Campos requeridos:**
- `userId` (Integer): ID del usuario.
- `newPassword` (String): Nueva contraseña.

**Respuestas:**
- `200 OK`: Contraseña actualizada exitosamente
- `400 Bad Request`: Datos inválidos o contraseña actual incorrecta
- `404 Not Found`: Usuario no encontrado

---

### 🔒 POST `/api/auth/reset-password`
**Descripción:** Restablece la contraseña de un usuario (funcionalidad administrativa).

**Acceso:** ADMIN, SUPERADMIN

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "userId": 5
}
```

**Campos requeridos:**
- `userId` (Integer): ID del usuario cuya contraseña se restablecerá.

**Respuestas:**
- `200 OK`: Contraseña restablecida exitosamente
- `403 Forbidden`: Solo admin o superadmin pueden restablecer contraseñas
- `404 Not Found`: Usuario no encontrado
- `400 Bad Request`: Datos inválidos

---

## Usuario - Perfil

### 🔒 GET `/api/user/v1/profile`
**Descripción:** Obtiene el perfil del usuario autenticado.

**Acceso:** Usuarios autenticados

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuestas:**
- `200 OK`: Perfil del usuario
  ```json
  {
    "id": 1,
    "name": "John",
    "lastName": "Doe",
    "email": "john@tickets.com",
    "role": "DEVELOPER",
    "isActive": true,
    "isBlocked": false,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-02-01T14:20:00"
  }
  ```
- `401 Unauthorized`: No autenticado

---

### 🔒 PUT `/api/user/v1/profile`
**Descripción:** Actualiza el perfil del usuario autenticado.

**Acceso:** Usuarios autenticados

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "John",
  "lastName": "Doe",
  "email": "john.doe@tickets.com"
}
```

**Campos requeridos:**
- `name` (String): Nombre del usuario.
- `lastName` (String): Apellido del usuario.
- `email` (String): Email del usuario. Formato válido requerido.

**Respuestas:**
- `200 OK`: Perfil actualizado exitosamente
- `401 Unauthorized`: No autenticado
- `400 Bad Request`: Datos inválidos

---

## SuperAdmin

### 🔒 POST `/api/superadmin/v1/users`
**Descripción:** Crea un nuevo usuario con cualquier rol (SUPERADMIN, ADMIN, DEVELOPER, SUPPORT).

**Acceso:** SUPERADMIN únicamente

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "name": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@tickets.com",
  "role": "DEVELOPER"
}
```

**Campos requeridos:**
- `name` (String): Nombre del usuario.
- `lastName` (String): Apellido del usuario.
- `email` (String): Email del usuario. Formato válido requerido.
- `role` (String): Rol del usuario. Valores: SUPERADMIN, ADMIN, DEVELOPER, SUPPORT.

**Respuestas:**
- `201 Created`: Usuario creado exitosamente
- `400 Bad Request`: Datos inválidos o email duplicado

---

### 🔒 GET `/api/superadmin/v1/users`
**Descripción:** Obtiene la lista completa de usuarios del sistema.

**Acceso:** SUPERADMIN únicamente

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuestas:**
- `200 OK`: Lista de usuarios
  ```json
  [
    {
      "id": 1,
      "name": "John",
      "lastName": "Doe",
      "email": "john@tickets.com",
      "role": "DEVELOPER",
      "isActive": true,
      "isBlocked": false,
      "createdAt": "2026-01-15T10:30:00",
      "updatedAt": "2026-02-01T14:20:00"
    }
  ]
  ```

---

### 🔒 GET `/api/superadmin/v1/users/filter/role/{role}`
**Descripción:** Obtiene usuarios filtrados por rol.

**Acceso:** SUPERADMIN únicamente

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `role` (String): Rol a filtrar. Valores: ADMIN, DEVELOPER, SUPPORT, SUPERADMIN.

**Ejemplo:** `/api/superadmin/v1/users/filter/role/DEVELOPER`

**Respuestas:**
- `200 OK`: Lista de usuarios con el rol especificado

---

### 🔒 GET `/api/superadmin/v1/users/filter/status/active`
**Descripción:** Obtiene todos los usuarios con estado activo.

**Acceso:** SUPERADMIN únicamente

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuestas:**
- `200 OK`: Lista de usuarios activos

---

### 🔒 GET `/api/superadmin/v1/users/filter/status/blocked`
**Descripción:** Obtiene todos los usuarios con estado bloqueado.

**Acceso:** SUPERADMIN únicamente

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuestas:**
- `200 OK`: Lista de usuarios bloqueados

---

### 🔒 PUT `/api/superadmin/v1/users/{id}/status/activate`
**Descripción:** Activa un usuario específico.

**Acceso:** SUPERADMIN únicamente

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `id` (Integer): ID del usuario a activar.

**Respuestas:**
- `200 OK`: Usuario activado exitosamente
- `404 Not Found`: Usuario no encontrado

---

### 🔒 PUT `/api/superadmin/v1/users/{id}/status/deactivate`
**Descripción:** Desactiva un usuario específico.

**Acceso:** SUPERADMIN únicamente

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `id` (Integer): ID del usuario a desactivar.

**Respuestas:**
- `200 OK`: Usuario desactivado exitosamente
- `404 Not Found`: Usuario no encontrado

---

### 🔒 DELETE `/api/superadmin/v1/users/{id}`
**Descripción:** Elimina permanentemente un usuario del sistema.

**Acceso:** SUPERADMIN únicamente

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `id` (Integer): ID del usuario a eliminar.

**Respuestas:**
- `204 No Content`: Usuario eliminado exitosamente
- `404 Not Found`: Usuario no encontrado

---

## Admin

### 🔒 POST `/api/admin/v1/return-requests/{requestId}/process`
**Descripción:** Procesa (aprueba o rechaza) una solicitud de devolución de ticket de desarrolladores.

**Acceso:** ADMIN, SUPERADMIN

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `requestId` (Integer): ID de la solicitud de devolución.

**Request Body:**
```json
{
  "adminId": 1,
  "approve": true,
  "resolutionComment": "Ticket devuelto correctamente al pool de pendientes"
}
```

**Campos requeridos:**
- `adminId` (Integer): ID del admin que procesa la solicitud.
- `approve` (Boolean): true para aprobar, false para rechazar.

**Campos opcionales:**
- `resolutionComment` (String): Comentario de resolución (máx. 500 caracteres).

**Respuestas:**
- `200 OK`: Solicitud procesada exitosamente
- `404 Not Found`: Solicitud no encontrada
- `403 Forbidden`: No autorizado
- `400 Bad Request`: Datos inválidos

---

### 🔒 PUT `/api/admin/v1/users/{id}`
**Descripción:** Actualiza los datos de un usuario existente.

**Acceso:** ADMIN, SUPERADMIN

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `id` (Integer): ID del usuario a actualizar.

**Request Body:**
```json
{
  "name": "John",
  "lastName": "Doe",
  "email": "john.updated@tickets.com"
}
```

**Campos requeridos:**
- `name` (String): Nombre del usuario.
- `lastName` (String): Apellido del usuario.
- `email` (String): Email del usuario.

**Respuestas:**
- `200 OK`: Usuario actualizado exitosamente
- `404 Not Found`: Usuario no encontrado

---

### 🔒 PUT `/api/admin/v1/users/{id}/status/toggle-active`
**Descripción:** Activa o desactiva un usuario (toggle).

**Acceso:** ADMIN, SUPERADMIN

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `id` (Integer): ID del usuario.

**Respuestas:**
- `200 OK`: Estado del usuario actualizado exitosamente
- `404 Not Found`: Usuario no encontrado

---

### 🔒 PUT `/api/admin/v1/users/{id}/status/toggle-blocked`
**Descripción:** Bloquea o desbloquea un usuario (toggle).

**Acceso:** ADMIN, SUPERADMIN

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `id` (Integer): ID del usuario.

**Respuestas:**
- `200 OK`: Estado del usuario actualizado exitosamente
- `404 Not Found`: Usuario no encontrado

---

### 🔒 PUT `/api/admin/v1/users/{id}/role`
**Descripción:** Cambia el rol de un usuario existente (Admin no puede asignar rol SUPERADMIN).

**Acceso:** ADMIN, SUPERADMIN

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `id` (Integer): ID del usuario.

**Request Body:**
```json
{
  "role": "DEVELOPER"
}
```

**Campos requeridos:**
- `role` (String): Nuevo rol. Valores: ADMIN, DEVELOPER, SUPPORT (SUPERADMIN solo para superadmin).

**Respuestas:**
- `200 OK`: Rol actualizado exitosamente
- `404 Not Found`: Usuario no encontrado
- `403 Forbidden`: No autorizado para asignar este rol

---

### 🔒 GET `/api/admin/v1/users`
**Descripción:** Lista todos los usuarios del sistema (Admin no puede ver SuperAdmins).

**Acceso:** ADMIN, SUPERADMIN

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuestas:**
- `200 OK`: Lista de usuarios

---

### 🔒 GET `/api/admin/v1/users/{id}`
**Descripción:** Obtiene los datos de un usuario específico.

**Acceso:** ADMIN, SUPERADMIN

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `id` (Integer): ID del usuario.

**Respuestas:**
- `200 OK`: Datos del usuario
- `404 Not Found`: Usuario no encontrado

---

### 🔒 GET `/api/admin/v1/return-requests`
**Descripción:** Obtiene todas las solicitudes de devolución de tickets pendientes de desarrolladores.

**Acceso:** ADMIN, SUPERADMIN

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuestas:**
- `200 OK`: Lista de solicitudes de devolución pendientes
- `403 Forbidden`: No autorizado

---

## Developer

### 🔒 POST `/api/developer/v1/tickets/{ticketId}/take`
**Descripción:** Permite a un desarrollador tomar tickets pendientes o reabiertos.

**Acceso:** DEVELOPER

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `ticketId` (Integer): ID del ticket a tomar.

**Parámetros de query:**
- `developerId` (Integer): ID del desarrollador que toma el ticket.

**Ejemplo:** `/api/developer/v1/tickets/5/take?developerId=3`

**Respuestas:**
- `200 OK`: Ticket tomado exitosamente
- `404 Not Found`: Desarrollador o ticket no encontrado
- `400 Bad Request`: Ticket ya está asignado o no disponible
- `501 Not Implemented`: Operación no soportada

---

### 🔒 POST `/api/developer/v1/tickets/{ticketId}/resolve`
**Descripción:** Marca un ticket como resuelto por el desarrollador.

**Acceso:** DEVELOPER

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `ticketId` (Integer): ID del ticket a resolver.

**Parámetros de query:**
- `developerId` (Integer): ID del desarrollador.
- `comment` (String, opcional): Comentario de resolución.

**Ejemplo:** `/api/developer/v1/tickets/5/resolve?developerId=3&comment=Fixed bug in authentication`

**Respuestas:**
- `200 OK`: Ticket resuelto exitosamente
- `404 Not Found`: Ticket no encontrado
- `400 Bad Request`: Ticket no está asignado a este desarrollador
- `501 Not Implemented`: Operación no soportada

---

### 🔒 POST `/api/developer/v1/tickets/{ticketId}/return`
**Descripción:** Devuelve un ticket con una razón específica (crea solicitud de devolución).

**Acceso:** DEVELOPER

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `ticketId` (Integer): ID del ticket a devolver.

**Parámetros de query:**
- `developerId` (Integer): ID del desarrollador.
- `reason` (String): Razón para la devolución del ticket.

**Ejemplo:** `/api/developer/v1/tickets/5/return?developerId=3&reason=Requiere acceso a base de datos`

**Respuestas:**
- `200 OK`: Solicitud de devolución enviada exitosamente
- `404 Not Found`: Desarrollador o ticket no encontrado
- `400 Bad Request`: Ticket no está asignado a este desarrollador
- `501 Not Implemented`: Operación no soportada

---

## Support

### 🔒 POST `/api/support/v1/tickets/{ticketId}/evaluate`
**Descripción:** Validación final del support: acepta la solución (CLOSED) o la rechaza (REOPENED).

**Acceso:** SUPPORT

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `ticketId` (Integer): ID del ticket a evaluar.

**Parámetros de query:**
- `approve` (Boolean): true para cerrar, false para reabrir.
- `comment` (String, opcional): Comentario de evaluación.

**Ejemplo:** `/api/support/v1/tickets/5/evaluate?approve=true&comment=Solución verificada correctamente`

**Respuestas:**
- `200 OK`: Ticket evaluado exitosamente
- `404 Not Found`: Ticket no encontrado
- `403 Forbidden`: No autorizado
- `400 Bad Request`: Ticket no está en estado RESOLVED o no puede ser evaluado

---

## Tickets

### 🔒 GET `/api/tickets/v1/all`
**Descripción:** Retorna todos los tickets del sistema.

**Acceso:** ADMIN, SUPERADMIN

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuestas:**
- `200 OK`: Lista de todos los tickets
  ```json
  [
    {
      "id": 1,
      "tittle": "Unable to access dashboard",
      "description": "Getting 500 error when logging in",
      "status": "PENDING",
      "creatorId": 2,
      "assignedDeveloperId": null,
      "createdAt": "2026-01-20T09:15:00",
      "updatedAt": "2026-01-20T09:15:00"
    }
  ]
  ```
- `403 Forbidden`: No autorizado

---

### 🔒 GET `/api/tickets/v1/support/my-tickets`
**Descripción:** Retorna los tickets creados por el usuario support autenticado.

**Acceso:** SUPPORT

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuestas:**
- `200 OK`: Lista de tickets del support
- `403 Forbidden`: No autorizado

---

### 🔒 GET `/api/tickets/v1/support/tickets-to-evaluate`
**Descripción:** Retorna los tickets creados por el usuario support autenticado en estado RESOLVED.

**Acceso:** SUPPORT

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Respuestas:**
- `200 OK`: Lista de tickets en estado RESOLVED para evaluar
- `403 Forbidden`: No autorizado

---

### 🔒 POST `/api/tickets/v1/create`
**Descripción:** Permite crear un ticket según el rol del usuario autenticado.

**Acceso:** Usuarios autenticados (no bloqueados)

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Request Body:**
```json
{
  "tittle": "Unable to access user dashboard",
  "description": "When I try to log in to the dashboard, I get a 500 error",
  "creatorId": 2
}
```

**Campos requeridos:**
- `tittle` (String): Título del ticket.
- `description` (String): Descripción detallada del problema.
- `creatorId` (Integer): ID del usuario que crea el ticket.

**Respuestas:**
- `201 Created`: Ticket creado exitosamente
- `400 Bad Request`: Datos inválidos
- `403 Forbidden`: No autorizado o usuario bloqueado

---

### 🔒 POST `/api/tickets/v1/{id}/reopen`
**Descripción:** Permite reabrir un ticket según el rol del usuario autenticado.

**Acceso:** Usuarios autenticados (no bloqueados)

**Headers requeridos:**
```
Authorization: Bearer {token}
```

**Parámetros de ruta:**
- `id` (Integer): ID del ticket a reabrir.

**Parámetros de query:**
- `comment` (String): Comentario sobre la reapertura.

**Ejemplo:** `/api/tickets/v1/5/reopen?comment=El problema persiste después de la solución`

**Respuestas:**
- `200 OK`: Ticket reabierto exitosamente
- `404 Not Found`: Ticket no encontrado
- `403 Forbidden`: No autorizado o usuario bloqueado
- `400 Bad Request`: Ticket no puede ser reabierto

---

## Notas Generales

### Estados de Tickets
- `PENDING`: Ticket pendiente de ser tomado por un desarrollador
- `IN_PROGRESS`: Ticket siendo trabajado por un desarrollador
- `RESOLVED`: Ticket resuelto, esperando validación de support
- `CLOSED`: Ticket cerrado, solución validada
- `REOPENED`: Ticket reabierto después de revisión

### Roles del Sistema
- `SUPERADMIN`: Acceso completo al sistema, puede crear usuarios con cualquier rol
- `ADMIN`: Gestión de usuarios (excepto SuperAdmin), procesamiento de solicitudes de devolución
- `DEVELOPER`: Tomar, resolver y devolver tickets
- `SUPPORT`: Crear tickets y evaluar soluciones

### Autenticación
Todos los endpoints excepto `/api/auth/login` requieren un token JWT válido en el header:
```
Authorization: Bearer {token}
```

El token se obtiene al hacer login y debe incluirse en todas las solicitudes protegidas.

### Códigos de Respuesta HTTP
- `200 OK`: Operación exitosa
- `201 Created`: Recurso creado exitosamente
- `204 No Content`: Operación exitosa sin contenido de retorno
- `400 Bad Request`: Datos inválidos o solicitud incorrecta
- `401 Unauthorized`: No autenticado
- `403 Forbidden`: No autorizado (autenticado pero sin permisos)
- `404 Not Found`: Recurso no encontrado
- `500 Internal Server Error`: Error interno del servidor
- `501 Not Implemented`: Funcionalidad no implementada
