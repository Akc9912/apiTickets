# 📚 Documentación Técnica - API Tickets

## 🚀 Guía de Integración para Desarrolladores

### 🔗 **URL Base de la API**

```
http://localhost:8080
```

### 🔐 **Autenticación JWT**

#### 1. **Login**

```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "superadmin@sistema.com",
  "password": "secret"
}
```

**Respuesta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "nombre": "Super",
    "apellido": "Admin",
    "email": "superadmin@sistema.com",
    "rol": "SUPERADMIN",
    "activo": true,
    "bloqueado": false
  }
}
```

#### 2. **Uso del Token**

```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 👥 **Roles y Permisos**

### **SUPERADMIN** (Dueño del sistema)

- ✅ Crear/editar/eliminar cualquier usuario
- ✅ Cambiar roles de usuarios
- ✅ Acceso completo a todos los tickets
- ✅ Reabrir tickets cerrados
- ✅ Estadísticas completas del sistema
- ✅ Promover/degradar administradores

### **ADMIN** (Administrador)

- ✅ Crear usuarios (excepto SuperAdmin)
- ✅ Gestionar técnicos y trabajadores
- ✅ Ver todos los tickets
- ✅ Reabrir tickets
- ✅ Estadísticas limitadas
- ❌ No puede crear otros admins

### **TECNICO** (Soporte técnico)

- ✅ Ver tickets asignados
- ✅ Tomar tickets disponibles
- ✅ Cambiar estado de tickets
- ✅ Finalizar tickets
- ✅ Devolver tickets con motivo

### **TRABAJADOR** (Usuario final)

- ✅ Crear tickets
- ✅ Ver sus propios tickets
- ✅ Validar tickets finalizados (aceptar/rechazar solución)
- ✅ Recibir notificaciones

## 🎫 **Endpoints con Ejemplos JSON**

### **🔐 Autenticación**

#### `POST /api/auth/login`

**Recibe:**

```json
{
  "email": "usuario@ejemplo.com",
  "password": "miPassword123"
}
```

