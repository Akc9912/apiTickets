# Esquemas JSON de la API

## Usuario

### UsuarioRequestDto (Request)

```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@email.com",
  "rol": "ADMIN"
}
```

### UsuarioResponseDto (Response)

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

---

## Ticket

### TicketRequestDto (Request)

```json
{
  "titulo": "Problema de red",
  "descripcion": "No tengo acceso a internet",
  "idTrabajador": 5
}
```

### TicketResponseDto (Response)

```json
{
  "id": 10,
  "titulo": "Problema de red",
  "descripcion": "No tengo acceso a internet",
  "estado": "NO_ATENDIDO",
  "creador": "Juan",
  "tecnicoAsignado": "Ana",
  "fechaCreacion": "2025-08-09T10:00:00",
  "fechaUltimaActualizacion": "2025-08-09T12:00:00"
}
```

---

## Login

### LoginRequestDto (Request)

```json
{
  "email": "juan@email.com",
  "password": "tu_contraseña"
}
```

### LoginResponseDto (Response)

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

---

## Cambio de contraseña

### ChangePasswordDto (Request)

```json
{
  "userId": 1,
  "newPassword": "nueva_contraseña"
}
```

---

## ResetPasswordDto (Request)

```json
{
  "userId": 1
}
```

---

## TrabajadorResponseDto (Response)

```json
{
  "id": 5,
  "nombre": "Ana",
  "apellido": "García",
  "email": "ana@email.com",
  "activo": true
}
```

---

## NotificacionResponseDto (Response)

```json
{
  "id": 100,
  "idUsuario": 1,
  "mensaje": "Ticket asignado",
  "fechaCreacion": "2025-08-09T13:00:00"
}
```

---

# Controladores

## AdminController

### Listar todos los usuarios (GET /api/admin/usuarios)

```json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@email.com",
    "rol": "ADMIN",
    "tipo": "ADMIN",
    "activo": true,
    "bloqueado": false
    // atributos específicos de admin...
  },
  {
    "id": 2,
    "nombre": "Ana",
    "apellido": "García",
    "email": "ana@email.com",
    "rol": "TECNICO",
    "tipo": "TECNICO",
    "activo": true,
    "bloqueado": false,
    "especialidad": "Redes",
    "fallas": 3
    // atributos específicos de técnico...
  },
  {
    "id": 3,
    "nombre": "Pedro",
    "apellido": "López",
    "email": "pedro@email.com",
    "rol": "TRABAJADOR",
    "tipo": "TRABAJADOR",
    "activo": true,
    "area": "Soporte"
    // atributos específicos de trabajador...
  }
]
```

---
