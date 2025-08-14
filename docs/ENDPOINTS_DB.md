### GET /api/admin/listar-usuarios

**Descripción:** Devuelve la lista de todos los usuarios del sistema. Solo Admin y SuperAdmin pueden acceder. Admin no puede ver SuperAdmins.

**Requiere autenticación JWT.**

**Response:**

```json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@empresa.com",
    "rol": "ADMIN",
    "activo": true,
    "bloqueado": false
  },
  {
    "id": 2,
    "nombre": "Ana",
    "apellido": "García",
    "email": "ana@empresa.com",
    "rol": "TECNICO",
    "activo": true,
    "bloqueado": false
  }
]
```

# Documentación de Endpoints API

Este documento lista todos los endpoints implementados en la API que interactúan con la base de datos.

---

## Autenticación

### POST /api/auth/login

**Request:**

```json
{
  "email": "usuario@email.com",
  "password": "tuPassword"
}
```

**Response:**

```json
{
  "token": "jwt-token",
  "refreshToken": "refresh-token",
  "user": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "usuario@email.com",
    "rol": "ADMIN"
  }
}
```

### POST /api/auth/cambiar-password

**Request:**

```json
{
  "userId": 1,
  "oldPassword": "anterior",
  "newPassword": "nueva"
}
```

**Response:**

```json
{
  "mensaje": "Contraseña actualizada correctamente"
}
```

### POST /api/auth/reiniciar-password

**Request:**

```json
{
  "email": "usuario@email.com"
}
```

**Response:**

```json
{
  "mensaje": "Contraseña reiniciada y enviada por email"
}
```

## Usuarios (autogestión)

### GET /api/usuarios/obtener-datos?userId={id}

**Response:**

```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "usuario@email.com",
  "rol": "TRABAJADOR",
  "activo": true
}
```

### PUT /api/usuarios/editar-datos?userId={id}

**Request:**

```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "usuario@email.com"
}
```

**Response:**

```json
{
  "mensaje": "Datos actualizados correctamente"
}
```

### PUT /api/usuarios/cambiar-password

**Request:**

```json
{
  "userId": 1,
  "oldPassword": "anterior",
  "newPassword": "nueva"
}
```

**Response:**

```json
{
  "mensaje": "Contraseña actualizada correctamente"
}
```

### GET /api/usuarios/notificaciones?userId={id}

**Response:**

```json
[
  {
    "id": 101,
    "mensaje": "Ticket #123 actualizado",
    "fecha": "2025-08-12T10:00:00Z",
    "leido": false
  },
  {
    "id": 102,
    "mensaje": "Nueva respuesta en ticket #124",
    "fecha": "2025-08-11T15:30:00Z",
    "leido": true
  }
]
```

### GET /api/usuarios/mis-tickets?userId={id}

**Response:**

```json
[
  {
    "id": 123,
    "titulo": "Problema con impresora",
    "estado": "ABIERTO",
    "fechaCreacion": "2025-08-10T09:00:00Z"
  },
  {
    "id": 124,
    "titulo": "No funciona el correo",
    "estado": "EN_PROGRESO",
    "fechaCreacion": "2025-08-11T11:20:00Z"
  }
]
```

## Administración de usuarios (solo admin/superadmin)

### POST /api/admin/usuarios/crear

**Request:**

```json
{
  "nombre": "Ana",
  "apellido": "García",
  "email": "ana@empresa.com",
  "rol": "TECNICO",
  "password": "password123"
}
```

**Response:**

```json
{
  "id": 2,
  "mensaje": "Usuario creado correctamente"
}
```

### PUT /api/admin/usuarios/{id}/editar

**Request:**

```json
{
  "nombre": "Ana",
  "apellido": "García",
  "email": "ana@empresa.com"
}
```

**Response:**

```json
{
  "mensaje": "Usuario actualizado correctamente"
}
```

### PUT /api/admin/usuarios/{id}/activar

**Request:**

```json
{
  "activo": true
}
```

**Response:**

```json
{
  "mensaje": "Usuario activado"
}
```

### PUT /api/admin/usuarios/{id}/bloquear

**Request:**

```json
{
  "bloqueado": true
}
```

**Response:**

```json
{
  "mensaje": "Usuario bloqueado"
}
```

### POST /api/admin/usuarios/{id}/reset-password

**Request:**

```json
{
  "id": 2
}
```

**Response:**

```json
{
  "mensaje": "Contraseña reseteada y enviada por email"
}
```

### PUT /api/admin/usuarios/{id}/rol

**Request:**

```json
{
  "rol": "ADMIN"
}
```

**Response:**

```json
{
  "mensaje": "Rol actualizado correctamente"
}
```

### GET /api/admin/usuarios/rol?rol={rol}

**Response:**

