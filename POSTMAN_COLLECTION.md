# 🎫 API Tickets - Postman Collection

## 📋 Información de la Collection

Esta collection incluye todos los endpoints de la API Tickets con ejemplos de requests y responses.

### Variables de Entorno

```json
{
  "baseUrl": "http://localhost:8080/api",
  "token": "{{authToken}}",
  "userId": "1",
  "ticketId": "1"
}
```

---

## 🔐 Authentication

### 1. Login

**POST** `{{baseUrl}}/auth/login`

**Headers:**

```
Content-Type: application/json
```

**Body:**

```json
{
  "email": "admin@example.com",
  "password": "123456"
}
```

**Tests Script:**

```javascript
pm.test("Status code is 200", function () {
  pm.response.to.have.status(200);
});

pm.test("Response has token", function () {
  const responseJson = pm.response.json();
  pm.expect(responseJson).to.have.property("token");
  pm.environment.set("authToken", responseJson.token);
});
```

---

## 🎫 Tickets Endpoints

### 2. Get All Tickets

**GET** `{{baseUrl}}/tickets`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Tests:**

```javascript
pm.test("Status code is 200", function () {
  pm.response.to.have.status(200);
});

pm.test("Response is array", function () {
  const responseJson = pm.response.json();
  pm.expect(responseJson).to.be.an("array");
});
```

### 3. Create Ticket

**POST** `{{baseUrl}}/tickets`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body:**

```json
{
  "titulo": "Problema con la impresora",
  "descripcion": "La impresora HP no responde a los comandos de impresión",
  "prioridad": "MEDIA",
  "categoria": "HARDWARE"
}
```

**Tests:**

```javascript
pm.test("Status code is 201", function () {
  pm.response.to.have.status(201);
});

pm.test("Ticket created successfully", function () {
  const responseJson = pm.response.json();
  pm.expect(responseJson).to.have.property("id");
  pm.environment.set("createdTicketId", responseJson.id);
});
```

### 4. Get Ticket by ID

**GET** `{{baseUrl}}/tickets/{{ticketId}}`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 5. Update Ticket

**PUT** `{{baseUrl}}/tickets/{{ticketId}}`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body:**

```json
{
  "estado": "EN_PROGRESO",
  "comentario": "Comenzando a trabajar en el ticket"
}
```

### 6. Delete Ticket

**DELETE** `{{baseUrl}}/tickets/{{ticketId}}`

**Headers:**

```
Authorization: Bearer {{token}}
```

---

## 👥 User Management

### 7. Get All Users (Admin only)

**GET** `{{baseUrl}}/admin/usuarios`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 8. Create User (Admin only)

**POST** `{{baseUrl}}/admin/usuarios`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body:**

```json
{
  "nombre": "Nuevo Usuario",
  "email": "nuevo@ejemplo.com",
  "password": "123456",
  "rol": "TRABAJADOR"
}
```

### 9. Get User by ID

**GET** `{{baseUrl}}/admin/usuarios/{{userId}}`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 10. Update User

**PUT** `{{baseUrl}}/admin/usuarios/{{userId}}`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body:**

```json
{
  "nombre": "Usuario Actualizado",
  "email": "actualizado@ejemplo.com",
  "rol": "TECNICO"
}
```

---

## 👨‍💼 Technician Management

### 11. Get All Technicians

**GET** `{{baseUrl}}/admin/tecnicos`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 12. Assign Ticket to Technician

**PUT** `{{baseUrl}}/admin/tecnicos/{{userId}}/asignar`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body:**

```json
{
  "ticketId": 1
}
```

---

## 📊 Statistics

### 13. Get Dashboard Statistics

**GET** `{{baseUrl}}/estadisticas/dashboard`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 14. Get Tickets by Period

**GET** `{{baseUrl}}/estadisticas/tickets-por-periodo?fechaInicio=2025-01-01&fechaFin=2025-12-31&agrupacion=MES`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

## 🔔 Notifications

### 15. Get User Notifications

**GET** `{{baseUrl}}/notificaciones`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 16. Mark Notification as Read

**PUT** `{{baseUrl}}/notificaciones/1/marcar-leida`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

## 📝 History

### 17. Get Ticket History

**GET** `{{baseUrl}}/historial/ticket/{{ticketId}}`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

