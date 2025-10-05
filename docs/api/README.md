# 📡 API Documentation - Endpoints

Documentación completa de todos los endpoints disponibles en la API de ApiTickets.

## 📋 Tabla de Contenidos

- [🔗 Base URL y Versioning](#base-url-y-versioning)
- [🔐 Autenticación](#autenticación)
- [👤 Endpoints de Autenticación](#endpoints-de-autenticación)
- [🎫 Endpoints de Tickets](#endpoints-de-tickets)
- [👥 Endpoints de Usuarios](#endpoints-de-usuarios)
- [🔧 Endpoints de Técnicos](#endpoints-de-técnicos)
- [📊 Endpoints de Estadísticas](#endpoints-de-estadísticas)
- [🔔 Endpoints de Notificaciones](#endpoints-de-notificaciones)
- [📋 Endpoints de Auditoría](#endpoints-de-auditoría)
- [⚠️ Códigos de Error](#códigos-de-error)
- [📝 Ejemplos de Uso](#ejemplos-de-uso)

---

## 🔗 Base URL y Versioning

### Base URL
```
Production:  https://api.apitickets.com/api
Staging:     https://staging-api.apitickets.com/api
Development: http://localhost:8080/api
```

### API Versioning
La API utiliza **base path** sin versioning explícito:
- `/api/` - Versión actual

### Content Type
```http
Content-Type: application/json
Accept: application/json
```

---

## 🔐 Autenticación

### JWT Token
Todos los endpoints (excepto login y register) requieren autenticación JWT:

```http
Authorization: Bearer <jwt_token>
```

### Roles Disponibles
- `SUPER_ADMIN` - Acceso total al sistema
- `ADMIN` - Gestión de usuarios y tickets
- `TECNICO` - Resolución de tickets asignados
- `TRABAJADOR` - Creación y seguimiento de tickets

---

## 👤 Endpoints de Autenticación

### 🔑 POST /api/auth/login
Autenticar usuario y obtener token JWT.

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "nombre": "Juan Pérez",
      "email": "usuario@example.com",
      "rol": "TRABAJADOR",
      "activo": true
    }
  },
  "message": "Autenticación exitosa"
}
```

**Errores:**
- `400` - Datos inválidos
- `401` - Credenciales incorrectas
- `423` - Usuario bloqueado

---

### � POST /api/auth/cambiar-password
Cambiar contraseña del usuario autenticado.

**Headers:**
```http
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "userId": 1,
  "currentPassword": "currentPassword123",
  "newPassword": "newSecurePassword456"
}
```

**Response (200 OK):**
```json
"Contraseña actualizada correctamente"
```

---

### � POST /api/auth/reiniciar-password
Reiniciar contraseña de un usuario (solo ADMIN/SUPERADMIN).

**Headers:**
```http
Authorization: Bearer <admin_token>
```

**Request Body:**
```json
{
  "userId": 2,
  "newPassword": "temporalPassword123"
}
```

**Response (200 OK):**
```json
"Contraseña reiniciada correctamente"
```
  "data": {
    "token": "new_jwt_token_here",
    "expiresIn": 86400
  }
}
```

---

---

## 🎫 Endpoints de Tickets

### 📋 GET /api/tickets/todos
Obtener todos los tickets del sistema (solo Admin/SuperAdmin).

**Headers:**
```http
Authorization: Bearer <admin_token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "titulo": "Error en sistema de facturación",
    "descripcion": "El sistema no permite generar facturas",
    "estado": "NO_ATENDIDO",
    "prioridad": "ALTA",
    "categoria": "SISTEMA",
    "fechaCreacion": "2025-10-01T09:00:00Z",
    "fechaAsignacion": null,
    "fechaResolucion": null,
    "creadorId": 2,
    "tecnicoAsignadoId": null
  }
]
```

---

### 👷 GET /api/tickets/trabajador/mis-tickets
Obtener tickets creados por el trabajador autenticado.

**Headers:**
```http
Authorization: Bearer <trabajador_token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "titulo": "Problema con impresora",
    "descripcion": "La impresora no responde",
    "estado": "ATENDIDO",
    "prioridad": "NORMAL",
    "fechaCreacion": "2025-10-01T09:00:00Z",
    "tecnicoAsignadoId": 5
  }
]
```

---

### 📝 GET /api/tickets/trabajador/tickets-para-evaluar
Obtener tickets en estado RESUELTO para evaluación del trabajador.

**Headers:**
```http
Authorization: Bearer <trabajador_token>
```

**Response (200 OK):**
```json
[
  {
    "id": 3,
    "titulo": "Ticket resuelto pendiente evaluación",
    "estado": "RESUELTO",
    "fechaResolucion": "2025-10-01T15:30:00Z",
    "tecnicoAsignadoId": 7
  }
]
```

---

### 🔧 GET /api/tickets/tecnico/tickets-disponibles
Obtener tickets no asignados y reabiertos para técnicos.

**Headers:**
```http
Authorization: Bearer <tecnico_token>
```

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "titulo": "Sistema lento",
    "estado": "NO_ATENDIDO",
    "prioridad": "ALTA",
    "fechaCreacion": "2025-10-01T10:00:00Z"
  }
]
```

---

### 🔧 GET /api/tickets/tecnico/mis-tickets
Obtener tickets asignados al técnico autenticado.

**Headers:**
```http
Authorization: Bearer <tecnico_token>
```

**Response (200 OK):**
```json
[
  {
    "id": 4,
    "titulo": "Error de red",
    "estado": "ATENDIDO",
    "fechaAsignacion": "2025-10-01T11:00:00Z"
  }
]
```

---

### 📚 GET /api/tickets/tecnico/historial
Obtener historial de tickets donde el técnico participó.

**Headers:**
```http
Authorization: Bearer <tecnico_token>
```

**Response (200 OK):**
```json
[
  {
    "id": 6,
    "titulo": "Ticket finalizado anteriormente",
    "estado": "FINALIZADO",
    "fechaFinalizacion": "2025-09-30T16:00:00Z"
  }
]
```

---

### ➕ POST /api/tickets/crear-ticket
Crear un nuevo ticket según el rol del usuario autenticado.

**Headers:**
```http
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "titulo": "Error en sistema de facturación",
  "descripcion": "El sistema no permite generar facturas",
  "prioridad": "ALTA",
  "categoria": "SISTEMA"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "titulo": "Error en sistema de facturación",
  "descripcion": "El sistema no permite generar facturas",
  "estado": "NO_ATENDIDO",
  "prioridad": "ALTA",
  "categoria": "SISTEMA",
  "fechaCreacion": "2025-10-01T09:00:00Z",
  "creadorId": 2,
  "tecnicoAsignadoId": null
}

---

### 🔄 POST /api/tickets/{id}/reabrir
Reabrir un ticket finalizado o resuelto.

**Path Parameters:**
- `id` (int) - ID del ticket

**Headers:**
```http
Authorization: Bearer <token>
```

**Query Parameters:**
- `comentario` (string, required) - Razón para reabrir el ticket

**Ejemplo Request:**
```http
POST /api/tickets/1/reabrir?comentario=El problema persiste después de la solución aplicada
```

**Response (200 OK):**
```json
{
  "id": 1,
  "titulo": "Error en sistema de facturación",
  "estado": "REABIERTO",
  "fechaReapertura": "2025-10-01T16:00:00Z"
    "tecnico": {
      "id": 5,
      "nombre": "Carlos Técnico",
      "email": "carlos@example.com"
    },
    "fechaAsignacion": "2025-10-01T15:00:00Z"
  },
  "message": "Ticket asignado exitosamente"
}
```

---

### ✅ PUT /tickets/{id}/resolver
Marcar ticket como resuelto (solo TECNICO asignado o ADMIN).

**Request Body:**
```json
{
  "solucion": "Se reinició el servicio de facturación y se actualizó la configuración de la base de datos. El problema se debía a un timeout en la conexión.",
  "tiempoTrabajado": 120,
  "requiereAprobacion": true
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "estado": "RESUELTO",
    "solucion": "Se reinició el servicio de facturación...",
    "fechaResolucion": "2025-10-01T16:30:00Z",
    "tiempoResolucion": "7.5 horas",
    "requiereAprobacion": true
  },
  "message": "Ticket resuelto exitosamente"
}
```

---

---

## 👥 Endpoints de Usuarios

### ➕ POST /api/usuarios/crear
Crear un nuevo usuario (solo Admin/SuperAdmin).

**Headers:**
```http
Authorization: Bearer <admin_token>
```

**Request Body:**
```json
{
  "nombre": "María García",
  "email": "maria@example.com",
  "password": "securePassword123",
  "rol": "TRABAJADOR",
  "telefono": "+1234567890",
  "departamento": "IT Support"
}
```

**Response (201 Created):**
```json
{
  "id": 2,
  "nombre": "María García",
  "email": "maria@example.com",
  "rol": "TRABAJADOR",
  "activo": true,
  "fechaCreacion": "2025-10-01T10:00:00Z",
  "telefono": "+1234567890",
  "departamento": "IT Support"
}
```

**Errores:**
- `403` - No autorizado (solo Admin/SuperAdmin pueden crear usuarios)
- `400` - Admin no puede crear SuperAdmin
- `409` - Email ya existe

---
Crear nuevo usuario (solo ADMIN/SUPER_ADMIN).

**Request Body:**
```json
{
  "nombre": "Ana López",
  "email": "ana@example.com",
  "password": "SecurePass123!",
  "rol": "TECNICO",
  "telefono": "+1234567891",
  "departamento": "Soporte Técnico",
  "especialidad": "Hardware"
}
```

---

### 🔄 PUT /usuarios/{id}
Actualizar usuario existente.

**Request Body:**
```json
{
  "nombre": "Ana López Gómez",
  "telefono": "+1234567892",
  "departamento": "Soporte Técnico Senior",
  "activo": true
}
```

---

### 🔐 PUT /usuarios/{id}/cambiar-password
Cambiar password de usuario.

**Request Body:**
```json
{
  "passwordActual": "oldPassword123",
  "passwordNueva": "newSecurePass456!",
  "confirmarPassword": "newSecurePass456!"
}
```

---

## 🔧 Endpoints de Técnicos

### 🔧 GET /tecnicos
Obtener lista de técnicos disponibles.

**Query Parameters:**
- `disponible` - true/false para filtrar disponibilidad
- `especialidad` - Filtrar por especialidad

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": 5,
      "nombre": "Carlos Técnico",
      "email": "carlos@example.com",
      "especialidad": "Sistemas",
      "disponible": true,
      "cargaTrabajo": {
        "ticketsAsignados": 3,
        "ticketsPendientes": 1,
        "ticketsResueltos": 15
      },
      "calificacionPromedio": 4.8,
      "tiempoPromedioResolucion": "3.2 horas"
    }
  ]
}
```

---

### 🔧 GET /tecnicos/{id}/tickets
Obtener tickets asignados a un técnico específico.

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "tecnico": {
      "id": 5,
      "nombre": "Carlos Técnico",
      "especialidad": "Sistemas"
    },
    "tickets": {
      "pendientes": [
        {
          "id": 1,
          "titulo": "Error en sistema de facturación",
          "prioridad": "ALTA",
          "fechaAsignacion": "2025-10-01T10:30:00Z",
          "tiempoTranscurrido": "8 horas"
        }
      ],
      "enProceso": [],
      "resueltos": [
        {
          "id": 10,
          "titulo": "Problema con email",
          "fechaResolucion": "2025-09-30T16:00:00Z",
          "tiempoResolucion": "2.5 horas"
        }
      ]
    },
    "estadisticas": {
      "totalAsignados": 3,
      "totalResueltos": 15,
      "tiempoPromedioResolucion": "3.2 horas",
      "satisfaccionCliente": 4.8
    }
  }
}
```

---

## 📊 Endpoints de Estadísticas

### 📊 GET /estadisticas/dashboard
Obtener datos para dashboard principal (solo ADMIN/SUPER_ADMIN).

**Query Parameters:**
- `periodo` - DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL
- `fechaDesde` - Fecha inicio del período
- `fechaHasta` - Fecha fin del período

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "resumen": {
      "totalTickets": 125,
      "ticketsPendientes": 15,
      "ticketsEnProceso": 8,
      "ticketsResueltos": 102,
      "porcentajeResolucion": 81.6,
      "tiempoPromedioResolucion": "4.2 horas"
    },
    "porPrioridad": {
      "URGENTE": 5,
      "ALTA": 12,
      "NORMAL": 85,
      "BAJA": 23
    },
    "porCategoria": {
      "SISTEMA": 45,
      "HARDWARE": 35,
      "SOFTWARE": 30,
      "RED": 15
    },
    "tendenciaSemanal": [
      {
        "fecha": "2025-09-24",
        "ticketsCreados": 18,
        "ticketsResueltos": 16
      },
      {
        "fecha": "2025-09-25",
        "ticketsCreados": 22,
        "ticketsResueltos": 20
      }
    ],
    "rendimientoTecnicos": [
      {
        "tecnico": "Carlos Técnico",
        "ticketsResueltos": 25,
        "tiempoPromedio": "3.2 horas",
        "satisfaccion": 4.8
      }
    ]
  }
}
```

---

### 📈 GET /estadisticas/periodo
Obtener estadísticas por período específico.

**Query Parameters:**
- `tipo` - DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL
- `anio` - Año a consultar
- `mes` - Mes (para MENSUAL)
- `trimestre` - Trimestre (para TRIMESTRAL)

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "periodoTipo": "MENSUAL",
      "anio": 2025,
      "mes": 10,
      "ticketsCreados": 45,
      "ticketsAsignados": 43,
      "ticketsResueltos": 38,
      "ticketsFinalizados": 35,
      "ticketsReabiertos": 2,
      "tiempoPromedioAsignacion": 1.5,
      "tiempoPromedioResolucion": 4.2,
      "porcentajeAprobacion": 92.1,
      "calculadoEn": "2025-10-01T00:00:00Z"
    }
  ]
}
```

