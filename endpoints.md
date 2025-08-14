# Endpoints de la API

## AuthController (`/api/auth`)

- `POST /api/auth/login` — Iniciar sesión (devuelve JWT y datos de usuario)
  **Request:**

  ```json
  {
    "email": "juan@email.com",
    "password": "tu_contraseña"
  }
  ```

  **Response:**

  ```json
  {
    "token": "jwt_token",
    "usuario": {
      "id": 1,
      "nombre": "Juan",
      "apellido": "Pérez",
      "email": "juan@email.com",
      "rol": "ADMIN",
      "cambiarPass": false,
      "activo": true,
      "bloqueado": false
    }
  }
  ```

- `POST /api/auth/cambiar-password` — Cambiar contraseña del usuario autenticado
  **Request:**

  ```json
  {
    "userId": 1,
    "newPassword": "nueva_contraseña"
  }
  ```

  **Response:**

  ```json
  "Contraseña actualizada correctamente"
  ```

- `POST /api/auth/reiniciar-password` — Reiniciar contraseña (solo admin/superadmin)
  **Request:**
  ```json
  {
    "userId": 1
  }
  ```
  **Response:**
  ```json
  "Contraseña reiniciada correctamente"
  ```

## UsuarioController (`/api/usuarios`)

- `POST /api/usuarios/crear` — Crear usuario (solo admin/superadmin)
  **Request:**

  ```json
  {
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "ADMIN"
  }
  ```

  **Response:**

  ```json
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "ADMIN",
    "cambiarPass": false,
    "activo": true,
    "bloqueado": false
  }
  ```

- `GET /api/usuarios/obtener-datos?userId={id}` — Obtener datos de usuario por ID
  **Response:**

  ```json
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "ADMIN",
    "cambiarPass": false,
    "activo": true,
    "bloqueado": false
  }
  ```

- `PUT /api/usuarios/editar-datos?userId={id}` — Editar datos de usuario
  **Request:**
  ```json
  {
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "ADMIN"
  }
  ```
  **Response:**
  ```json
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "ADMIN",
    "cambiarPass": false,
    "activo": true,
    "bloqueado": false
  }
  ```

## TrabajadorController (`/api/trabajador`)

- `POST /api/trabajador/tickets/{ticketId}/evaluar` — Evaluar solución de ticket (aceptar/rechazar)
  **Request:**
  ```json
  {
    "idTicket": 10,
    "idTrabajador": 4,
    "aceptado": true,
    "comentario": "Solución correcta"
  }
  ```
  **Response:**
  ```json
  {
    "id": 10,
    "titulo": "No funciona impresora",
    "descripcion": "La impresora no imprime hojas",
    "estado": "RESUELTO",
    "creador": "Juan Pérez",
    "tecnicoAsignado": "Carlos López",
    "fechaCreacion": "2025-08-10T09:00:00",
    "fechaUltimaActualizacion": "2025-08-10T12:00:00"
  }
  ```

## TicketController (`/api/tickets`)

- `GET /api/tickets/todos` — Listar todos los tickets (admin/superadmin)
  **Response:**

  ```json
  [
    {
      "id": 10,
      "titulo": "No funciona impresora",
      "descripcion": "La impresora no imprime hojas",
      "estado": "RESUELTO",
      "creador": "Juan Pérez",
      "tecnicoAsignado": "Carlos López",
      "fechaCreacion": "2025-08-10T09:00:00",
      "fechaUltimaActualizacion": "2025-08-10T12:00:00"
    }
  ]
  ```

- `GET /api/tickets/trabajador/mis-tickets` — Listar tickets del trabajador autenticado
  **Response:**

  ```json
  [
    {
      "id": 11,
      "titulo": "No anda el monitor",
      "descripcion": "Pantalla negra al encender",
      "estado": "NO_ATENDIDO",
      "creador": "Ana García",
      "tecnicoAsignado": null,
      "fechaCreacion": "2025-08-11T10:00:00",
      "fechaUltimaActualizacion": "2025-08-11T10:00:00"
    }
  ]
  ```

- `GET /api/tickets/trabajador/tickets-para-evaluar` — Tickets en estado RESUELTO para evaluación
  **Response:**

  ```json
  [
    {
      "id": 12,
      "titulo": "Problema de red",
      "descripcion": "Sin acceso a internet",
      "estado": "RESUELTO",
      "creador": "Ana García",
      "tecnicoAsignado": "Carlos López",
      "fechaCreacion": "2025-08-12T08:00:00",
      "fechaUltimaActualizacion": "2025-08-12T09:00:00"
    }
  ]
  ```

- `GET /api/tickets/tecnico/tickets-disponibles` — Tickets no asignados y reabiertos (técnico)
  **Response:**

  ```json
  [
    {
      "id": 13,
      "titulo": "Error en software",
      "descripcion": "No abre el programa",
      "estado": "REABIERTO",
      "creador": "Juan Pérez",
      "tecnicoAsignado": null,
      "fechaCreacion": "2025-08-13T09:00:00",
      "fechaUltimaActualizacion": "2025-08-13T09:00:00"
    }
  ]
  ```