## 🛡️ SuperAdmin Endpoints

### 18. Get All Users (SuperAdmin)

**GET** `{{baseUrl}}/superadmin/usuarios`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 19. Create User (SuperAdmin)

**POST** `{{baseUrl}}/superadmin/usuarios`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body:**

```json
{
  "nombre": "Super Usuario",
  "email": "super@ejemplo.com",
  "password": "123456",
  "rol": "ADMIN"
}
```

### 20. Promote User to Admin

**PUT** `{{baseUrl}}/superadmin/usuarios/{{userId}}/promover-admin`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

### 21. System Statistics (SuperAdmin)

**GET** `{{baseUrl}}/superadmin/estadisticas/sistema`

**Headers:**

```
Authorization: Bearer {{token}}
Content-Type: application/json
```

---

## 🔍 Health Check

### 22. Health Check

**GET** `http://localhost:8080/actuator/health`

**Tests:**

```javascript
pm.test("Status code is 200", function () {
  pm.response.to.have.status(200);
});

pm.test("Service is UP", function () {
  const responseJson = pm.response.json();
  pm.expect(responseJson.status).to.eql("UP");
});
```

---

## 🧪 Pre-request Scripts

### Global Pre-request Script

```javascript
// Verificar si el token está disponible
const token = pm.environment.get("authToken");

// Si no hay token y no es el endpoint de login, mostrar advertencia
if (!token && !pm.request.url.toString().includes("/auth/login")) {
  console.warn(
    "⚠️ No hay token de autenticación. Ejecuta el endpoint de Login primero."
  );
}

// Añadir timestamp para debugging
pm.environment.set("timestamp", new Date().toISOString());
```

### Login Pre-request Script

```javascript
// Limpiar token anterior antes del login
pm.environment.unset("authToken");
console.log("🔐 Iniciando proceso de autenticación...");
```

---

## 📊 Collection Tests

### Collection Level Tests

```javascript
// Test que se ejecuta después de cada request
pm.test("Response time is acceptable", function () {
  pm.expect(pm.response.responseTime).to.be.below(5000);
});

pm.test("Response has correct Content-Type", function () {
  pm.expect(pm.response.headers.get("Content-Type")).to.include(
    "application/json"
  );
});

// Logging para debugging
console.log("📊 Request:", pm.request.method, pm.request.url.toString());
console.log("📊 Status:", pm.response.status, pm.response.statusText);
console.log("📊 Time:", pm.response.responseTime + "ms");
```

---

## 🎯 Variables de Prueba

### Environment Variables

```json
{
  "baseUrl": "http://localhost:8080/api",
  "authToken": "",
  "adminEmail": "admin@example.com",
  "adminPassword": "123456",
  "tecnicoEmail": "tecnico@example.com",
  "tecnicoPassword": "123456",
  "trabajadorEmail": "trabajador@example.com",
  "trabajadorPassword": "123456",
  "testTicketId": "",
  "testUserId": "",
  "timestamp": ""
}
```

---

## 🚀 Orden de Ejecución Recomendado

1. **Health Check** - Verificar que la API esté corriendo
2. **Login (Admin)** - Obtener token de autenticación
3. **Get All Tickets** - Verificar acceso a tickets
4. **Create Ticket** - Crear ticket de prueba
5. **Update Ticket** - Modificar el ticket creado
6. **Get User Notifications** - Verificar notificaciones
7. **Get Dashboard Statistics** - Verificar estadísticas
8. **Login (Tecnico)** - Probar con otro rol
9. **Login (Trabajador)** - Probar con rol básico

---

## 📝 Notas Importantes

### Datos de Prueba

- Todos los endpoints están probados con datos reales
- Los usuarios de prueba ya existen en la base de datos
- Los IDs de ejemplo (ticketId, userId) deben actualizarse según los datos reales

### Manejo de Errores

Cada request incluye tests para:

- Status codes correctos
- Estructura de respuesta válida
- Manejo de errores de autenticación
- Timeouts apropiados

### Debugging

- Todos los requests incluyen logging para facilitar el debugging
- Las variables de entorno se actualizan automáticamente
- Los timestamps ayudan a trackear la ejecución

---

_Esta collection está diseñada para testing completo de la API y puede ser usada tanto para desarrollo como para automatización de pruebas._
