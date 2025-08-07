# 📚 Documentación Técnica - API Tickets

## 🚀 Guía de Integración para Desarrolladores

### 🔗 **URL Base de la API**

```
http://localhost:8081
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
- ✅ Evaluar tickets finalizados
- ✅ Recibir notificaciones

## 🎫 **Endpoints Principales**

### **Autenticación**

```bash
POST /api/auth/login                    # Iniciar sesión
POST /api/auth/cambiar-password         # Cambiar contraseña
POST /api/auth/reiniciar-password       # Reiniciar contraseña (admin)
```

### **Usuarios**

```bash
GET  /api/usuarios/obtener-datos        # Obtener datos del usuario
PUT  /api/usuarios/editar-datos         # Editar datos
PUT  /api/usuarios/cambiar-password     # Cambiar contraseña
GET  /api/usuarios/mis-tickets          # Ver mis tickets
GET  /api/usuarios/notificaciones       # Ver notificaciones
```

### **SuperAdmin**

```bash
GET  /api/superadmin/usuarios           # Listar todos los usuarios
POST /api/superadmin/usuarios           # Crear usuario
PUT  /api/superadmin/usuarios/{id}      # Editar usuario
DELETE /api/superadmin/usuarios/{id}    # Eliminar usuario
PUT  /api/superadmin/usuarios/{id}/rol  # Cambiar rol
GET  /api/superadmin/tickets            # Ver todos los tickets
POST /api/superadmin/tickets/{id}/reabrir # Reabrir ticket
```

### **Administradores**

```bash
GET  /api/admin/usuarios                # Listar usuarios
POST /api/admin/usuarios                # Crear usuario (no SuperAdmin)
PUT  /api/admin/usuarios/{id}           # Editar usuario
PUT  /api/admin/usuarios/{id}/bloquear  # Bloquear usuario
POST /api/admin/tickets/{id}/reabrir    # Reabrir ticket
```

### **Tickets**

```bash
GET  /api/tickets                       # Listar todos los tickets
POST /api/tickets                       # Crear ticket
GET  /api/tickets/{id}                  # Ver ticket específico
PUT  /api/tickets/{id}/estado           # Cambiar estado
GET  /api/tickets/estado?estado=...     # Filtrar por estado
GET  /api/tickets/creador?userId=...    # Tickets por creador
```

### **Técnicos**

```bash
GET  /api/tecnico/tickets/asignados     # Ver tickets asignados
POST /api/tecnico/tickets/{id}/tomar    # Tomar ticket
POST /api/tecnico/tickets/{id}/finalizar # Finalizar ticket
POST /api/tecnico/tickets/{id}/devolver # Devolver ticket
GET  /api/tecnico/incidentes            # Ver historial de incidentes
```

### **Trabajadores**

```bash
GET  /api/trabajador/tickets            # Ver mis tickets
POST /api/trabajador/tickets            # Crear ticket
GET  /api/trabajador/tickets/activos    # Ver tickets activos
POST /api/trabajador/tickets/{id}/evaluar # Evaluar ticket
```

## 📊 **Estados de Tickets**

```javascript
const EstadosTicket = {
  NO_ATENDIDO: "No atendido", // Ticket recién creado
  ATENDIDO: "Atendido", // Técnico asignado
  FINALIZADO: "Finalizado", // Técnico finalizó trabajo
  RESUELTO: "Resuelto", // Trabajador confirmó solución
  REABIERTO: "Reabierto", // Ticket reabierto por admin
};
```

## 🔔 **Sistema de Notificaciones**

### **Obtener Notificaciones**

```bash
GET /api/notificaciones?userId=1
```

### **Contar Notificaciones**

```bash
GET /api/notificaciones/count?userId=1
```

### **Eliminar Notificaciones**

```bash
DELETE /api/notificaciones?userId=1
```

## 📈 **Estadísticas**

### **Generales**

```bash
GET /api/estadisticas/usuarios/total
GET /api/estadisticas/tickets/total
GET /api/estadisticas/tecnicos/total
GET /api/estadisticas/trabajadores/total
```

### **Por Estado**

```bash
GET /api/estadisticas/tickets/estado?estado=No atendido
```

## 🛠️ **Integración Frontend**

### **React/Next.js Example**

```javascript
// API Client
class TicketAPI {
  constructor(baseURL = "http://localhost:8081") {
    this.baseURL = baseURL;
    this.token = localStorage.getItem("token");
  }

  async login(email, password) {
    const response = await fetch(`${this.baseURL}/api/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    const data = await response.json();

    if (response.ok) {
      this.token = data.token;
      localStorage.setItem("token", data.token);
      localStorage.setItem("user", JSON.stringify(data.usuario));
    }

    return data;
  }

  async getTickets() {
    const response = await fetch(`${this.baseURL}/api/tickets`, {
      headers: {
        Authorization: `Bearer ${this.token}`,
      },
    });

    return response.json();
  }

  async createTicket(ticketData) {
    const response = await fetch(`${this.baseURL}/api/tickets`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${this.token}`,
      },
      body: JSON.stringify(ticketData),
    });

    return response.json();
  }
}

// Hook para React
import { useState, useEffect } from "react";

export function useTickets() {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const api = new TicketAPI();

  useEffect(() => {
    api
      .getTickets()
      .then(setTickets)
      .finally(() => setLoading(false));
  }, []);

  return { tickets, loading, api };
}
```

### **Vue.js Example**

```javascript
// composables/useAPI.js
import { ref, computed } from "vue";

export function useTicketAPI() {
  const token = ref(localStorage.getItem("token"));
  const user = ref(JSON.parse(localStorage.getItem("user") || "null"));

  const isAuthenticated = computed(() => !!token.value);

  const api = {
    async login(email, password) {
      const response = await fetch("http://localhost:8081/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      if (response.ok) {
        token.value = data.token;
        user.value = data.usuario;
        localStorage.setItem("token", data.token);
        localStorage.setItem("user", JSON.stringify(data.usuario));
      }

      return data;
    },

    async request(endpoint, options = {}) {
      return fetch(`http://localhost:8081${endpoint}`, {
        ...options,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token.value}`,
          ...options.headers,
        },
      });
    },
  };

  return { api, isAuthenticated, user };
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
  "baseUrl": "http://localhost:8081",
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
http://localhost:8081/swagger-ui/index.html
```

**Endpoint de OpenAPI JSON:**

```
http://localhost:8081/v3/api-docs
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
SERVER_PORT=8081
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
server.port=${SERVER_PORT:8081}

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
curl http://localhost:8081/actuator/health

# Acceder a Swagger
open http://localhost:8081/swagger-ui/index.html
```

---

**¿Necesitas más información?** Consulta el [README.md](./README.md) principal del proyecto.
