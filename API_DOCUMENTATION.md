# 📖 Documentación API para Frontend - Sistema de Tickets

## 🌐 Información General

### URL Base

```
http://localhost:8080/api
```

### Autenticación

Todas las rutas (excepto login) requieren autenticación JWT en el header:

```
Authorization: Bearer {token}
```

### Formatos de Respuesta

- **Content-Type**: `application/json`
- **Códigos de Estado**: Estándar HTTP (200, 201, 400, 401, 403, 404, 500)
- **Estructura de Error**:

```json
{
  "timestamp": "2025-08-06T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "path": "/api/endpoint"
}
```

---

## 🔐 Autenticación

### POST `/auth/login`

Iniciar sesión y obtener token JWT.

**Request:**

```json
{
  "email": "admin@example.com",
  "password": "123456"
}
```

**Response (200):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTY5MTMyNDgwMCwiZXhwIjoxNjkxMzYwODAwfQ...",
  "usuario": {
    "id": 1,
    "nombre": "Administrador",
    "email": "admin@example.com",
    "rol": "ADMIN",
    "fechaCreacion": "2025-01-15T10:30:00"
  }
}
```

**Usuarios de Prueba:**

- **SuperAdmin**: `admin@example.com` / `123456`
- **Técnico**: `tecnico@example.com` / `123456`
- **Trabajador**: `trabajador@example.com` / `123456`

---

## 🎫 Gestión de Tickets

### GET `/tickets`

Obtener lista de tickets según el rol del usuario.

**Headers:** `Authorization: Bearer {token}`

**Response (200):**

```json
[
  {
    "id": 1,
    "titulo": "Problema con impresora",
    "descripcion": "La impresora HP no imprime documentos",
    "estado": "ABIERTO",
    "prioridad": "MEDIA",
    "categoria": "HARDWARE",
    "fechaCreacion": "2025-08-06T10:15:00",
    "fechaActualizacion": "2025-08-06T10:15:00",
    "solicitante": {
      "id": 2,
      "nombre": "Juan Pérez",
      "email": "juan@empresa.com"
    },
    "tecnicoAsignado": null
  }
]
```

### POST `/tickets`

Crear un nuevo ticket.

**Headers:** `Authorization: Bearer {token}`

**Request:**

```json
{
  "titulo": "Problema con software",
  "descripcion": "El programa se cierra inesperadamente",
  "prioridad": "ALTA",
  "categoria": "SOFTWARE"
}
```

**Response (201):**

```json
{
  "id": 15,
  "titulo": "Problema con software",
  "descripcion": "El programa se cierra inesperadamente",
  "estado": "ABIERTO",
  "prioridad": "ALTA",
  "categoria": "SOFTWARE",
  "fechaCreacion": "2025-08-06T15:30:00",
  "fechaActualizacion": "2025-08-06T15:30:00",
  "solicitante": {
    "id": 3,
    "nombre": "María González",
    "email": "maria@empresa.com"
  },
  "tecnicoAsignado": null
}
```

### GET `/tickets/{id}`

Obtener detalles de un ticket específico.

**Headers:** `Authorization: Bearer {token}`

**Response (200):**

```json
{
  "id": 1,
  "titulo": "Problema con impresora",
  "descripcion": "La impresora HP no imprime documentos",
  "estado": "EN_PROGRESO",
  "prioridad": "MEDIA",
  "categoria": "HARDWARE",
  "fechaCreacion": "2025-08-06T10:15:00",
  "fechaActualizacion": "2025-08-06T14:20:00",
  "solicitante": {
    "id": 2,
    "nombre": "Juan Pérez",
    "email": "juan@empresa.com"
  },
  "tecnicoAsignado": {
    "id": 4,
    "nombre": "Carlos Técnico",
    "email": "carlos@empresa.com"
  }
}
```

### PUT `/tickets/{id}`

Actualizar un ticket (solo técnicos y administradores).

**Headers:** `Authorization: Bearer {token}`

**Request:**

```json
{
  "estado": "RESUELTO",
  "comentario": "Problema solucionado. Se cambió el tóner de la impresora."
}
```

### DELETE `/tickets/{id}`

Eliminar un ticket (solo administradores).

**Headers:** `Authorization: Bearer {token}`

**Response (204):** Sin contenido

---

## 👥 Gestión de Usuarios

### GET `/admin/usuarios`

Obtener lista de usuarios (solo administradores).

**Headers:** `Authorization: Bearer {token}`

**Response (200):**

```json
[
  {
    "id": 1,
    "nombre": "Administrador",
    "email": "admin@example.com",
    "rol": "ADMIN",
    "fechaCreacion": "2025-01-15T10:30:00",
    "activo": true
  },
  {
    "id": 2,
    "nombre": "Juan Pérez",
    "email": "juan@empresa.com",
    "rol": "TRABAJADOR",
    "fechaCreacion": "2025-02-01T09:15:00",
    "activo": true
  }
]
```

### POST `/admin/usuarios`

Crear un nuevo usuario (solo administradores).

**Headers:** `Authorization: Bearer {token}`

**Request:**

```json
{
  "nombre": "Nuevo Empleado",
  "email": "nuevo@empresa.com",
  "password": "123456",
  "rol": "TRABAJADOR"
}
```

**Response (201):**

```json
{
  "id": 10,
  "nombre": "Nuevo Empleado",
  "email": "nuevo@empresa.com",
  "rol": "TRABAJADOR",
  "fechaCreacion": "2025-08-06T15:45:00",
  "activo": true
}
```

### GET `/admin/usuarios/{id}`

Obtener detalles de un usuario específico.

### PUT `/admin/usuarios/{id}`

Actualizar datos de un usuario.

### DELETE `/admin/usuarios/{id}`

Eliminar un usuario (cambiar estado a inactivo).

---

## 👨‍💼 Gestión de Técnicos

### GET `/admin/tecnicos`

Obtener lista de técnicos disponibles.

**Response (200):**

```json
[
  {
    "id": 4,
    "nombre": "Carlos Técnico",
    "email": "carlos@empresa.com",
    "especialidad": "HARDWARE",
    "ticketsAsignados": 3,
    "disponible": true
  }
]
```

### PUT `/admin/tecnicos/{id}/asignar`

Asignar un ticket a un técnico.

**Request:**

```json
{
  "ticketId": 5
}
```

---

## 📊 Estadísticas y Reportes

### GET `/estadisticas/dashboard`

Obtener estadísticas generales del sistema.

**Headers:** `Authorization: Bearer {token}`

**Response (200):**

```json
{
  "totalTickets": 150,
  "ticketsAbiertos": 25,
  "ticketsEnProgreso": 15,
  "ticketsResueltos": 110,
  "ticketsPorPrioridad": {
    "CRITICA": 2,
    "ALTA": 8,
    "MEDIA": 35,
    "BAJA": 105
  },
  "ticketsPorCategoria": {
    "SOFTWARE": 75,
    "HARDWARE": 45,
    "NETWORK": 20,
    "OTHER": 10
  },
  "promedioResolucion": "2.5 días",
  "tecnicosMasActivos": [
    {
      "nombre": "Carlos Técnico",
      "ticketsResueltos": 45
    }
  ]
}
```

### GET `/estadisticas/tickets-por-periodo`

Estadísticas por período de tiempo.

**Query Parameters:**

- `fechaInicio`: `2025-01-01`
- `fechaFin`: `2025-08-06`
- `agrupacion`: `DIA|SEMANA|MES`

---

## 🔔 Notificaciones

### GET `/notificaciones`

Obtener notificaciones del usuario actual.

**Response (200):**

```json
[
  {
    "id": 1,
    "mensaje": "Se te ha asignado el ticket #15",
    "tipo": "ASIGNACION",
    "leida": false,
    "fechaCreacion": "2025-08-06T15:30:00",
    "ticketId": 15
  }
]
```

### PUT `/notificaciones/{id}/marcar-leida`

Marcar una notificación como leída.

**Response (200):**

```json
{
  "id": 1,
  "mensaje": "Se te ha asignado el ticket #15",
  "tipo": "ASIGNACION",
  "leida": true,
  "fechaCreacion": "2025-08-06T15:30:00",
  "ticketId": 15
}
```

---

## 📝 Historial de Cambios

### GET `/historial/ticket/{ticketId}`

Obtener historial de cambios de un ticket.

**Response (200):**

```json
[
  {
    "id": 1,
    "accion": "CREADO",
    "descripcion": "Ticket creado por Juan Pérez",
    "fecha": "2025-08-06T10:15:00",
    "usuario": {
      "id": 2,
      "nombre": "Juan Pérez"
    }
  },
  {
    "id": 2,
    "accion": "ASIGNADO",
    "descripcion": "Ticket asignado a Carlos Técnico",
    "fecha": "2025-08-06T11:20:00",
    "usuario": {
      "id": 1,
      "nombre": "Administrador"
    }
  }
]
```

---

## 🛡️ Roles y Permisos

| Rol            | Descripción         | Permisos                                                                                                    |
| -------------- | ------------------- | ----------------------------------------------------------------------------------------------------------- |
| **TRABAJADOR** | Usuario estándar    | • Crear tickets<br>• Ver sus propios tickets<br>• Comentar en sus tickets                                   |
| **TECNICO**    | Soporte técnico     | • Todo lo de TRABAJADOR<br>• Ver todos los tickets<br>• Actualizar estado de tickets<br>• Asignarse tickets |
| **ADMIN**      | Administrador       | • Todo lo de TECNICO<br>• Gestionar usuarios<br>• Ver estadísticas<br>• Eliminar tickets                    |
| **SUPERADMIN** | Super administrador | • Acceso total al sistema<br>• Promover/degradar administradores<br>• Configuración del sistema             |

---

## 📋 Enumeraciones

### Estados de Ticket

```javascript
const ESTADOS = {
  ABIERTO: "Ticket recién creado",
  EN_PROGRESO: "Ticket siendo trabajado",
  RESUELTO: "Ticket solucionado",
  CERRADO: "Ticket finalizado y confirmado",
};
```

### Prioridades

```javascript
const PRIORIDADES = {
  BAJA: "Sin urgencia",
  MEDIA: "Prioridad normal",
  ALTA: "Requiere atención pronta",
  CRITICA: "Requiere atención inmediata",
};
```

### Categorías

```javascript
const CATEGORIAS = {
  SOFTWARE: "Problemas de software",
  HARDWARE: "Problemas de hardware",
  NETWORK: "Problemas de red",
  OTHER: "Otros problemas",
};
```

---

## 🔄 Códigos de Estado HTTP

| Código  | Descripción           | Cuándo se usa                  |
| ------- | --------------------- | ------------------------------ |
| **200** | OK                    | Operación exitosa              |
| **201** | Created               | Recurso creado exitosamente    |
| **204** | No Content            | Eliminación exitosa            |
| **400** | Bad Request           | Datos inválidos en el request  |
| **401** | Unauthorized          | Token inválido o expirado      |
| **403** | Forbidden             | Sin permisos para la operación |
| **404** | Not Found             | Recurso no encontrado          |
| **500** | Internal Server Error | Error interno del servidor     |

---

## 🧪 Testing de la API

### Swagger UI

Interfaz interactiva para probar todos los endpoints:

```
http://localhost:8080/swagger-ui.html
```

### Health Check

Verificar que la API esté funcionando:

```
GET http://localhost:8080/actuator/health
```

### Ejemplos con cURL

**Login:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"123456"}'
```