---

### 👨‍🔧 GET /estadisticas/tecnicos
Obtener estadísticas de rendimiento por técnico.

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "tecnico": {
        "id": 5,
        "nombre": "Carlos Técnico",
        "especialidad": "Sistemas"
      },
      "estadisticas": {
        "ticketsAsignados": 30,
        "ticketsCompletados": 28,
        "ticketsAprobados": 26,
        "ticketsRechazados": 2,
        "tiempoPromedioResolucion": 3.2,
        "porcentajeAprobacion": 92.8,
        "ticketsDentroSLA": 25,
        "porcentajeSLA": 89.3
      },
      "ranking": 1,
      "insignias": ["TOP_PERFORMER", "FAST_RESOLVER", "HIGH_QUALITY"]
    }
  ]
}
```

---

## 🔔 Endpoints de Notificaciones

### 🔔 GET /notificaciones
Obtener notificaciones del usuario actual.

**Query Parameters:**
- `estado` - NO_LEIDA, LEIDA, ARCHIVADA
- `tipo` - Tipo específico de notificación
- `page`, `size` - Paginación

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "titulo": "Ticket Asignado",
        "mensaje": "Se te ha asignado el ticket #TK-2025-000001: Error en sistema de facturación",
        "tipo": "TICKET_ASIGNADO",
        "categoria": "TICKETS",
        "estado": "NO_LEIDA",
        "prioridad": "ALTA",
        "fechaCreacion": "2025-10-01T10:30:00Z",
        "ticket": {
          "id": 1,
          "titulo": "Error en sistema de facturación"
        },
        "requiereAccion": true,
        "accionUrl": "/tickets/1",
        "accionTexto": "Ver Ticket"
      }
    ],
    "resumen": {
      "totalNoLeidas": 5,
      "totalLeidas": 15,
      "totalArchivadas": 3
    }
  }
}
```