- `GET /api/tickets/tecnico/mis-tickets` — Tickets asignados al técnico autenticado
  **Response:**

  ```json
  [
    {
      "id": 14,
      "titulo": "Problema de red",
      "descripcion": "Sin acceso a internet",
      "estado": "ATENDIDO",
      "creador": "Ana García",
      "tecnicoAsignado": "Carlos López",
      "fechaCreacion": "2025-08-12T08:00:00",
      "fechaUltimaActualizacion": "2025-08-12T09:00:00"
    }
  ]
  ```

- `GET /api/tickets/tecnico/historial` — Historial de tickets del técnico
  **Response:**

  ```json
  [
    {
      "id": 15,
      "titulo": "No funciona impresora",
      "descripcion": "La impresora no imprime hojas",
      "estado": "RESUELTO",
      "creador": "Juan Pérez",
      "tecnicoAsignado": "Carlos López",
      "fechaCreacion": "2025-08-10T09:00:00",
      "fechaUltimaActualizacion": "2025-08-10T12:00:00"
    }
  ]
  ```

- `POST /api/tickets/crear-ticket` — Crear ticket (según rol)
  **Request:**

  ```json
  {
    "titulo": "No anda el monitor",
    "descripcion": "Pantalla negra al encender"
  }
  ```

  **Response:**

  ```json
  {
    "id": 16,
    "titulo": "No anda el monitor",
    "descripcion": "Pantalla negra al encender",
    "estado": "NO_ATENDIDO",
    "creador": "Ana García",
    "tecnicoAsignado": null,
    "fechaCreacion": "2025-08-13T10:00:00",
    "fechaUltimaActualizacion": "2025-08-13T10:00:00"
  }
  ```

- `POST /api/tickets/{id}/reabrir` — Reabrir ticket (con comentario)
  **Request:**
  ```json
  {
    "comentario": "El problema persiste"
  }
  ```
  **Response:**
  ```json
  {
    "id": 17,
    "titulo": "No funciona impresora",
    "descripcion": "La impresora no imprime hojas",
    "estado": "REABIERTO",
    "creador": "Juan Pérez",
    "tecnicoAsignado": "Carlos López",
    "fechaCreacion": "2025-08-10T09:00:00",
    "fechaUltimaActualizacion": "2025-08-13T11:00:00"
  }
  ```

## TecnicoController (`/api/tecnico`)

- `POST /api/tecnico/tickets/{ticketId}/tomar?idTecnico={id}` — Tomar ticket (NO_ATENDIDO/REABIERTO)
  **Response:**

  ```json
  "Ticket tomado correctamente"
  ```

- `POST /api/tecnico/tickets/{ticketId}/resolver?idTecnico={id}` — Resolver ticket
  **Response:**

  ```json
  "Estado de ticket actualizado a: Resuelto"
  ```

- `POST /api/tecnico/tickets/{ticketId}/devolver?idTecnico={id}&motivo={motivo}` — Devolver ticket con motivo
  **Response:**
  ```json
  "Ticket devuelto"
  ```

## SuperAdminController (`/api/superadmin`)

- `GET /api/superadmin/admins` — Listar todos los administradores
  **Response:**
  ```json
  [
    {
      "id": 2,
      "nombre": "Carlos",
      "apellido": "López",
      "email": "carlos@email.com",
      "rol": "ADMIN",
      "cambiarPass": false,
      "activo": true,
      "bloqueado": false
    }
  ]
  ```

## AdminController (`/api/admin`)

- `PUT /api/admin/usuarios/{id}/editar` — Editar datos de usuario
  **Request:**

  ```json
  {
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "ADMIN"
  }
  ```

  **Response:**

  ```json
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "ADMIN",
    "cambiarPass": false,
    "activo": true,
    "bloqueado": false
  }
  ```

- `PUT /api/admin/usuarios/{id}/activar` — Activar/desactivar usuario
  **Response:**

  ```json
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "ADMIN",
    "cambiarPass": false,
    "activo": true,
    "bloqueado": false
  }
  ```

- `PUT /api/admin/usuarios/{id}/bloquear` — Bloquear/desbloquear usuario
  **Response:**

  ```json
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "ADMIN",
    "cambiarPass": false,
    "activo": true,
    "bloqueado": true
  }
  ```

- `PUT /api/admin/usuarios/{id}/rol` — Cambiar rol de usuario
  **Request:**
  ```json
  {
    "rol": "TECNICO"
  }
  ```
  **Response:**
  ```json
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "TECNICO",
    "cambiarPass": false,
    "activo": true,
    "bloqueado": false
  }
  ```