**Devuelve:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "usuario@ejemplo.com",
    "rol": "TRABAJADOR",
    "activo": true,
    "bloqueado": false,
    "fechaCreacion": "2025-08-07T10:30:00"
  }
}
```

#### `POST /api/auth/cambiar-password`

**Recibe:**

```json
{
  "passwordActual": "passwordViejo123",
  "passwordNuevo": "passwordNuevo456"
}
```

**Devuelve:**

```json
{
  "mensaje": "Contraseña cambiada exitosamente",
  "fecha": "2025-08-07T10:30:00"
}
```

### **👥 Gestión de Usuarios**

#### `GET /api/usuarios/obtener-datos`

**Recibe:** Solo headers con token

**Devuelve:**

```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "usuario@ejemplo.com",
  "rol": "TRABAJADOR",
  "activo": true,
  "bloqueado": false,
  "fechaCreacion": "2025-08-07T10:30:00",
  "fechaUltimoAcceso": "2025-08-07T11:15:00"
}
```

#### `PUT /api/usuarios/editar-datos`

**Recibe:**

```json
{
  "nombre": "Juan Carlos",
  "apellido": "Pérez García",
  "email": "juan.perez@nuevoemail.com"
}
```

**Devuelve:**

```json
{
  "id": 1,
  "nombre": "Juan Carlos",
  "apellido": "Pérez García",
  "email": "juan.perez@nuevoemail.com",
  "rol": "TRABAJADOR",
  "activo": true,
  "fechaActualizacion": "2025-08-07T11:20:00"
}
```

#### `GET /api/usuarios/mis-tickets`

**Recibe:** Solo headers con token

**Devuelve:**

```json
[
  {
    "id": 1,
    "titulo": "Problema con impresora",
    "descripcion": "La impresora no funciona en la oficina 203",
    "estado": "ATENDIDO",
    "fechaCreacion": "2025-08-07T09:00:00",
    "fechaActualizacion": "2025-08-07T10:15:00",
    "tecnico": {
      "id": 3,
      "nombre": "María",
      "apellido": "González"
    }
  }
]
```

### **🔱 Endpoints SuperAdmin**

#### `GET /api/superadmin/usuarios`

**Recibe:** Solo headers con token

**Devuelve:**

```json
[
  {
    "id": 1,
    "nombre": "Super",
    "apellido": "Admin",
    "email": "superadmin@sistema.com",
    "rol": "SUPER_ADMIN",
    "activo": true,
    "bloqueado": false,
    "fechaCreacion": "2025-08-07T08:00:00"
  },
  {
    "id": 2,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@empresa.com",
    "rol": "TRABAJADOR",
    "activo": true,
    "bloqueado": false,
    "fechaCreacion": "2025-08-07T09:30:00"
  }
]
```

#### `POST /api/superadmin/usuarios`

**Recibe:**

```json
{
  "nombre": "Carlos",
  "apellido": "Rodríguez",
  "email": "carlos@empresa.com",
  "password": "password123",
  "rol": "TECNICO"
}
```

**Devuelve:**

```json
{
  "id": 4,
  "nombre": "Carlos",
  "apellido": "Rodríguez",
  "email": "carlos@empresa.com",
  "rol": "TECNICO",
  "activo": true,
  "bloqueado": false,
  "fechaCreacion": "2025-08-07T16:00:00",
  "mensaje": "Usuario creado exitosamente"
}
```

#### `PUT /api/superadmin/usuarios/{id}/rol`

**Recibe:**

```json
{
  "rol": "ADMIN"
}
```

**Devuelve:**

```json
{
  "id": 2,
  "nombre": "Juan",
  "apellido": "Pérez",
  "rol": "ADMIN",
  "fechaActualizacion": "2025-08-07T16:15:00",
  "mensaje": "Rol actualizado exitosamente"
}
```

### **⚙️ Endpoints Admin**

#### `GET /api/admin/usuarios`

**Recibe:** Solo headers con token

**Devuelve:**

```json
[
  {
    "id": 2,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@empresa.com",
    "rol": "TRABAJADOR",
    "activo": true,
    "bloqueado": false,
    "fechaCreacion": "2025-08-07T09:30:00"
  }
]
```

#### `POST /api/admin/usuarios`

**Recibe:**

```json
{
  "nombre": "Ana",
  "apellido": "López",
  "email": "ana@empresa.com",
  "password": "password456",
  "rol": "TRABAJADOR"
}
```

**Devuelve:**

```json
{
  "id": 5,
  "nombre": "Ana",
  "apellido": "López",
  "email": "ana@empresa.com",
  "rol": "TRABAJADOR",
  "activo": true,
  "fechaCreacion": "2025-08-07T16:30:00",
  "mensaje": "Usuario creado exitosamente"
}
```

#### `PUT /api/admin/usuarios/{id}/bloquear`

**Recibe:**

```json
{
  "bloqueado": true,
  "motivo": "Incumplimiento de políticas de seguridad"
}
```

**Devuelve:**

```json
{
  "id": 5,
  "nombre": "Ana",
  "apellido": "López",
  "bloqueado": true,
  "fechaBloqueo": "2025-08-07T17:00:00",
  "motivo": "Incumplimiento de políticas de seguridad",
  "mensaje": "Usuario bloqueado exitosamente"
}
```

### **🎫 Gestión de Tickets**

#### `GET /api/tickets`

**Recibe:** Solo headers con token

**Devuelve:**

```json
[
  {
    "id": 1,
    "titulo": "Problema con impresora",
    "descripcion": "La impresora HP de la oficina 203 no imprime documentos",
    "estado": "ATENDIDO",
    "fechaCreacion": "2025-08-07T09:00:00",
    "fechaActualizacion": "2025-08-07T10:15:00",
    "trabajador": {
      "id": 1,
      "nombre": "Juan",
      "apellido": "Pérez",
      "email": "juan@empresa.com"
    },
    "tecnico": {
      "id": 3,
      "nombre": "María",
      "apellido": "González",
      "email": "maria@empresa.com"
    }
  }
]
```

#### `POST /api/tickets`

**Recibe:**

```json
{
  "titulo": "Error en sistema de ventas",
  "descripcion": "El sistema de ventas se cierra inesperadamente al generar reportes"
}
```

**Devuelve:**

```json
{
  "id": 2,
  "titulo": "Error en sistema de ventas",
  "descripcion": "El sistema de ventas se cierra inesperadamente al generar reportes",
  "estado": "NO_ATENDIDO",
  "fechaCreacion": "2025-08-07T11:30:00",
  "trabajador": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@empresa.com"
  },
  "tecnico": null
}
```

#### `GET /api/tickets/{id}`

**Recibe:** Solo headers con token

**Devuelve:**

```json
{
  "id": 1,
  "titulo": "Problema con impresora",
  "descripcion": "La impresora HP de la oficina 203 no imprime documentos",
  "estado": "FINALIZADO",
  "solucion": "Se reemplazó el cartucho de tinta y se configuró el driver",
  "fechaCreacion": "2025-08-07T09:00:00",
  "fechaActualizacion": "2025-08-07T14:20:00",
  "fechaFinalizacion": "2025-08-07T14:20:00",
  "trabajador": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@empresa.com"
  },
  "tecnico": {
    "id": 3,
    "nombre": "María",
    "apellido": "González",
    "email": "maria@empresa.com"
  },
  "historiales": [
    {
      "id": 1,
      "accion": "CREADO",
      "fechaAccion": "2025-08-07T09:00:00",
      "usuario": "Juan Pérez"
    },
    {
      "id": 2,
      "accion": "ASIGNADO",
      "fechaAccion": "2025-08-07T10:15:00",
      "usuario": "María González"
    }
  ]
}
```

#### `PUT /api/tickets/{id}/estado`

**Recibe:**

```json
{
  "estado": "ATENDIDO",
  "comentario": "Ticket asignado para revisión"
}
```

**Devuelve:**

```json
{
  "id": 1,
  "estado": "ATENDIDO",
  "fechaActualizacion": "2025-08-07T10:15:00",
  "mensaje": "Estado actualizado exitosamente"
}
```

### **🔧 Endpoints para Técnicos**

#### `GET /api/tecnico/tickets/asignados`

**Recibe:** Solo headers con token

**Devuelve:**

```json
[
  {
    "id": 1,
    "titulo": "Problema con impresora",
    "descripcion": "La impresora HP de la oficina 203 no imprime documentos",
    "estado": "ATENDIDO",
    "fechaCreacion": "2025-08-07T09:00:00",
    "trabajador": {
      "id": 1,
      "nombre": "Juan",
      "apellido": "Pérez",
      "email": "juan@empresa.com"
    }
  }
]
```

#### `POST /api/tecnico/tickets/{id}/tomar`

**Descripción:** Permite al técnico tomar tickets en estado NO_ATENDIDO o REABIERTO. El ticket pasa automáticamente a ATENDIDO.

**Recibe:** Solo headers con token

**Devuelve:**

```json
{
  "id": 2,
  "titulo": "Error en sistema de ventas",
  "estado": "ATENDIDO",
  "tecnicoAsignado": {
    "id": 3,
    "nombre": "María",
    "apellido": "González"
  },
  "fechaAsignacion": "2025-08-07T11:45:00",
  "mensaje": "Ticket tomado exitosamente"
}
```

#### `POST /api/tecnico/tickets/{id}/finalizar`

**Recibe:**

```json
{
  "solucion": "Se reemplazó el cartucho de tinta y se configuró correctamente el driver de la impresora. Problema resuelto."
}
```

**Devuelve:**

```json
{
  "id": 1,
  "titulo": "Problema con impresora",
  "estado": "FINALIZADO",
  "solucion": "Se reemplazó el cartucho de tinta y se configuró correctamente el driver de la impresora. Problema resuelto.",
  "fechaFinalizacion": "2025-08-07T14:20:00",
  "mensaje": "Ticket finalizado exitosamente"
}
```

#### `POST /api/tecnico/tickets/{id}/devolver`

**Recibe:**

```json
{
  "motivo": "Requiere aprobación del supervisor para compra de repuestos"
}
```

**Devuelve:**

```json
{
  "id": 2,
  "estado": "NO_ATENDIDO",
  "motivo": "Requiere aprobación del supervisor para compra de repuestos",
  "fechaDevolucion": "2025-08-07T12:30:00",
  "mensaje": "Ticket devuelto exitosamente"
}
```

### **👷 Endpoints para Trabajadores**

#### `GET /api/trabajador/tickets`

**Recibe:** Solo headers con token

**Devuelve:**

```json
[
  {
    "id": 1,
    "titulo": "Problema con impresora",
    "descripcion": "La impresora HP de la oficina 203 no imprime documentos",
    "estado": "FINALIZADO",
    "fechaCreacion": "2025-08-07T09:00:00",
    "fechaFinalizacion": "2025-08-07T14:20:00",
    "tecnico": {
      "id": 3,
      "nombre": "María",
      "apellido": "González"
    },
    "solucion": "Se reemplazó el cartucho de tinta y se configuró el driver"
  }
]
```

#### `POST /api/trabajador/tickets`

**Recibe:**

```json
{
  "titulo": "Computadora lenta",
  "descripcion": "Mi computadora de trabajo está muy lenta desde ayer, tarda mucho en abrir programas"
}
```

**Devuelve:**

```json
{
  "id": 3,
  "titulo": "Computadora lenta",
  "descripcion": "Mi computadora de trabajo está muy lenta desde ayer, tarda mucho en abrir programas",
  "estado": "NO_ATENDIDO",
  "fechaCreacion": "2025-08-07T15:00:00",
  "trabajador": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez"
  },
  "mensaje": "Ticket creado exitosamente"
}
```

#### `POST /api/trabajador/tickets/{id}/evaluar`

**Descripción:** Validación final del trabajador después de que el técnico finaliza el ticket. El trabajador decide si acepta o rechaza la solución.

**Recibe (aceptar):**

```json
{
  "idTrabajador": 1,
  "fueResuelto": true,
  "motivoFalla": null
}
```

**Devuelve (si acepta):**

```json
{
  "id": 1,
  "estado": "RESUELTO",
  "evaluacion": {
    "fueResuelto": true,
    "fechaEvaluacion": "2025-08-07T15:30:00"
  },
  "mensaje": "Ticket marcado como resuelto exitosamente"
}
```

**Recibe (rechazar):**

```json
{
  "idTrabajador": 1,
  "fueResuelto": false,
  "motivoFalla": "El problema persiste, necesita más revisión"
}
```

**Devuelve (si rechaza):**

```json
{
  "id": 1,
  "estado": "REABIERTO",
  "evaluacion": {
    "fueResuelto": false,
    "motivoFalla": "El problema persiste, necesita más revisión",
    "fechaEvaluacion": "2025-08-07T15:30:00"
  },
  "mensaje": "Ticket reabierto para nueva revisión"
}
```

## 📊 **Estados de Tickets**

```javascript
const EstadosTicket = {
  NO_ATENDIDO: "No atendido", // Ticket recién creado
  ATENDIDO: "Atendido", // Técnico asignado trabajando en él
  RESUELTO: "Resuelto", // Técnico finalizó trabajo, esperando validación del trabajador
  FINALIZADO: "Finalizado", // Trabajador aceptó la solución - ticket cerrado
  REABIERTO: "Reabierto", // Trabajador rechazó la solución, técnico debe retomar
};
```

## 🔔 **Sistema de Notificaciones**

#### `GET /api/notificaciones?userId=1`

**Recibe:** Solo headers con token + parámetro userId

**Devuelve:**

```json
[
  {
    "id": 1,
    "mensaje": "Tu ticket 'Problema con impresora' ha sido asignado al técnico María González",
    "tipo": "ASIGNACION",
    "leida": false,
    "fechaCreacion": "2025-08-07T10:15:00",
    "ticketId": 1
  },
  {
    "id": 2,
    "mensaje": "Tu ticket 'Problema con impresora' ha sido finalizado",
    "tipo": "FINALIZACION",
    "leida": true,
    "fechaCreacion": "2025-08-07T14:20:00",
    "ticketId": 1
  }
]
```

#### `GET /api/notificaciones/count?userId=1`

**Recibe:** Solo headers con token + parámetro userId

**Devuelve:**

```json
{
  "total": 5,
  "noLeidas": 2,
  "leidas": 3
}
```

#### `DELETE /api/notificaciones?userId=1`

**Recibe:** Solo headers con token + parámetro userId

**Devuelve:**

```json
{
  "mensaje": "Notificaciones eliminadas exitosamente",
  "cantidad": 5,
  "fecha": "2025-08-07T16:00:00"
}
```

## 📈 **Estadísticas del Sistema**

#### `GET /api/estadisticas/usuarios/total`

**Recibe:** Solo headers con token

**Devuelve:**

```json
{
  "total": 25,
  "activos": 23,
  "bloqueados": 2,
  "porRol": {
    "SUPER_ADMIN": 1,
    "ADMIN": 2,
    "TECNICO": 5,
    "TRABAJADOR": 17
  }
}
```

#### `GET /api/estadisticas/tickets/total`

**Recibe:** Solo headers con token

**Devuelve:**

```json
{
  "total": 157,
  "porEstado": {
    "NO_ATENDIDO": 12,
    "ATENDIDO": 8,
    "FINALIZADO": 5,
    "RESUELTO": 132
  },
  "estadisticasHoy": {
    "creados": 3,
    "finalizados": 5,
    "resueltos": 4
  }
}
```

#### `GET /api/estadisticas/tickets/estado?estado=NO_ATENDIDO`

**Recibe:** Solo headers con token + parámetro estado

**Devuelve:**

```json
{
  "estado": "NO_ATENDIDO",
  "cantidad": 12,
  "tickets": [
    {
      "id": 15,
      "titulo": "Error en sistema de ventas",
      "fechaCreacion": "2025-08-07T15:30:00",
      "trabajador": "Juan Pérez"
    },
    {
      "id": 16,
      "titulo": "Computadora lenta",
      "fechaCreacion": "2025-08-07T16:00:00",
      "trabajador": "Ana López"
    }
  ]
}
```

## 🧪 **Datos de Prueba**

### **Usuarios por Defecto**

| Email                    | Password | Rol        | Descripción       |
| ------------------------ | -------- | ---------- | ----------------- |
| `superadmin@sistema.com` | `secret` | SUPERADMIN | Dueño del sistema |

### **Crear Usuarios de Prueba**

```bash
# Crear Admin
POST /api/superadmin/usuarios
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "admin@test.com",
  "rol": "ADMIN"
}