---

### 👁️ PUT /notificaciones/{id}/marcar-leida
Marcar notificación como leída.

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "estado": "LEIDA",
    "fechaLectura": "2025-10-01T15:45:00Z"
  },
  "message": "Notificación marcada como leída"
}
```

---

### 📥 PUT /notificaciones/marcar-todas-leidas
Marcar todas las notificaciones como leídas.

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "notificacionesActualizadas": 8
  },
  "message": "Todas las notificaciones marcadas como leídas"
}
```

---

## 📋 Endpoints de Auditoría

### 🔍 GET /auditoria
Obtener logs de auditoría (solo ADMIN/SUPER_ADMIN).

**Query Parameters:**
- `usuario` - Filtrar por usuario
- `accion` - Tipo de acción
- `entidadTipo` - Tipo de entidad afectada
- `fechaDesde`, `fechaHasta` - Rango de fechas
- `page`, `size` - Paginación

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "nombreUsuario": "Admin Sistema",
        "accion": "ASSIGN_TICKET",
        "entidadTipo": "TICKET",
        "entidadId": 1,
        "descripcion": "Ticket asignado a técnico Carlos",
        "ipAddress": "192.168.1.100",
        "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)...",
        "valoresAnteriores": {
          "tecnicoId": null,
          "estado": "PENDIENTE"
        },
        "valoresNuevos": {
          "tecnicoId": 5,
          "estado": "ASIGNADO"
        },
        "resultado": "SUCCESS",
        "fecha": "2025-10-01T10:30:00Z"
      }
    ],
    "totalElements": 1250,
    "totalPages": 63
  }
}
```

---

## ⚠️ Códigos de Error

### Estructura de Error Estándar
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Los datos proporcionados no son válidos",
    "details": [
      {
        "field": "email",
        "message": "El email no tiene un formato válido"
      },
      {
        "field": "password",
        "message": "La contraseña debe tener al menos 8 caracteres"
      }
    ],
    "timestamp": "2025-10-01T15:30:00Z",
    "path": "/api/v1/auth/register"
  }
}
```

