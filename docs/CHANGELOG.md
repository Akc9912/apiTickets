# 📝 Changelog

Todos los cambios importantes en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Sin Publicar]

### 🔄 En Desarrollo
- Sistema de notificaciones en tiempo real con WebSockets
- Dashboard avanzado con métricas ejecutivas
- Integración con herramientas de monitoreo externas
- Testing automatizado completo (Unit + Integration)

---

## [0.2.0] - 2025-10-01

### ✨ Nuevas Funcionalidades
- **Sistema de Auditoría Completo**: Nueva tabla de auditoría con campos avanzados (JSON, IP, user-agent, contexto)
- **Sistema de Notificaciones Mejorado**: Tipos, categorías, prioridades y estados de notificación
- **Analytics Avanzado**: Estadísticas por período (diario, semanal, mensual, trimestral, anual)
- **Métricas de Rendimiento**: Estadísticas detalladas por técnico
- **Sistema de Devoluciones**: Nueva funcionalidad para solicitar devolución de tickets
- **Script de Migración Consolidado**: Un solo script para todas las actualizaciones de base de datos

### 🛠️ Mejoras
- **Performance**: Índices optimizados en todas las nuevas tablas
- **Seguridad**: Mejores validaciones y constraints en base de datos
- **Documentación**: README completamente renovado con arquitectura detallada
- **Configuración**: Variables de entorno mejoradas para diferentes ambientes

### 🔧 Cambios Técnicos
- Migración de tabla `auditoria` simple a sistema completo con JSON y metadatos
- Reemplazo de `resumen_ticket_mensual` por sistema multi-período `estadistica_periodo`
- Nueva tabla `estadistica_tecnico` para métricas de rendimiento individual
- Procedimientos almacenados para cálculo automático de estadísticas históricas

### 📚 Documentación
- **README.md**: Completamente reescrito con arquitectura, roadmap y guías
- **Estructura docs/**: Nueva organización de documentación por categorías
- **API Documentation**: Preparación para documentación completa de endpoints
- **Architecture Docs**: Diagramas y explicaciones de la arquitectura del sistema

### 🗃️ Base de Datos
- **Backups Automáticos**: Sistema de backup antes de migraciones
- **Rollback Scripts**: Scripts de recuperación en caso de fallos
- **Verificaciones**: Checks de integridad pre y post migración
- **Estadísticas Históricas**: Generación automática de métricas históricas

---

## [0.1.0] - 2025-09-15

### ✨ Funcionalidades Iniciales
- **Autenticación JWT**: Sistema completo de autenticación y autorización
- **CRUD Completo**: Operaciones para todas las entidades principales
- **API REST**: Endpoints RESTful para todas las funcionalidades
- **Roles y Permisos**: Sistema de roles (SUPER_ADMIN, ADMIN, TECNICO, TRABAJADOR)
- **Swagger Integration**: Documentación automática de API con OpenAPI 3.0

### 🏗️ Arquitectura Base
- **Spring Boot 3.5.3**: Framework principal con las últimas características
- **Java 24**: Aprovechando las nuevas funcionalidades del lenguaje
- **MySQL 8.0**: Base de datos relacional con características avanzadas
- **JPA/Hibernate**: ORM para manejo de datos
- **Maven**: Gestión de dependencias y construcción

### 🗄️ Entidades Principales
- **Usuario**: Gestión de usuarios del sistema
- **Ticket**: Sistema de tickets/incidencias
- **Técnico**: Gestión de técnicos y asignaciones
- **Trabajador**: Usuarios que crean tickets
- **Admin**: Administradores del sistema
- **Auditoría**: Registro básico de acciones del sistema

### 🔐 Seguridad
- **JWT Tokens**: Tokens seguros para autenticación
- **BCrypt**: Hashing seguro de contraseñas
- **CORS**: Configuración para aplicaciones web
- **SQL Injection Protection**: Prevención automática con JPA
- **Validación de Entrada**: Bean Validation en todos los endpoints

### 📊 Monitoreo Básico
- **Spring Actuator**: Endpoints de health check y métricas
- **Logging**: Sistema de logs configurado
- **Health Checks**: Verificación de estado de la aplicación

### 🐳 Containerización
- **Dockerfile**: Imagen Docker optimizada
- **Docker Compose**: Orquestación con MySQL
- **Variables de Entorno**: Configuración externalizada

---

## 📋 Tipos de Cambios

- **✨ Added** - Para nuevas funcionalidades
- **🛠️ Changed** - Para cambios en funcionalidades existentes
- **⚠️ Deprecated** - Para funcionalidades que serán removidas pronto
- **🗑️ Removed** - Para funcionalidades removidas
- **🐛 Fixed** - Para corrección de bugs
- **🔒 Security** - Para mejoras de seguridad

---

## 🎯 Roadmap Futuro

### 📅 Versión 0.3.0 - Notificaciones en Tiempo Real
- WebSockets para notificaciones push
- Sistema de suscripciones por tipo de evento
- Dashboard en tiempo real
- Integración con servicios de mensajería externos

### 📅 Versión 0.4.0 - Analytics Avanzado
- Dashboards ejecutivos interactivos
- Reportes personalizables
- Exportación de datos (Excel, PDF, CSV)
- KPIs automatizados y alertas

### 📅 Versión 0.5.0 - Microservicios
- Separación en microservicios
- Event-driven architecture
- API Gateway
- Service discovery

### 📅 Versión 1.0.0 - Producción Enterprise
- Escalabilidad completa
- Multi-tenancy
- Compliance (SOX, GDPR)
- SLA monitoring
- Advanced security features

---

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor, sigue el formato de [Conventional Commits](https://www.conventionalcommits.org/) para los mensajes de commit:

```
tipo(alcance): descripción breve

Descripción más detallada del cambio...

BREAKING CHANGE: describe cualquier cambio que rompa compatibilidad
```

### Tipos de Commit
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Documentación
- `style`: Formateo, punto y coma faltante, etc.
- `refactor`: Cambio de código que no corrige bug ni agrega funcionalidad
- `test`: Agregando tests
- `chore`: Cambios en build, tareas auxiliares, etc.

---

## 📞 Soporte

Para reportar bugs o solicitar funcionalidades:
- **Issues**: [GitHub Issues](https://github.com/Akc9912/apiTickets/issues)
- **Discussions**: [GitHub Discussions](https://github.com/Akc9912/apiTickets/discussions)
- **Email**: akc9912@gmail.com