# Crear Técnico
POST /api/superadmin/usuarios
{
  "nombre": "María",
  "apellido": "García",
  "email": "tecnico@test.com",
  "rol": "TECNICO"
}

# Crear Trabajador
POST /api/superadmin/usuarios
{
  "nombre": "Carlos",
  "apellido": "López",
  "email": "trabajador@test.com",
  "rol": "TRABAJADOR"
}
```

## 📝 **Postman Collection**

### **Variables de Entorno**

```json
{
  "baseUrl": "http://localhost:8080",
  "token": "{{token}}"
}
```

### **Pre-request Script para Auth**

```javascript
// En requests que requieren autenticación
if (pm.environment.get("token")) {
  pm.request.headers.add({
    key: "Authorization",
    value: "Bearer " + pm.environment.get("token"),
  });
}
```

### **Test Script para Login**

```javascript
// Guardar token después del login
if (pm.response.code === 200) {
  const responseData = pm.response.json();
  pm.environment.set("token", responseData.token);
  pm.environment.set("userId", responseData.usuario.id);
}
```

## 📖 **Swagger UI**

Accede a la documentación interactiva en:

```
http://localhost:8080/swagger-ui/index.html
```

**Endpoint de OpenAPI JSON:**

```
http://localhost:8080/v3/api-docs
```

## ⚙️ **Configuración del Entorno**

### **Variables de Entorno (.env)**

```bash
# Base de datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=apiticket
DB_USERNAME=root
DB_PASSWORD=tu_password

# JWT
JWT_SECRET=tu_clave_secreta_muy_larga_y_segura
JWT_EXPIRATION=86400000

# Aplicación
SERVER_PORT=8080
PROFILE=mysql

# Logs
LOG_LEVEL=INFO
```

### **application.properties**

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/apiticket
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT
jwt.secret=${JWT_SECRET:default_secret_key}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Servidor
server.port=${SERVER_PORT:8080}

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🚀 **Deploy y Producción**

### **Setup Inicial**

```bash
# 1. Clonar repositorio
git clone https://github.com/Akc9912/apiTickets.git
cd apiTickets

# 2. Configurar base de datos
mysql -u root -p < create_database.sql

# 3. Configurar variables de entorno
cp .env.example .env
# Editar .env con tus configuraciones

# 4. Compilar y ejecutar
./mvnw clean package
java -jar target/miapi-*.jar
```

### **Verificación**

```bash
# Verificar que la API esté funcionando
curl http://localhost:8080/actuator/health

# Acceder a Swagger
open http://localhost:8080/swagger-ui/index.html
```

---

**¿Necesitas más información?** Consulta el [README.md](./README.md) principal del proyecto.
