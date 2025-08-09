# Documentación de Endpoints API

Este documento lista todos los endpoints implementados en la API que interactúan con la base de datos.

---

## Autenticación

- `POST /api/auth/login` — Iniciar sesión (JWT)
- `POST /api/auth/cambiar-password` — Cambiar contraseña
- `POST /api/auth/reiniciar-password` — Reiniciar contraseña (admin)

## Usuarios

- `GET /api/usuarios/obtener-datos?userId={id}` — Obtener datos de usuario
- `PUT /api/usuarios/editar-datos?userId={id}` — Editar datos de usuario
- `PUT /api/usuarios/cambiar-password` — Cambiar contraseña de usuario
- `GET /api/usuarios/notificaciones?userId={id}` — Ver notificaciones del usuario
- `GET /api/usuarios/mis-tickets?userId={id}` — Ver tickets del usuario
- `GET /api/usuarios/estadisticas/totales` — Total de usuarios
- `GET /api/usuarios/estadisticas/activos` — Total de usuarios activos
- `GET /api/usuarios/estadisticas/tecnicos-bloqueados` — Total de técnicos bloqueados

## Tickets

- `GET /api/tickets` — Listar todos los tickets
- `GET /api/tickets/{id}` — Ver detalle de ticket
- `POST /api/tickets` — Crear nuevo ticket
- `PUT /api/tickets/{id}/estado?estado={estado}` — Actualizar estado de ticket
- `GET /api/tickets/estado?estado={estado}` — Listar tickets por estado
- `GET /api/tickets/creador?userId={id}` — Listar tickets por creador
- `GET /api/tickets/buscar-titulo?palabra={palabra}` — Buscar tickets por título

## Administración

- `POST /api/admin/usuarios` — Crear usuario (admin, técnico, trabajador)
- `GET /api/admin/usuarios` — Listar todos los usuarios
- `GET /api/admin/usuarios/{id}` — Ver detalles de usuario

## Otros módulos

- Endpoints adicionales disponibles en:
  - `/api/notificaciones`
  - `/api/auditoria`
  - `/api/estadisticas`
  - `/api/superadmin`
  - `/api/tecnico`
  - `/api/trabajador`

---

## Autenticación

### POST /api/auth/login

- **Body:**

```json
{
  "email": "string",
  "password": "string"
}
```

- **Devuelve:**

```json
{
  "token": "string",
  "refreshToken": "string",
  "user": { ... }
}
```

### POST /api/auth/cambiar-password

- **Body:**

```json
{
  "userId": 1,
  "oldPassword": "string",
  "newPassword": "string"
}
```

- **Devuelve:**
  `"Contraseña actualizada correctamente"`

### POST /api/auth/reiniciar-password

- **Body:**

```json
{
  "email": "string"
}
```

- **Devuelve:**
  `"Contraseña reiniciada y enviada por email"`

---

## Usuarios

### GET /api/usuarios/obtener-datos?userId={id}

- **Devuelve:**

```json
{
  "id": 1,
  "nombre": "string",
  "apellido": "string",
  "email": "string",
  "rol": "string",
  ...
}
```

### PUT /api/usuarios/editar-datos?userId={id}

- **Body:**

```json
{
  "nombre": "string",
  "apellido": "string",
  "email": "string",
  ...
}
```

- **Devuelve:**
  `UsuarioResponseDto` (igual al GET)

### PUT /api/usuarios/cambiar-password

- **Body:**

```json
{
  "userId": 1,
  "oldPassword": "string",
  "newPassword": "string"
}
```

- **Devuelve:**
  `"Contraseña cambiada exitosamente"`

### GET /api/usuarios/notificaciones?userId={id}

- **Devuelve:**

```json
[
  {
    "id": 1,
    "mensaje": "string",
    "fecha": "yyyy-MM-dd HH:mm:ss",
    ...
  }
]
```

### GET /api/usuarios/mis-tickets?userId={id}

- **Devuelve:**

```json
[
  {
    "id": 1,
    "titulo": "string",
    "estado": "string",
    ...
  }
]
```

---

## Tickets

### GET /api/tickets

- **Devuelve:**
  `Array<TicketResponseDto>`

### GET /api/tickets/{id}

- **Devuelve:**
  `TicketResponseDto`

### POST /api/tickets

- **Body:**

```json
{
  "titulo": "string",
  "descripcion": "string",
  "creadorId": 1,
  ...
}
```

- **Devuelve:**
  `TicketResponseDto`

### PUT /api/tickets/{id}/estado?estado={estado}

- **Devuelve:**
  `TicketResponseDto` actualizado

### GET /api/tickets/estado?estado={estado}

- **Devuelve:**
  `Array<TicketResponseDto>`

### GET /api/tickets/creador?userId={id}

- **Devuelve:**
  `Array<TicketResponseDto>`

### GET /api/tickets/buscar-titulo?palabra={palabra}

- **Devuelve:**
  `Array<TicketResponseDto>`

---

## Administración

### POST /api/admin/usuarios

- **Body:**

```json
{
  "nombre": "string",
  "apellido": "string",
  "email": "string",
  "rol": "string",
  ...
}
```

- **Devuelve:**
  `UsuarioResponseDto`

### GET /api/admin/usuarios

- **Devuelve:**
  `Array<UsuarioResponseDto>`

### GET /api/admin/usuarios/{id}

- **Devuelve:**
  `UsuarioResponseDto`

---

> Para detalles completos de cada campo, consultar la documentación Swagger/OpenAPI.