```json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@empresa.com",
    "rol": "ADMIN"
  },
  {
    "id": 2,
    "nombre": "Ana",
    "apellido": "García",
    "email": "ana@empresa.com",
    "rol": "ADMIN"
  }
]
```

### GET /api/admin/usuarios/listar-todos

**Response:**

```json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@empresa.com",
    "rol": "ADMIN"
  },
  {
    "id": 2,
    "nombre": "Ana",
    "apellido": "García",
    "email": "ana@empresa.com",
    "rol": "TECNICO"
  }
]
```

## Tickets

### GET /api/tickets/todos

**Response:**

```json
[
  {
    "id": 201,
    "titulo": "Problema con impresora",
    "estado": "ABIERTO",
    "creador": "Juan Pérez",
    "fechaCreacion": "2025-08-10T09:00:00Z"
  },
  {
    "id": 202,
    "titulo": "No funciona el correo",
    "estado": "EN_PROGRESO",
    "creador": "Ana García",
    "fechaCreacion": "2025-08-11T11:20:00Z"
  }
]
```

### GET /api/tickets/trabajador/mis-tickets

**Response:**

```json
[
  {
    "id": 203,
    "titulo": "Error en PC",
    "estado": "CERRADO",
    "fechaCreacion": "2025-08-09T08:00:00Z"
  }
]
```

### GET /api/tickets/tecnico/tickets-disponibles

**Response:**

```json
[
  {
    "id": 204,
    "titulo": "Problema de red",
    "estado": "ABIERTO"
  }
]
```

### GET /api/tickets/tecnico/mis-tickets

**Response:**

```json
[
  {
    "id": 205,
    "titulo": "Actualizar software",
    "estado": "EN_PROGRESO"
  }
]
```

### GET /api/tickets/tecnico/historial

**Response:**

```json
[
  {
    "id": 206,
    "titulo": "Instalación de impresora",
    "estado": "CERRADO"
  }
]
```

### POST /api/tickets/crear-ticket

**Request:**

```json
{
  "titulo": "Nuevo problema",
  "descripcion": "La PC no enciende",
  "creadorId": 1
}
```

**Response:**

```json
{
  "id": 207,
  "mensaje": "Ticket creado correctamente"
}
```

### POST /api/tickets/{id}/reabrir?comentario={comentario}

**Request:**

```json
{
  "comentario": "El problema persiste"
}
```

**Response:**

```json
{
  "mensaje": "Ticket reabierto"
}
```

### GET /api/tickets/{id}

**Response:**

```json
{
  "id": 208,
  "titulo": "Problema de red",
  "descripcion": "No hay conexión",
  "estado": "ABIERTO",
  "creador": "Juan Pérez",
  "fechaCreacion": "2025-08-12T10:00:00Z"
}
```

### PUT /api/tickets/{id}/estado?estado={estado}

**Request:**

```json
{
  "estado": "EN_PROGRESO"
}
```

**Response:**

```json
{
  "mensaje": "Estado actualizado"
}
```

### GET /api/tickets/estado?estado={estado}

**Response:**

```json
[
  {
    "id": 209,
    "titulo": "Problema de red",
    "estado": "EN_PROGRESO"
  }
]
```

### GET /api/tickets/creador?userId={id}

**Response:**

```json
[
  {
    "id": 210,
    "titulo": "Problema con impresora",
    "estado": "ABIERTO"
  }
]
```

### GET /api/tickets/buscar-titulo?palabra={palabra}

**Response:**

```json
[
  {
    "id": 211,
    "titulo": "Problema de red",
    "estado": "ABIERTO"
  }
]
```

## Trabajadores

### POST /api/trabajador/tickets

**Request:**

```json
{
  "titulo": "Problema con monitor",
  "descripcion": "No enciende el monitor",
  "trabajadorId": 3
}
```

**Response:**

```json
{
  "id": 301,
  "mensaje": "Ticket creado correctamente"
}
```

### GET /api/trabajador/tickets

**Response:**

```json
[
  {
    "id": 302,
    "titulo": "Problema con monitor",
    "estado": "ABIERTO"
  }
]
```

### GET /api/trabajador/tickets/activos

**Response:**

```json
[
  {
    "id": 303,
    "titulo": "Problema con teclado",
    "estado": "EN_PROGRESO"
  }
]
```

### POST /api/trabajador/tickets/{ticketId}/evaluar

**Request:**

```json
{
  "ticketId": 302,
  "calificacion": 5,
  "comentario": "Solución rápida y efectiva"
}
```

**Response:**

```json
{
  "mensaje": "Evaluación registrada"
}
```

### GET /api/trabajador/listar-todos

**Response:**

```json
[
  {
    "id": 3,
    "nombre": "Carlos",
    "apellido": "López",
    "email": "carlos@empresa.com"
  },
  {
    "id": 4,
    "nombre": "María",
    "apellido": "Fernández",
    "email": "maria@empresa.com"
  }
]
```
