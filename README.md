# 🎫 ApiTickets - Sistema de Gestión de Tickets

<div align="center">

![Java](https://img.shields.io/ba## 📚 Documentación

| Documento                                       | Descripción                                 |
| ----------------------------------------------- | ------------------------------------------- | ---------------- |
| 📖 **[TECHNICAL_DOCS.md](./TECHNICAL_DOCS.md)** | Documentación completa para desarrolladores |
| 📈 **[CHANGELOG.md](./CHANGELOG.md)**           | Historial de cambios y versiones            | -24+-orange.svg) |

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)
![JWT](https://img.shields.io/badge/JWT-Auth-red.svg)
![Maven](https://img.shields.io/badge/Maven-3.9+-purple.svg)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-brightgreen.svg)
![Version](https://img.shields.io/badge/Version-1.0.0-brightgreen.svg)

**Sistema completo de gestión de tickets para soporte técnico con arquitectura moderna y escalable**

[🚀 Instalación Rápida](#-instalación-rápida) • [📚 Documentación Técnica](./TECHNICAL_DOCS.md) • [ Changelog](./CHANGELOG.md)

</div>

---

## 📋 Descripción

**ApiTickets** es un sistema robusto de gestión de tickets diseñado para organizaciones que necesitan un control eficiente de sus solicitudes de soporte técnico. Construido con **Spring Boot 3.5.3** y **MySQL 8.0**, ofrece:

- ✅ **Sistema de roles jerárquico** (SuperAdmin, Admin, Técnico, Trabajador)
- ✅ **Autenticación JWT** segura
- ✅ **API REST completa** con documentación Swagger
- ✅ **Auditoría completa** de todas las acciones
- ✅ **Notificaciones automáticas**
- ✅ **Estadísticas en tiempo real**

---

## 🚀 Instalación Rápida

### **Prerrequisitos**

- ☕ Java 24+
- 🗄️ MySQL 8.0+
- 📦 Maven 3.9+

### **1. Clonar el Repositorio**

```bash
git clone https://github.com/Akc9912/apiTickets.git
cd apiTickets
```

### **2. Configurar Base de Datos**

```bash
# 1. Configurar archivo .env (copiar desde ejemplo)
cp .env.example .env
# Editar .env con tus credenciales de MySQL

# 2. Ejecutar el script único que hace todo
mysql -u root -p < create_database.sql

# 3. Verificar configuración (opcional)
chmod +x verify_db_config.sh
./verify_db_config.sh
# En Windows: verify_db_config.bat
```

> 📋 **Nota**: El script `create_database.sql` incluye:
>
> - ✅ Creación de la base de datos `apiticket`
> - ✅ Estructura completa de tablas
> - ✅ Configuración de enums `Rol` y `EstadoTicket`
> - ✅ Datos iniciales (SuperAdmin)
> - ✅ Índices optimizados

> 🔒 **Seguridad**: Todas las configuraciones de DB se cargan desde `.env` (nunca hardcodeadas)

### **3. Iniciar la Aplicación**

```bash
# Opción 1: Con Maven
./mvnw spring-boot:run

# Opción 2: JAR compilado
./mvnw clean package
java -jar target/miapi-*.jar
```

### **4. Verificar Instalación**

- 🌐 **API**: http://localhost:8080
- 📖 **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- 🔐 **Login**: `superadmin@sistema.com` / `secret`

---

## 👥 Roles del Sistema

| Rol               | Descripción       | Permisos Principales                               |
| ----------------- | ----------------- | -------------------------------------------------- |
| 🔴 **SuperAdmin** | Dueño del sistema | Control total, crear admins, estadísticas globales |
| 🟠 **Admin**      | Administrador     | Gestionar usuarios, tickets, reportes limitados    |
| 🔵 **Técnico**    | Soporte técnico   | Resolver tickets, gestionar asignaciones           |
| 🟢 **Trabajador** | Usuario final     | Crear tickets, evaluar soluciones                  |

---

## 🔒 Estados de Usuario

| Estado           | Descripción              | Puede Iniciar Sesión         | Puede Realizar Acciones |
| ---------------- | ------------------------ | ---------------------------- | ----------------------- |
| ✅ **Activo**    | Usuario normal           | ✅ Sí                        | ✅ Sí                   |
| ❌ **Inactivo**  | Baja lógica del usuario  | ❌ No (como si no existiera) | ❌ No                   |
| 🚫 **Bloqueado** | Suspendido temporalmente | ✅ Sí                        | ❌ No                   |

### 🔐 Medidas de Seguridad Implementadas

1. **Usuarios Inactivos**: Se tratan como si no existieran en el sistema

   - Mismo mensaje de error que usuarios inexistentes
   - Previene enumeración de cuentas registradas

2. **Usuarios Bloqueados**: Pueden autenticarse pero no realizar acciones

   - Útil para suspensiones temporales
   - Permite al usuario ver su estado en el sistema

3. **Protección contra Timing Attacks**:
   - Verificación de contraseña incluso para usuarios inexistentes
   - Tiempo de respuesta consistente

> **Nota de Seguridad**: Los usuarios inactivos se tratan como si no existieran en el sistema para proteger información sobre cuentas registradas.

---

## 🛠️ Tecnologías

- **Backend**: Spring Boot 3.5.3, Spring Security 6.5.1, Spring Data JPA
- **Base de Datos**: MySQL 8.0 con HikariCP
- **Autenticación**: JWT con refresh tokens
- **Documentación**: Swagger/OpenAPI 3.0
- **Build**: Maven 3.9.10
- **Java**: 24+ con Records y Pattern Matching

---

## � Documentación

| Documento                                       | Descripción                                 |
| ----------------------------------------------- | ------------------------------------------- |
| 📖 **[TECHNICAL_DOCS.md](./TECHNICAL_DOCS.md)** | Documentación completa para desarrolladores |
| 🗄️ **[DATABASE_SETUP.md](./DATABASE_SETUP.md)** | Guía de configuración de base de datos      |
| � **[JWT_SECURITY.md](./JWT_SECURITY.md)**      | Configuración de seguridad JWT              |
| �📈 **[CHANGELOG.md](./CHANGELOG.md)**          | Historial de cambios y versiones            |

---

## 🎯 Endpoints Principales

### **Autenticación**

```bash
POST /api/auth/login                 # Iniciar sesión
POST /api/auth/cambiar-password      # Cambiar contraseña
```

### **Gestión de Tickets**

```bash
GET  /api/tickets                    # Listar tickets
POST /api/tickets                    # Crear ticket
PUT  /api/tickets/{id}/estado        # Cambiar estado
```

### **Administración**

```bash
GET  /api/superadmin/usuarios        # Gestión de usuarios
GET  /api/superadmin/estadisticas    # Estadísticas del sistema
```

**📖 Ver documentación completa:** [TECHNICAL_DOCS.md](./TECHNICAL_DOCS.md)

---

## 🔧 Configuración

### **Variables de Entorno (.env)**

El proyecto utiliza un archivo `.env` para la configuración sensible. Copia `.env.example` como `.env`:

```bash
# Copiar plantilla
cp .env.example .env
```

Variables principales:

```bash
# Base de datos
DB_URL=jdbc:mysql://localhost:3306/apiticket?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASS=tu_password

# JWT Security (¡IMPORTANTE: cambiar en producción!)
JWT_SECRET=tu_clave_secreta_muy_larga_minimo_256_bits
JWT_EXPIRATION_MS=36000000

# Aplicación
SERVER_PORT=8080
```

### **🔑 Sistema de Contraseñas**

**Contraseña por defecto**: `[Apellido]123`

- Ejemplo: Usuario "Juan Pérez" → contraseña: `Perez123`
- **Cambio obligatorio**: Los usuarios deben cambiar la contraseña en su primer inicio de sesión
- **Campo `cambiarPass`**: Indica si el usuario debe actualizar su contraseña

### **Propiedades de la Aplicación**

```properties
# application.properties - Configuración con variables de entorno
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
server.port=${SERVER_PORT:8080}
jwt.secret=${JWT_SECRET:defaultSecretKey}
jwt.expiration-ms=${JWT_EXPIRATION_MS:36000000}
# La contraseña se genera automáticamente: apellido + "123"
```

> ⚠️ **Seguridad**: El archivo `.env` está en `.gitignore` y no se sube al repositorio.

---

## 🧪 Testing

### **Datos de Prueba**

- 🔐 **SuperAdmin**: `superadmin@sistema.com` / `secret`

### **Crear Usuarios de Prueba**

```bash
# Usar Swagger UI o endpoints REST para crear:
# - Administradores
# - Técnicos
# - Trabajadores
```

### **Swagger UI**

```
http://localhost:8080/swagger-ui/index.html
```

---

## 📊 Características del Sistema

### **✅ Gestión de Tickets**

- Estados: No atendido → Atendido → Finalizado → Resuelto/Reabierto
- Asignación automática y manual de técnicos
- Historial completo de cambios
- Validación final por parte del trabajador (aceptar/rechazar solución)

### **✅ Sistema de Usuarios**

- Roles jerárquicos con permisos específicos
- Autenticación JWT segura
- Gestión de contraseñas con políticas
- Activación/desactivación de cuentas

### **✅ Auditoría y Reportes**

- Log completo de todas las acciones
- Estadísticas en tiempo real
- Reportes por usuario, técnico, período
- Métricas de rendimiento

### **✅ Notificaciones**

- Sistema automático de notificaciones
- Alertas por cambios de estado
- Notificaciones de asignación
- Recordatorios automáticos

---

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

## 👨‍� Autor

**Sebastian Conde** - [@Akc9912](https://github.com/Akc9912)

---

<div align="center">

**¿Necesitas ayuda?**

📖 [Documentación Técnica](./TECHNICAL_DOCS.md) • � [Changelog](./CHANGELOG.md) • 🐛 [Issues](https://github.com/Akc9912/apiTickets/issues)

</div>

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
| `GET`  | `/api/tickets`                      | Listar mis tickets | Trabajador+   |
| `POST` | `/api/tickets`                      | Crear ticket       | Trabajador+   |
| `PUT`  | `/api/tickets/{id}/resolver`        | Resolver ticket    | Técnico       |
| `POST` | `/api/trabajador/tickets/{id}/evaluar` | Evaluar solución   | Trabajador    |

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
        string rol
        boolean activo
        boolean bloqueado
    }
        string titulo
        text descripcion
````

````mermaid

---
### 🔧 Pruebas con Postman

2. **Configurar ambiente:**
   ```json
````

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
