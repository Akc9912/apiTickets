# API Tickets - Documentación de Endpoints

## Índice
1. [Autenticación](#autenticación)
2. [Usuarios](#usuarios)
3. [Admin - Solicitudes de Devolución](#admin---solicitudes-de-devolución)
4. [Developer](#developer)
5. [Support](#support)
6. [Tickets](#tickets)

> ⚠️ **Estado de esta documentación.** La sección **Usuarios** refleja el código actual
> (módulo `users` refactorizado: entidad única con rol global, ids UUID).
> Las secciones **Admin - Solicitudes de Devolución**, **Developer**, **Support** y
> **Tickets** describen el esquema legacy (ids enteros, roles `DEVELOPER`/`SUPPORT` que ya
> no existen en `UserRole`) y **todavía no compilan**: quedan pendientes de migración.
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

## Usuarios

Un solo controller (`UserController`) sirve dos árboles: perfil propio y administración.

> 🚫 **El alta de usuarios no tiene endpoint.** El registro vive en `module/auth`
> (`AuthService.register()`), único llamador de `UserApi.create()`. Después del alta la
> cuenta queda en `PENDING_VERIFICATION` y se valida por mail — no hay login automático.

> ⚠️ **Todos estos endpoints responden `403` hoy.** Las reglas usan `hasAnyRole`, que exige
> los authorities `ROLE_USER` / `ROLE_ADMIN` / `ROLE_SUPERADMIN`. Esos authorities salen de
> `UserDetails.getAuthorities()`, y `User` todavía no implementa `UserDetails`. Falla
> cerrado a propósito. Al conectarlo, `getAuthorities()` debe devolver `ROLE_ADMIN`, **con
> el prefijo** — `ADMIN` pelado no alcanza.

### Representación de usuario

Todas las respuestas que devuelven un usuario usan esta forma (`UserResponse`). Nunca
incluye `passwordHash`.

```json
{
  "id": "1639a043-2257-4d4b-87b2-6a6d4ff593c5",
  "firstName": "Ada",
  "lastName": "Lovelace",
  "email": "ada@example.com",
  "globalRole": "USER",
  "status": "ACTIVE"
}
```

- `id` (UUID)
- `globalRole` (String): `SUPERADMIN` | `ADMIN` | `USER`
- `status` (String): `PENDING_VERIFICATION` | `ACTIVE` | `INACTIVE` | `SUSPENDED` | `DELETED`

---

## Perfil propio

### 🔒 GET `/api/user/v1/view-profile`
**Descripción:** Devuelve el perfil del usuario autenticado.

**Acceso:** `USER`, `ADMIN`, `SUPERADMIN`

**Headers requeridos:**
```
Authorization: Bearer {token}
```

El usuario se resuelve por el email del token (`Authentication.getName()`), no por
parámetro: no hay forma de pedir el perfil de otro por este endpoint.

**Respuestas:**
- `200 OK`: `UserResponse`
- `401 Unauthorized`: No autenticado
- `403 Forbidden`: Sin rol
- `404 Not Found`: El usuario del token no existe o está dado de baja

---

### 🔒 PUT `/api/user/v1/update-profile`
**Descripción:** Actualiza los datos propios. **Update parcial:** los campos ausentes o
`null` se dejan como están.

**Acceso:** `USER`, `ADMIN`, `SUPERADMIN`

**Request Body:** (`UpdateUserRequest`)
```json
{
  "firstName": "Ada",
  "lastName": "Lovelace",
  "phone": "+54 11 5555-5555"
}
```

**Campos (todos opcionales):**
- `firstName` (String): si viene, no puede ser vacío.
- `lastName` (String): vacío se guarda como `null`.
- `phone` (String): vacío se guarda como `null`.

**No editable por acá:**
- `email`: requiere re-verificación de la cuenta.
- `password`: va por `/api/auth/change-password`.
- `globalRole` y `status`: son operaciones de administración.
- `id`: mandarlo en el body no tiene efecto — el usuario editado sale siempre del token.

**Respuestas:**
- `200 OK`: `UserResponse` actualizado
- `400 Bad Request`: `firstName` presente pero vacío
- `401 Unauthorized`: No autenticado
- `403 Forbidden`: Sin rol
- `404 Not Found`: El usuario del token no existe o está dado de baja

---

## Administración de usuarios

**Acceso a toda esta sección:** `ADMIN`, `SUPERADMIN`.

Un único árbol para los dos roles: con rol global en una sola entidad, duplicarlo en
`/api/superadmin` reintroduciría la duplicación por rol que eliminó el refactor.

Además del `@PreAuthorize` por método, `SecurityConfig` exige el rol por URL para
`/api/admin/**` como defensa en profundidad.

### 🔒 GET `/api/admin/v1/users`
**Descripción:** Lista usuarios.

**Query params:**
- `includeDeleted` (Boolean, default `false`): con `true` incluye los dados de baja.

**Respuestas:**
- `200 OK`: Array de `UserResponse`
- `401 Unauthorized` / `403 Forbidden`

---

### 🔒 GET `/api/admin/v1/users/{id}`
**Descripción:** Devuelve un usuario activo por id.

**Parámetros de ruta:**
- `id` (UUID)

**Respuestas:**
- `200 OK`: `UserResponse`
- `400 Bad Request`: El id no es un UUID válido
- `401 Unauthorized` / `403 Forbidden`
- `404 Not Found`: No existe o está dado de baja

---

### 🔒 GET `/api/admin/v1/users/filter/role/{role}`
**Descripción:** Lista usuarios activos con el rol global indicado.

**Parámetros de ruta:**
- `role`: `SUPERADMIN` | `ADMIN` | `USER`

**Ejemplo:** `/api/admin/v1/users/filter/role/ADMIN`

**Respuestas:**
- `200 OK`: Array de `UserResponse`
- `400 Bad Request`: Rol desconocido (p. ej. `DEVELOPER`, que ya no existe)
- `401 Unauthorized` / `403 Forbidden`

---

### 🔒 GET `/api/admin/v1/users/filter/status/{status}`
**Descripción:** Lista usuarios activos en el estado indicado.

**Parámetros de ruta:**
- `status`: `PENDING_VERIFICATION` | `ACTIVE` | `INACTIVE` | `SUSPENDED` | `DELETED`

**Respuestas:**
- `200 OK`: Array de `UserResponse`
- `400 Bad Request`: Estado desconocido
- `401 Unauthorized` / `403 Forbidden`

---

### 🔒 GET `/api/admin/v1/users/search`
**Descripción:** Búsqueda parcial sobre nombre **y** apellido, sin distinguir mayúsculas.
Sólo usuarios activos.

**Query params:**
- `name` (String, opcional): fragmento a buscar. Ausente o vacío devuelve lista vacía,
  **no** la tabla completa.

**Ejemplo:** `/api/admin/v1/users/search?name=love`

**Respuestas:**
- `200 OK`: Array de `UserResponse`
- `401 Unauthorized` / `403 Forbidden`

---

### 🔒 PUT `/api/admin/v1/users/{id}/status/{status}`
**Descripción:** Cambia el estado de un usuario.

**Parámetros de ruta:**
- `id` (UUID)
- `status`: `PENDING_VERIFICATION` | `ACTIVE` | `INACTIVE` | `SUSPENDED`

**`DELETED` no se acepta acá:** implica una baja lógica, así que va por `DELETE` sobre el
usuario. Pedirlo devuelve `400`.

**Ejemplo:** `/api/admin/v1/users/{id}/status/SUSPENDED`

**Respuestas:**
- `200 OK`: `UserResponse` actualizado
- `400 Bad Request`: Estado desconocido, o `DELETED`
- `401 Unauthorized` / `403 Forbidden`
- `404 Not Found`: No existe o está dado de baja

---

### 🔒 DELETE `/api/admin/v1/users/{id}`
**Descripción:** Baja lógica (*soft delete*): marca `deletedAt` y deja el estado en
`DELETED`. La fila **no** se borra.

**Parámetros de ruta:**
- `id` (UUID)

**Consecuencias:**
- El usuario desaparece de todas las consultas salvo `GET /users?includeDeleted=true`.
- **El email sigue ocupado.** El `UNIQUE` de email aplica a la tabla completa, así que no
  se puede reutilizar para un alta nueva.

**Respuestas:**
- `204 No Content`: Usuario dado de baja, sin cuerpo de respuesta
- `401 Unauthorized` / `403 Forbidden`
- `404 Not Found`: No existe o ya estaba dado de baja

---

## Admin - Solicitudes de Devolución

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

`UserRole` (campo `globalRole` de la entidad `User`):
- `SUPERADMIN`: Acceso completo al sistema
- `ADMIN`: Consulta y baja de usuarios, procesamiento de solicitudes de devolución
- `USER`: Su propio perfil

Los roles `DEVELOPER` y `SUPPORT` **fueron eliminados** en el refactor que unificó las
subclases de usuario en una entidad única. Las secciones de Developer, Support y Tickets
todavía los mencionan porque describen código pendiente de migrar.

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