### Códigos HTTP y Errores

| Código | Descripción | Casos Comunes |
|--------|-------------|---------------|
| **200** | OK | Operación exitosa |
| **201** | Created | Recurso creado exitosamente |
| **400** | Bad Request | Datos inválidos, validación fallida |
| **401** | Unauthorized | Token inválido o expirado |
| **403** | Forbidden | Sin permisos para la operación |
| **404** | Not Found | Recurso no encontrado |
| **409** | Conflict | Conflicto (ej: email duplicado) |
| **422** | Unprocessable Entity | Error de lógica de negocio |
| **429** | Too Many Requests | Rate limiting activado |
| **500** | Internal Server Error | Error interno del servidor |

### Errores Específicos del Dominio

```json
{
  "success": false,
  "error": {
    "code": "TICKET_ALREADY_ASSIGNED",
    "message": "El ticket ya está asignado a otro técnico",
    "details": {
      "ticketId": 1,
      "currentTechnician": "Carlos Técnico",
      "assignedAt": "2025-10-01T10:30:00Z"
    }
  }
}
```

---

## 📝 Ejemplos de Uso

### 🔄 Flujo Completo: Crear y Resolver Ticket

#### 1. Autenticarse
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "trabajador@example.com",
    "password": "password123"
  }'
```

#### 2. Crear Ticket
```bash
curl -X POST http://localhost:8080/api/v1/tickets \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Problema con impresora",
    "descripcion": "La impresora no responde",
    "prioridad": "NORMAL",
    "categoria": "HARDWARE"
  }'
