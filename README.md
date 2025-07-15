# apiTickets

Sistema de gestión de tickets para soporte técnico, desarrollado en Java con Spring Boot y MySQL.

## Características

- Autenticación y autorización con JWT
- Gestión de usuarios (admin, técnico, trabajador)
- Creación y seguimiento de tickets
- Asignación de técnicos a tickets
- Notificaciones internas
- Estadísticas y reportes
- Cambios y reinicio de contraseñas
- Roles y permisos configurables

## Requisitos

- Java 17+
- Maven 3.8+
- MySQL 8+
- Docker (opcional para despliegue)
- Postman (para pruebas)

## Instalación

1. Clona el repositorio:

   ```sh
   git clone https://github.com/tuusuario/apiTickets.git
   cd apiTickets
   ```

2. Configura la base de datos MySQL y crea el esquema:

   - Usa el script `init_ticket_system.sql` para crear las tablas y datos de prueba.

3. Configura las variables de entorno en `.env`:

   ```
   DB_URL=jdbc:mysql://localhost:3306/api_tickets?useSSL=false&serverTimezone=UTC
   DB_USER=root
   DB_PASS=tu_password
   JWT_SECRET=claveSuperSecreta12345678901234567890
   ```

4. Compila y ejecuta el proyecto:
   ```sh
   mvn clean package
   java -jar target/apiTickets.jar
   ```

## Uso

### Endpoints principales

- **Autenticación**

  - `POST /api/auth/login` — Login y obtención de JWT
  - `POST /api/auth/cambiar-password` — Cambiar contraseña
  - `POST /api/auth/reiniciar-password` — Reiniciar contraseña

- **Usuarios**

  - `GET /api/admin/usuarios` — Listar usuarios (admin)
  - `POST /api/admin/usuarios` — Crear usuario (admin)
  - `GET /api/usuarios/obtener-datos?userId={id}` — Ver datos de usuario

- **Tickets**

  - `GET /api/tickets` — Listar tickets
  - `POST /api/tickets` — Crear ticket
  - `PUT /api/tickets/{id}` — Actualizar ticket

- **Notificaciones**
  - `GET /api/notificaciones?userId={id}` — Ver notificaciones
  - `POST /api/notificaciones` — Enviar notificación (admin)

### Autenticación JWT

1. Haz login en `/api/auth/login` y copia el token recibido.
2. En cada endpoint protegido, agrega el header:
   ```
   Authorization: Bearer TU_TOKEN_JWT
   ```

## Pruebas

- Usa Postman para probar los endpoints.
- Incluye el token JWT en las peticiones protegidas.

## Escalabilidad

- Contenerización con Docker
- Orquestación con Kubernetes (opcional)
- Balanceo de carga con Nginx/HAProxy
- Cache con Redis
- Monitorización con Prometheus/Grafana