**Crear Ticket:**

```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Test","descripcion":"Ticket de prueba","prioridad":"MEDIA","categoria":"SOFTWARE"}'
```

**Listar Tickets:**

```bash
curl -X GET http://localhost:8080/api/tickets \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🔧 Configuración CORS

La API está configurada para permitir requests desde:

- `http://localhost:3000` (React/Angular dev server)
- `http://localhost:5173` (Vite dev server)
- `http://localhost:8080` (mismo origen)

Headers permitidos:

- `Authorization`
- `Content-Type`
- `X-Requested-With`

---

## 📚 Recursos Adicionales

### Documentación Técnica

- **OpenAPI Spec**: `http://localhost:8080/api-docs`
- **Health Metrics**: `http://localhost:8080/actuator`

### Logs del Sistema

Los logs se guardan en: `logs/app.log`

### Base de Datos

- **Motor**: MySQL 8.0+
- **Puerto**: 3306
- **Schema**: `apiticket`

---

## 🚀 Iniciar el Backend

```bash
# Clonar repositorio
git clone [URL_DEL_REPO]
cd apiTickets

# Configurar base de datos en application.properties
# Asegurar que MySQL esté corriendo

# Compilar y ejecutar
./mvnw.cmd spring-boot:run

# O usar el script
./start_api.bat
```

**Servidor corriendo en**: `http://localhost:8080`

---

## 📧 Contacto y Soporte

Para dudas sobre la integración o funcionalidades adicionales, consultar:

- Documentación Swagger: `http://localhost:8080/swagger-ui.html`
- Logs del sistema: `logs/app.log`
- Health check: `http://localhost:8080/actuator/health`

---

_Esta documentación está actualizada al 6 de agosto de 2025. Para la versión más reciente, consultar la documentación Swagger en tiempo real._