```

#### 3. Asignar Ticket (como Admin)
```bash
curl -X PUT http://localhost:8080/api/v1/tickets/1/asignar \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "tecnicoId": 5,
    "comentario": "Asignado por especialidad en hardware"
  }'
```

#### 4. Resolver Ticket (como Técnico)
```bash
curl -X PUT http://localhost:8080/api/v1/tickets/1/resolver \
  -H "Authorization: Bearer <tecnico_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "solucion": "Se reemplazó el cartucho de tinta y se reinició la impresora",
    "tiempoTrabajado": 30
  }'
```

---

### 📊 Obtener Dashboard de Estadísticas

```bash
curl -X GET "http://localhost:8080/api/v1/estadisticas/dashboard?periodo=MENSUAL" \
  -H "Authorization: Bearer <admin_token>"
```

---

### 🔔 Gestionar Notificaciones

```bash
# Obtener notificaciones no leídas
curl -X GET "http://localhost:8080/api/v1/notificaciones?estado=NO_LEIDA" \
  -H "Authorization: Bearer <token>"

# Marcar como leída
curl -X PUT http://localhost:8080/api/v1/notificaciones/1/marcar-leida \
  -H "Authorization: Bearer <token>"
```

---

## 🔗 Recursos Adicionales

### 📖 Documentación Interactiva
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI Spec**: `http://localhost:8080/v3/api-docs`

### 🛠️ Herramientas de Testing
- **Postman Collection**: [Disponible en el repo](../postman/)
- **Insomnia Workspace**: [Import file](../insomnia/)

### 📞 Soporte
- **GitHub Issues**: [Reportar problemas](https://github.com/Akc9912/apiTickets/issues)
- **Email**: akc9912@gmail.com

---

## 📄 Versionado de API

### Política de Versionado
- **Major Version**: Cambios que rompen compatibilidad
- **Minor Version**: Nuevas funcionalidades sin romper compatibilidad
- **Patch Version**: Bug fixes y mejoras menores

### Deprecation Policy
- **Notice Period**: 6 meses antes de remover endpoints
- **Migration Guide**: Documentación completa para migrar
- **Backward Compatibility**: Mantenida por al menos 12 meses

---

*Última actualización: Octubre 2025*
