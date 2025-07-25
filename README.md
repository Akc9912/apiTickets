# 🎫 ApiTickets - Sistema de Gestión de Tickets

<div align="center">

![Java](https://img.shields.io/badge/Java-24+-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)
![JWT](https://img.shields.io/badge/JWT-Auth-red.svg)
![Maven](https://img.shields.io/badge/Maven-3.9+-purple.svg)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-brightgreen.svg)
![Version](https://img.shields.io/badge/Version-0.2.0--SNAPSHOT-yellow.svg)

**Sistema completo de gestión de tickets para soporte técnico con arquitectura moderna, escalable y documentación interactiva**

[Características](#-características) • [Instalación](#-instalación) • [API Documentation](#-api-documentation) • [Arquitectura](#-arquitectura)

</div>

---

## 📋 Descripción

**ApiTickets** es un sistema robusto de gestión de tickets diseñado para organizaciones que necesitan un control eficiente de sus solicitudes de soporte técnico. Construido con **Spring Boot** y **MySQL**, ofrece una arquitectura escalable con autenticación JWT, un sistema de roles jerárquico y **documentación interactiva con Swagger**.

### 🎯 Problema que Resuelve

- **Desorganización** en el manejo de solicitudes de soporte
- **Falta de trazabilidad** en la resolución de incidencias
- **Ausencia de roles y permisos** claros
- **Dificultad para generar reportes** y estadísticas
- **Documentación API obsoleta o inexistente**

### 💡 Solución Ofrecida

Un sistema centralizado que permite gestionar tickets desde su creación hasta su resolución, con roles diferenciados, notificaciones automáticas y reportes en tiempo real.

---

## ✨ Características

### � **Documentación API Interactiva**

- **Swagger UI integrado** para exploración y pruebas en tiempo real
- **OpenAPI 3.0** con especificaciones completas de todos los endpoints
- **Documentación automática** que se actualiza con el código
- **Testing interactivo** desde la interfaz web
- **Ejemplos de request/response** para cada endpoint

### �🔐 **Sistema de Autenticación Avanzado**

- Autenticación JWT segura con tokens de larga duración
- Cambio obligatorio de contraseña en primer acceso
- Sistema de reset de contraseñas por administradores

### 👥 **Gestión de Usuarios Jerárquica**

- **SuperAdmin**: Dueño del sistema con acceso total
- **Admin**: Gestión de usuarios y tickets del sistema
- **Técnico**: Resolución y gestión de tickets asignados
- **Trabajador**: Creación y seguimiento de tickets propios

### 🎫 **Gestión Completa de Tickets**

- Creación, asignación y seguimiento de tickets
- Estados automáticos: `NO_ATENDIDO` → `ATENDIDO` → `RESUELTO` → `FINALIZADO`
- Posibilidad de reapertura con comentarios
- Historial completo de cambios y asignaciones

### 🔔 **Sistema de Notificaciones**

- Notificaciones automáticas por cambios de estado
- Alertas por asignación de tickets
- Notificaciones de bloqueo y desbloqueo de usuarios

### 📊 **Reportes y Estadísticas**

- Métricas de rendimiento por técnico
- Estadísticas de tickets por estado y período
- Reportes de usuarios activos y bloqueados
- Dashboard de administración con KPIs

### 🛡️ **Seguridad y Auditoría**

- Logs automáticos de todas las acciones
- Sistema de marcas por fallos técnicos
- Bloqueo automático por exceso de fallos
- Trazabilidad completa de cambios

---

## 🚀 Instalación

### Prerrequisitos

```bash
# Verificar versiones
java --version    # Java 17+
mvn --version     # Maven 3.8+
mysql --version   # MySQL 8.0+
```

### 1️⃣ Configuración de Base de Datos

```sql
-- Crear base de datos
CREATE DATABASE apiticket;

-- Ejecutar script de inicialización
source init_ticket_system.sql;
```

### 2️⃣ Configuración del Proyecto

```bash
# Clonar repositorio
git clone https://github.com/Akc9912/apiTickets.git
cd apiTickets

# Configurar variables de entorno
cp .env.example .env
```

**Variables de entorno (.env):**

```properties
DB_URL=jdbc:mysql://localhost:3306/apiticket?useSSL=false&serverTimezone=UTC
DB_USER=root
DB_PASS=tu_password_mysql
JWT_SECRET=claveSuperSecreta12345678901234567890
APP_DEFAULT_PASSWORD=123456
```

### 3️⃣ Compilación y Ejecución

```bash
# Compilar proyecto
mvn clean compile

# Ejecutar en desarrollo
mvn spring-boot:run

# O generar JAR y ejecutar
mvn clean package
java -jar target/miapi-0.0.1-SNAPSHOT.jar
```

### 4️⃣ Credenciales por Defecto

```
🔑 SuperAdmin por defecto:
   Email: superadmin@sistema.com
   Password: secret

⚠️ IMPORTANTE: Cambiar contraseña después del primer login
```

### 5️⃣ Acceder a la Documentación

```
📚 Swagger UI: http://localhost:8080/swagger-ui/index.html
📄 API Docs (JSON): http://localhost:8080/api-docs
```

📄 API Docs: http://localhost:8080/api-docs

```

---

## 📖 API Documentation

> � **Documentación Interactiva**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### 🎯 **Swagger UI - Documentación Completa**

La API cuenta con **documentación automática y interactiva** generada con Swagger/OpenAPI 3.0:

- **� Explorar Endpoints**: Todos los endpoints organizados por categorías
- **🧪 Testing en Vivo**: Probar la API directamente desde el navegador
- **📝 Esquemas de Datos**: Documentación completa de DTOs y modelos
- **📊 Ejemplos**: Request/response examples para cada endpoint
- **🔒 Autenticación**: Sistema de autorización JWT integrado

### �🔓 **Cómo Usar la API**

1. **Hacer Login**: `POST /api/auth/login` con credenciales por defecto
2. **Copiar Token**: Del response JSON en Swagger UI
3. **Autorizar**: Clic en "🔒 Authorize" → `Bearer TU_TOKEN`
4. **Probar Endpoints**: Directamente desde la interfaz interactiva

### 📚 **Categorías de Endpoints**

| Categoría | Descripción | Endpoints Principales |
|-----------|-------------|----------------------|
| 🔐 **Autenticación** | Login y gestión de contraseñas | `/api/auth/*` |
| 👑 **SuperAdmin** | Gestión completa del sistema | `/api/superadmin/*` |
| ⚙️ **Administradores** | Gestión de usuarios y tickets | `/api/admin/*` |
| 🎫 **Tickets** | CRUD completo de tickets | `/api/tickets/*` |
| 🔧 **Técnicos** | Gestión de tickets asignados | `/api/tecnico/*` |
| 🏢 **Trabajadores** | Creación y seguimiento | `/api/trabajador/*` |
| 👥 **Usuarios** | Gestión de perfil personal | `/api/usuarios/*` |
| 🔔 **Notificaciones** | Sistema de notificaciones | `/api/notificaciones/*` |
| 📊 **Estadísticas** | Métricas del sistema | `/api/estadisticas/*` |
| 📋 **Auditoría** | Logs y seguimiento | `/api/auditoria/*` |

### 🔐 **Autenticación Rápida**

| Método | Endpoint                       | Descripción          | Rol Requerido |
| ------ | ------------------------------ | -------------------- | ------------- |
| `POST` | `/api/auth/login`              | Iniciar sesión       | Público       |
| `POST` | `/api/auth/cambiar-password`   | Cambiar contraseña   | Autenticado   |
| `POST` | `/api/auth/reiniciar-password` | Reiniciar contraseña | Admin+        |

### 👑 **SuperAdmin Endpoints**

| Método   | Endpoint                                       | Descripción                     |
| -------- | ---------------------------------------------- | ------------------------------- |
| `GET`    | `/api/superadmin/usuarios`                     | Listar todos los usuarios       |
| `POST`   | `/api/superadmin/usuarios`                     | Crear usuario de cualquier tipo |
| `PUT`    | `/api/superadmin/usuarios/{id}/promover-admin` | Promover usuario a Admin        |
| `PUT`    | `/api/superadmin/usuarios/{id}/degradar-admin` | Degradar Admin a Trabajador     |
| `GET`    | `/api/superadmin/estadisticas/sistema`         | Estadísticas completas          |
| `PUT`    | `/api/superadmin/tickets/{id}/reabrir`         | Reabrir ticket cerrado          |
| `DELETE` | `/api/superadmin/tickets/{id}`                 | Eliminar ticket                 |

### 👨‍💼 **Admin Endpoints**

| Método | Endpoint                            | Descripción              |
| ------ | ----------------------------------- | ------------------------ |
| `GET`  | `/api/admin/usuarios`               | Listar usuarios no-admin |
| `POST` | `/api/admin/usuarios`               | Crear Técnico/Trabajador |
| `PUT`  | `/api/admin/usuarios/{id}/bloquear` | Bloquear usuario         |
| `GET`  | `/api/admin/tickets`                | Gestionar tickets        |

### 🎫 **Tickets**

| Método | Endpoint                      | Descripción        | Rol Requerido |
| ------ | ----------------------------- | ------------------ | ------------- |
| `GET`  | `/api/tickets`                | Listar mis tickets | Trabajador+   |
| `POST` | `/api/tickets`                | Crear ticket       | Trabajador+   |
| `PUT`  | `/api/tickets/{id}/resolver`  | Resolver ticket    | Técnico       |
| `PUT`  | `/api/tickets/{id}/finalizar` | Finalizar ticket   | Trabajador    |

### 🔔 **Notificaciones**

| Método | Endpoint                                | Descripción        |
| ------ | --------------------------------------- | ------------------ |
| `GET`  | `/api/notificaciones?userId={id}`       | Ver notificaciones |
| `PUT`  | `/api/notificaciones/{id}/marcar-leida` | Marcar como leída  |

### 📊 **Estadísticas**

| Método | Endpoint                     | Descripción          | Rol Requerido |
| ------ | ---------------------------- | -------------------- | ------------- |
| `GET`  | `/api/estadisticas/tickets`  | Stats de tickets     | Admin+        |
| `GET`  | `/api/estadisticas/tecnicos` | Performance técnicos | Admin+        |

---

## 🏗️ Arquitectura

### 📁 Estructura del Proyecto

```

src/main/java/com/poo/miapi/
├── 🎮 controller/ # Controladores REST
│ ├── auth/ # Autenticación
│ ├── core/ # Usuarios, Tickets
│ ├── estadistica/ # Reportes
│ ├── historial/ # Auditoría
│ └── notificacion/ # Notificaciones
├── 📊 dto/ # Data Transfer Objects
├── 🗃️ model/ # Entidades JPA
├── 🔧 repository/ # Acceso a datos
├── 🛠️ service/ # Lógica de negocio
├── 🔐 security/ # Configuración JWT
└── ⚙️ config/ # Configuraciones

````

### 🗄️ Modelo de Datos

```mermaid
erDiagram
    USUARIO ||--o{ TICKET : crea
    USUARIO ||--o{ NOTIFICACION : recibe
    TECNICO ||--o{ TECNICO_POR_TICKET : asignado
    TICKET ||--o{ TECNICO_POR_TICKET : historial

    USUARIO {
        int id
        string nombre
        string apellido
        string email
        string password
        string rol
        boolean activo
        boolean bloqueado
    }

    TICKET {
        int id
        string titulo
        text descripcion
        enum estado
        datetime fecha_creacion
    }
````

### 🔄 Flujo de Estados de Ticket

```mermaid
stateDiagram-v2
    [*] --> NO_ATENDIDO : Crear Ticket
    NO_ATENDIDO --> ATENDIDO : Asignar Técnico
    ATENDIDO --> RESUELTO : Técnico Resuelve
    RESUELTO --> FINALIZADO : Trabajador Confirma
    FINALIZADO --> REABIERTO : SuperAdmin/Admin
    REABIERTO --> ATENDIDO : Reasignar
```

---

## 🧪 Testing

### 🔧 Pruebas con Postman

1. **Importar colección** (próximamente)
2. **Configurar ambiente:**
   ```json
   {
     "baseUrl": "http://localhost:8080",
     "token": "{{jwt_token}}"
   }
   ```

### 📝 Ejemplo de Requests

**Login:**

```json
POST /api/auth/login
{
  "email": "superadmin@sistema.com",
  "password": "secret"
}
```

**Crear Ticket:**

```json
POST /api/tickets
Authorization: Bearer {{token}}
{
  "titulo": "Error en sistema",
  "descripcion": "Descripción detallada del problema"
}
```

---

## 🐳 Despliegue

### Docker Compose

```yaml
version: "3.8"
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: apiticket
      MYSQL_ROOT_PASSWORD: root123
    ports:
      - "3306:3306"

  api:
    build: .
    depends_on:
      - mysql
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:mysql://mysql:3306/apiticket
```

### 📈 Escalabilidad

- **Containerización**: Docker + Docker Compose
- **Orquestación**: Kubernetes
- **Load Balancing**: Nginx/HAProxy
- **Caching**: Redis para sesiones
- **Monitoring**: Prometheus + Grafana

### 📋 Versionado

Este proyecto sigue [Semantic Versioning](https://semver.org/):

- **0.x.x**: Versiones de desarrollo
- **1.x.x**: Primera versión estable
- **x.y.z**: Patch releases

**Versión Actual**: `0.2.0-SNAPSHOT`

**Changelog**: Ver [CHANGELOG.md](./CHANGELOG.md) para detalles de todas las versiones.

<div align="center">

**⭐ Si este proyecto te resulta útil, ¡dale una estrella! ⭐**

</div>
