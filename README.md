# 🎫 ApiTickets - Sistema de Gestión de Tickets

<div align="center">

![Java](https://img.shields.io/badge/Java-24+-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-green.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)
![JWT](https://img.shields.io/badge/JWT-Auth-red.svg)
![Maven](https://img.shields.io/badge/Maven-3.9+-purple.svg)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203.0-yellow.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)

**Sistema empresarial de gestión de tickets con arquitectura moderna, seguridad avanzada y escalabilidad horizontal**

[🚀 Demo](#demo) • [📖 Documentación](./docs/) • [🔧 Instalación](#instalación-y-configuración) • [🌟 Features](#características-principales)

</div>

---

## � Tabla de Contenidos

- [🎯 Descripción](#descripción)
- [🌟 Características Principales](#características-principales)
- [🏗️ Arquitectura](#arquitectura)
- [🚀 Instalación y Configuración](#instalación-y-configuración)
- [📖 Documentación](#documentación)
- [🔐 Seguridad](#seguridad)
- [📊 Monitoreo](#monitoreo)
- [🚢 Despliegue](#despliegue)
- [🤝 Contribución](#contribución)
- [📄 Licencia](#licencia)

---

## 🎯 Descripción

**ApiTickets** es un sistema empresarial de gestión de tickets desarrollado con Spring Boot 3.5.3 y Java 24. Diseñado para organizaciones que requieren un control detallado de incidencias, soporte técnico y gestión de activos, con énfasis en seguridad, escalabilidad y observabilidad.

### 🎪 **Demo en Vivo**
- **API Swagger**: `http://localhost:8080/swagger-ui/index.html`
- **Actuator Health**: `http://localhost:8080/actuator/health`
- **Metrics**: `http://localhost:8080/actuator/metrics`

---

## 🌟 Características Principales

### 🔐 **Seguridad Avanzada**
- **JWT Authentication** con refresh tokens
- **Autorización basada en roles** (SUPER_ADMIN, ADMIN, TECNICO, TRABAJADOR)
- **Protección CSRF** y **CORS** configurables
- **Auditoría completa** de todas las acciones
- **Validación de entrada** con Bean Validation
- **Rate Limiting** y **SQL Injection** prevention

### 📊 **Analytics y Reportes**
- **Dashboard ejecutivo** con métricas en tiempo real
- **Estadísticas detalladas** por técnico y período
- **KPIs automáticos**: SLA, tiempo de resolución, satisfacción
- **Exportación de reportes** en múltiples formatos
- **Alertas inteligentes** basadas en umbrales

### 🚀 **Performance y Escalabilidad**
- **Connection Pooling** optimizado (HikariCP)
- **Caché inteligente** con invalidación automática
- **Paginación eficiente** en todas las consultas
- **Índices optimizados** para consultas complejas
- **Horizontal scaling** ready con Docker/K8s

### 🔄 **Integración y APIs**
- **RESTful API** completamente documentada
- **OpenAPI 3.0** con Swagger UI interactivo
- **WebSockets** para notificaciones en tiempo real
- **Actuator endpoints** para monitoreo
- **Health checks** personalizados

---

## 🏗️ Arquitectura

### **📐 Patrón de Arquitectura: Clean Architecture + DDD**

```
┌─────────────────────────────────────────────────────┐
│                 PRESENTATION LAYER                  │
├─────────────────────────────────────────────────────┤
│  Controllers │ DTOs │ Exception Handlers │ Security │
├─────────────────────────────────────────────────────┤
│                   SERVICE LAYER                     │
├─────────────────────────────────────────────────────┤
│ Business Logic │ Validations │ Analytics │ Events   │
├─────────────────────────────────────────────────────┤
│                 REPOSITORY LAYER                    │
├─────────────────────────────────────────────────────┤
│   JPA Repos │ Custom Queries │ Transactions │ Cache │
├─────────────────────────────────────────────────────┤
│                   DOMAIN LAYER                      │
├─────────────────────────────────────────────────────┤
│    Entities │ Value Objects │ Enums │ Domain Logic  │
└─────────────────────────────────────────────────────┘
```

### **🗄️ Entidades Principales**

| Entidad | Descripción | Relaciones |
|---------|-------------|------------|
| **User** | Usuario base del sistema | → Tecnico, Trabajador, Admin |
| **Ticket** | Ticket/Incidencia principal | → User, Tecnico, History |
| **Notification** | Sistema de notificaciones | → User, Ticket |
| **Audit** | Auditoría de todas las acciones | → User, Entities |
| **Statistics** | Métricas y KPIs del sistema | → Tickets, Users |

---

## 🚀 Instalación y Configuración

### **📋 Prerrequisitos**

- **Java 24+** ([OpenJDK](https://jdk.java.net/24/) recomendado)
- **Maven 3.9+**
- **MySQL 8.0+** o **PostgreSQL 13+**
- **Docker** (opcional, para containerización)
- **Redis** (opcional, para caché distribuido)

### **⚡ Instalación Rápida**

```bash
# 1. Clonar el repositorio
git clone https://github.com/Akc9912/apiTickets.git
cd apiTickets

# 2. Configurar base de datos
mysql -u root -p < create_database.sql
mysql -u root -p apiticket < migracion_completa.sql

# 3. Configurar variables de entorno
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Editar application.properties con tus configuraciones

# 4. Compilar y ejecutar
./mvnw clean install
./mvnw spring-boot:run
```

### **🐳 Instalación con Docker**

```bash
# Opción 1: Docker Compose (Recomendado)
docker-compose up -d

# Opción 2: Docker manual
docker build -t apitickets .
docker run -p 8080:8080 -e DB_URL=jdbc:mysql://host:3306/apiticket apitickets
```

### **⚙️ Configuración**

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/apiticket
spring.datasource.username=root
spring.datasource.password=tu_password

# JWT
jwt.secret=tu_jwt_secret_muy_seguro_de_al_menos_256_bits
jwt.expiration=86400000

# Logging
logging.level.com.poo.miapi=DEBUG
logging.pattern.file=%d{ISO8601} [%thread] %-5level %logger{36} - %msg%n

# Actuator
management.endpoints.web.exposure.include=health,metrics,info,prometheus
management.endpoint.health.show-details=always
```

---

## 📖 Documentación

### **📚 Documentación Completa**

| Documento | Descripción | Enlace |
|-----------|-------------|--------|
| **API Reference** | Documentación completa de endpoints | [📄 API Docs](./docs/api/) |
| **Arquitectura** | Diagramas y diseño del sistema | [🏗️ Architecture](./docs/architecture/) |
| **Desarrollo** | Guías de desarrollo y contribución | [👨‍💻 Development](./docs/development/) |
| **Despliegue** | Guías de despliegue y DevOps | [🚢 Deployment](./docs/deployment/) |
| **Changelog** | Historial de versiones y cambios | [📝 Changelog](./docs/CHANGELOG.md) |

### **🔗 Links Rápidos**

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Metrics**: [http://localhost:8080/actuator/metrics](http://localhost:8080/actuator/metrics)

---

## 🔐 Seguridad

### **🛡️ Medidas de Seguridad Implementadas**

- ✅ **JWT Authentication** con algoritmo HS256
- ✅ **HTTPS** enforcing en producción
- ✅ **CORS** configuración restrictiva
- ✅ **SQL Injection** prevention con JPA/Hibernate
- ✅ **XSS Protection** con headers de seguridad
- ✅ **Rate Limiting** por IP y usuario
- ✅ **Audit Trail** completo de todas las acciones
- ✅ **Password Hashing** con BCrypt
- ✅ **Session Management** seguro

### **👥 Roles y Permisos**

| Rol | Permisos | Descripción |
|-----|----------|-------------|
| **SUPER_ADMIN** | 🔓 Total | Acceso completo al sistema |
| **ADMIN** | 🔐 Gestión | Gestión de usuarios y tickets |
| **TECNICO** | 🔧 Técnico | Resolución de tickets asignados |
| **TRABAJADOR** | 📝 Básico | Creación y seguimiento de tickets |

---

## 📊 Monitoreo

### **📈 Métricas Disponibles**

- **Application Metrics**: Rendimiento de la aplicación
- **Business Metrics**: KPIs del negocio (tickets, resolución, SLA)
- **Infrastructure Metrics**: CPU, memoria, DB connections
- **Security Metrics**: Intentos de login, accesos bloqueados

### **🚨 Health Checks**

```bash
# Health check general
curl http://localhost:8080/actuator/health

# Health check detallado
curl http://localhost:8080/actuator/health/db
curl http://localhost:8080/actuator/health/diskSpace
```

### **📊 Prometheus Integration**

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'apitickets'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
```

---

## 🚢 Despliegue

### **🌐 Ambientes Soportados**

- **Local Development**: `./mvnw spring-boot:run`
- **Docker**: `docker-compose up`
- **Kubernetes**: Helm charts incluidos
- **Cloud**: AWS ECS, Azure Container Instances, GCP Cloud Run

### **🔧 Variables de Entorno**

```bash
# Base de datos
DB_URL=jdbc:mysql://localhost:3306/apiticket
DB_USERNAME=root
DB_PASSWORD=password

# JWT
JWT_SECRET=your-256-bit-secret
JWT_EXPIRATION=86400000

# Logging
LOGGING_LEVEL=INFO
LOGGING_FILE=/app/logs/application.log
```

### **📊 Escalabilidad**

- **Horizontal Scaling**: Stateless design
- **Load Balancing**: Nginx/HAProxy ready
- **Database Scaling**: Read replicas support
- **Caching**: Redis distributed cache
- **CDN**: Static assets optimization

---

## 🎯 Roadmap y Fases de Desarrollo

### **🚀 Fase 1: Fundación (Completada)**
- ✅ Arquitectura base con Spring Boot
- ✅ Autenticación JWT
- ✅ CRUD completo de entidades
- ✅ API REST documentada
- ✅ Base de datos optimizada

### **🔧 Fase 2: Optimización (En Progreso)**
- 🔄 Sistema de notificaciones en tiempo real
- 🔄 Analytics avanzado y dashboards
- 🔄 Mejoras de performance
- 🔄 Testing automatizado completo

### **🌟 Fase 3: Escalabilidad (Planificada)**
- 📅 Microservicios architecture
- 📅 Event-driven architecture
- 📅 Kubernetes deployment
- 📅 Multi-tenancy support

### **🔒 Fase 4: Seguridad Avanzada (Planificada)**
- 📅 OAuth2/OpenID Connect
- 📅 MFA (Multi-Factor Authentication)
- 📅 Advanced audit system
- 📅 Compliance reports (SOX, GDPR)

---

## 🤝 Contribución

### **👨‍💻 Guía de Contribución**

1. **Fork** el repositorio
2. **Crear** rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. **Commit** cambios (`git commit -am 'Add: nueva funcionalidad'`)
4. **Push** a la rama (`git push origin feature/nueva-funcionalidad`)
5. **Crear** Pull Request

### **📋 Estándares de Código**

- **Java**: Google Java Style Guide
- **Commits**: Conventional Commits
- **Testing**: Mínimo 80% coverage
- **Documentation**: JavaDoc obligatorio

### **� Code Review Process**

- ✅ **Automated Tests** deben pasar
- ✅ **Security Scan** sin vulnerabilidades
- ✅ **Performance Tests** sin regresiones
- ✅ **Code Review** por al menos 2 reviewers

---

## 📞 Soporte y Contacto

### **🆘 Obtener Ayuda**

- **📖 Documentación**: [./docs/](./docs/)
- **🐛 Issues**: [GitHub Issues](https://github.com/Akc9912/apiTickets/issues)
- **💬 Discussions**: [GitHub Discussions](https://github.com/Akc9912/apiTickets/discussions)

### **📧 Contacto**

- **Autor**: Sebastian Kc
- **Email**: akc9912@gmail.com
- **GitHub**: [@Akc9912](https://github.com/Akc9912)

---

## 📄 Licencia

Este proyecto está licenciado bajo la **MIT License** - ver el archivo [LICENSE](LICENSE) para más detalles.

---

## 🙏 Agradecimientos

- **Spring Boot Team** por el excelente framework
- **MySQL Team** por la robusta base de datos
- **JWT.io** por la excelente documentación
- **Swagger** por las herramientas de documentación API

---

<div align="center">

**⭐ Si te gusta este proyecto, dale una estrella en GitHub! ⭐**

[🔝 Volver al inicio](#-apitickets---sistema-de-gestión-de-tickets)

</div>
- **Query Optimization**: Índices estratégicos y consultas optimizadas
- **Caching Strategy**: Cache L1/L2 para estadísticas frecuentes
- **Batch Processing**: Operaciones masivas optimizadas

---

## 🤝 Contribución y Desarrollo

### **🎯 Roadmap del Proyecto**

- ✅ **v1.0**: Sistema base de tickets y usuarios
- ✅ **v1.1**: Sistema de auditoría y notificaciones
- ✅ **v1.2**: Analytics avanzado y migración segura
- 🔄 **v1.3**: Dashboard web y exportación de reportes
- 📋 **v1.4**: API webhooks y integraciones externas
- 🔮 **v2.0**: Microservicios y escalabilidad cloud

### **🛠️ Cómo Contribuir**

1. **Fork** el repositorio
2. **Crear rama**: `git checkout -b feature/nueva-funcionalidad`
3. **Desarrollar** siguiendo las convenciones del proyecto
4. **Tests**: Asegurar cobertura de código
5. **Pull Request** con descripción detallada

### **📋 Guidelines de Desarrollo**

- **Código**: Java 24+, Spring Boot patterns, clean architecture
- **Base de Datos**: Migraciones versionadas, no raw SQL en código
- **API**: OpenAPI 3.0 documentation, RESTful principles
- **Testing**: Unit + Integration tests, minimum 80% coverage
- **Security**: OWASP compliance, input validation

---

## 📝 Licencia y Soporte

**Licencia**: MIT - Ver [LICENSE](./LICENSE) para detalles completos

**Soporte Técnico**:
- 📖 **Documentación**: [Wiki del proyecto](https://github.com/Akc9912/apiTickets/wiki)
- 🐛 **Issues**: [GitHub Issues](https://github.com/Akc9912/apiTickets/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/Akc9912/apiTickets/discussions)

---

## 👨‍💻 Créditosg)
![Version](https://img.shields.io/badge/Version-1.2.0-brightgreen.svg)
![Build](https://img.shields.io/badge/Build-Passing-success.svg)

**Sistema completo de gestión de tickets para soporte técnico con arquitectura moderna, analytics avanzados y migración segura**

[🚀 Instalación Rápida](#-instalación-rápida) • [📊 Sistema de Estadísticas](#-sistema-de-estadísticas-avanzado) • [🔄 Migración Segura](#-migración-segura-de-base-de-datos) • [📚 Documentación Técnica](./TECHNICAL_DOCS.md)

</div>

---

## 📋 Descripción

**ApiTickets** es un sistema robusto y escalable de gestión de tickets diseñado para organizaciones que requieren control avanzado de sus solicitudes de soporte técnico. Construido con **Spring Boot 3.5.3** y **MySQL 8.0**, incorpora las mejores prácticas de desarrollo empresarial:

### 🌟 **Características Principales**

- ✅ **Sistema de roles jerárquico** con permisos granulares
- ✅ **Autenticación JWT** con refresh tokens y seguridad avanzada
- ✅ **API REST completa** con documentación interactiva Swagger/OpenAPI
- ✅ **Sistema de auditoría completo** con trazabilidad total
- ✅ **Notificaciones inteligentes** categorizadas y priorizadas
- ✅ **Dashboard de estadísticas en tiempo real** con métricas avanzadas
- ✅ **Migración de datos segura** sin pérdida de información
- ✅ **Arquitectura escalable** preparada para crecimiento empresarial

### 🔥 **Nuevas Características v1.2.0**

- 📊 **Sistema de Estadísticas Avanzado**: Dashboard ejecutivo, métricas por técnico, análisis de tendencias
- 🎯 **Analytics de Usuarios**: Productividad, actividad, rankings de rendimiento
- 🔄 **Migración Segura**: Scripts automáticos para actualización sin pérdida de datos
- 📈 **Reportes Dinámicos**: Filtros por fechas, comparativas, exportación de datos
- 🏆 **Sistema de Solicitudes de Devolución**: Workflow completo para técnicos
- 💡 **Optimización de Rendimiento**: Consultas optimizadas e índices mejorados

---

## 🚀 Instalación Rápida

### **Prerrequisitos**

- ☕ **Java 24+** con soporte para Records y Pattern Matching
- 🗄️ **MySQL 8.0+** para funciones avanzadas de JSON y Window Functions
- 📦 **Maven 3.9+** para gestión de dependencias
- 💾 **4GB RAM mínimo** (8GB recomendado para desarrollo)

### **1. Clonar el Repositorio**

```bash
git clone https://github.com/Akc9912/apiTickets.git
cd apiTickets
```

### **2. Configuración Inicial**

#### **Configurar Variables de Entorno**
```bash
# Copiar plantilla de configuración
cp .env.example .env

# Editar .env con tus credenciales
# Variables críticas:
# - DB_USER, DB_PASS: Credenciales MySQL
# - JWT_SECRET: Clave de 256+ bits para JWT
# - ADMIN_EMAIL: Email del SuperAdmin inicial
```

#### **Inicializar Base de Datos**
```bash
# NUEVA INSTALACIÓN (Base de datos vacía)
mysql -u root -p < create_database.sql

# ACTUALIZACIÓN DESDE VERSIÓN ANTERIOR
# Ver sección "🔄 Migración Segura" más abajo
```

### **3. Compilar y Ejecutar**

```bash
# Verificar configuración y compilar
./mvnw clean compile

# Ejecutar en modo desarrollo
./mvnw spring-boot:run

# O compilar JAR para producción
./mvnw clean package
java -jar target/miapi-*.jar
```

### **4. Verificar Instalación**

**🎯 Acceso Rápido:**
- 🌐 **API Principal**: http://localhost:8080
- � **Dashboard Ejecutivo**: http://localhost:8080/swagger-ui/index.html#/estadistica-controller
- 📖 **Documentación Interactiva**: http://localhost:8080/swagger-ui/index.html
- 🔐 **Credenciales por defecto**: `superadmin@sistema.com` / `secret`

---

## � Sistema de Estadísticas Avanzado

### 🎯 **Dashboard Ejecutivo en Tiempo Real**

El sistema incluye un completo dashboard de métricas empresariales:

#### **📈 Estadísticas Generales**
- **Resumen Ejecutivo**: KPIs principales del sistema
- **Métricas de Tickets**: Creados, resueltos, pendientes por período
- **Análisis de Tendencias**: Comparativas mensuales y anuales
- **Tiempo Real**: Actividad actual del sistema

#### **👨‍💼 Analytics de Técnicos**
- **Ranking de Productividad**: Top performers por resoluciones
- **Métricas Individuales**: Eficiencia, tiempos promedio, calidad
- **Análisis de Carga**: Distribución de trabajo por técnico
- **Indicadores de Calidad**: Marcas, fallas, porcentaje de éxito

#### **👥 Analytics de Usuarios**
- **Actividad por Usuario**: Tickets creados, evaluaciones, sesiones
- **Usuarios Más Activos**: Rankings de participación
- **Métricas de Productividad**: Análisis de uso del sistema
- **Comportamiento por Rol**: Patrones de uso diferenciados

### 🔍 **Endpoints de Estadísticas Disponibles**

```bash
# Dashboard Ejecutivo
GET /api/estadisticas/resumen-ejecutivo
GET /api/estadisticas/incidentes
GET /api/estadisticas/tiempo-real

# Analytics de Técnicos
GET /api/estadisticas/tecnicos/ranking
GET /api/estadisticas/tecnicos/{id}/detalle

# Analytics de Usuarios
GET /api/estadisticas/usuarios/mi-actividad
GET /api/estadisticas/usuarios/ranking/mas-activos
GET /api/estadisticas/usuarios/productividad
```

---

## 🔄 Migración Segura de Base de Datos

### ⚠️ **Si tienes datos existentes y necesitas actualizar**

El sistema incluye scripts de migración automática que preservan todos los datos:

#### **📋 Proceso de Migración Paso a Paso**

```bash
# 1. Verificar estado actual
mysql -u root -p apiticket < pre_migration_check.sql

# 2. Backup manual (OBLIGATORIO)
mysqldump -u root -p --single-transaction apiticket > backup_$(date +%Y%m%d).sql

# 3. Ejecutar migración segura
mysql -u root -p apiticket < migration_safe.sql

# 4. Generar estadísticas históricas
mysql -u root -p apiticket < post_migration_stats.sql
```

#### **🛡️ Características de Seguridad**

- ✅ **Backups automáticos** con timestamp
- ✅ **Transaccional**: Rollback automático si algo falla
- ✅ **Preservación de datos**: Migra información al nuevo formato
- ✅ **Plan de recuperación**: Script de rollback incluido
- ✅ **Verificación de integridad** en cada paso

#### **� Scripts de Migración Incluidos**

| Script | Propósito |
|--------|-----------|
| `pre_migration_check.sql` | Verificación previa al proceso |
| `migration_safe.sql` | Migración principal transaccional |
| `rollback_migration.sql` | Recuperación en caso de problemas |
| `post_migration_stats.sql` | Generación de estadísticas históricas |
| `MIGRATION_GUIDE.md` | Guía completa del proceso |

---

## 👥 Sistema de Roles y Permisos

| Rol | Descripción | Permisos Clave | Estadísticas Accesibles |
|-----|-------------|----------------|------------------------|
| 🔴 **SuperAdmin** | Control total del sistema | Todos los permisos + gestión de admins | Dashboard completo, métricas globales |
| 🟠 **Admin** | Gestión operativa | Usuarios, tickets, reportes departamentales | Analytics de equipo, KPIs operativos |
| � **Técnico** | Resolución especializada | Tickets asignados, solicitudes devolución | Métricas personales, productividad |
| 🟢 **Trabajador** | Usuario final | Crear tickets, evaluar soluciones | Actividad personal, tickets propios |

---

## 🔒 Seguridad y Auditoría

### **Sistema de Auditoría Completo**
- 📝 **Trazabilidad total**: Cada acción queda registrada
- 👤 **Contexto completo**: Usuario, IP, timestamp, cambios realizados
- 🔍 **Valores anteriores/nuevos**: Registro en formato JSON
- 📊 **Reportes de auditoría**: Análisis de comportamiento y seguridad

### **Medidas de Seguridad Avanzadas**
- 🛡️ **JWT con refresh tokens**: Expiración automática y renovación
- 🔐 **Protección contra timing attacks**: Respuestas consistentes
- 🚫 **Soft delete**: Los usuarios inactivos no revelan información
- 📱 **Contexto de sesión**: IP, user agent, origen de la acción

---

## 🛠️ Stack Tecnológico

**Backend Robusto:**
- **Framework**: Spring Boot 3.5.3 con Spring Security 6.5.1
- **Persistencia**: Spring Data JPA + Hibernate 6.x
- **Base de Datos**: MySQL 8.0+ con optimizaciones para analytics
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

GET  /api/tickets                           # Lista con filtros avanzados
POST /api/tickets                           # Crear con validaciones
PUT  /api/tickets/{id}/asignar-tecnico      # Asignación inteligente
POST /api/tecnico/solicitar-devolucion      # Workflow de devolución
```

### **👑 Administración Empresarial**
```bash
GET  /api/superadmin/dashboard              # KPIs ejecutivos
GET  /api/admin/reportes/productividad      # Reportes departamentales
GET  /api/auditoria/acciones                # Trazabilidad completa
```

> 📖 **Documentación Interactiva Completa**: [Swagger UI](http://localhost:8080/swagger-ui/index.html)

---

## 🔧 Configuración Avanzada

### **⚙️ Variables de Entorno (.env)**

Sistema de configuración centralizada para máxima seguridad:

```bash
# Copiar plantilla y personalizar
cp .env.example .env
```

**Configuración de Producción:**
```bash
# Base de datos
DB_URL=jdbc:mysql://localhost:3306/apiticket?useSSL=true&requireSSL=true&serverTimezone=UTC
DB_USER=apiticket_user
DB_PASS=your_secure_password_here

# JWT Security (CRÍTICO: Usar claves de 256+ bits)
JWT_SECRET=your_super_secure_secret_key_minimum_256_bits_for_production_security
JWT_EXPIRATION_MS=3600000  # 1 hora en producción

# Sistema
SERVER_PORT=8080
ENVIRONMENT=production
LOG_LEVEL=INFO

# Analytics y Estadísticas
STATS_CALCULATION_SCHEDULE=0 0 2 * * *  # 2 AM diario
STATS_RETENTION_DAYS=365
```

### **🔑 Sistema de Contraseñas Inteligente**

**Generación Automática Segura:**
- **Patrón**: `[Apellido]123` (ej: Usuario "Juan Pérez" → `Perez123`)
- **Cambio Obligatorio**: First-login password reset requirement
- **Validación**: Política de contraseñas robusta (8+ chars, mayús/minús/números)
- **Auditoría**: Registro completo de cambios de contraseña

### **🏗️ Arquitectura de Configuración**

```properties
# application.properties - Sistema de configuración híbrido
# Producción: Variables de entorno + Vault/Config Server
# Desarrollo: .env file para simplicidad

# Database con pool de conexiones optimizado
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# JWT Security con rotación automática
jwt.secret=${JWT_SECRET}
jwt.expiration-ms=${JWT_EXPIRATION_MS:3600000}
jwt.refresh-expiration-ms=604800000  # 7 días

# Analytics y Performance
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.show-sql=${SHOW_SQL:false}

# Actuator para monitoreo (solo en desarrollo)
management.endpoints.web.exposure.include=${ACTUATOR_ENDPOINTS:health,info}
```

> 🔒 **Seguridad Empresarial**:
> - Archivo `.env` en `.gitignore` (nunca commited)
> - Rotación automática de secrets en producción
> - Validación de configuración en startup

---

## 🧪 Testing y Demo

### **🎯 Acceso Demo Inmediato**

```bash
# Credenciales por defecto (cambiar en producción)
SuperAdmin: superadmin@sistema.com / secret
```

### **🚀 Flujo de Testing Completo**

1. **Login y Exploración**: Swagger UI → Authorize → Explorar endpoints
2. **Crear Usuarios**: POST `/api/superadmin/usuarios` con diferentes roles
3. **Generar Tickets**: POST `/api/tickets` como Trabajador
4. **Workflow Completo**: Asignar → Resolver → Evaluar
5. **Ver Estadísticas**: Dashboard ejecutivo con datos reales

### **📊 Testing de Analytics**

```bash
# Generar datos de prueba para estadísticas
curl -X POST "http://localhost:8080/api/admin/generar-datos-demo" \
  -H "Authorization: Bearer {token}"

# Verificar métricas generadas
curl "http://localhost:8080/api/estadisticas/resumen-ejecutivo" \
  -H "Authorization: Bearer {token}"
```

### **🔍 Herramientas de Testing**

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Postman Collection**: Ver `/docs/postman/` para colección completa
- **Health Check**: http://localhost:8080/actuator/health

---

## 🌟 Características Empresariales

### **📈 Sistema de Analytics Avanzado**

- **Dashboard Ejecutivo**: KPIs en tiempo real con visualizaciones
- **Métricas de Productividad**: Rankings de técnicos y análisis de carga
- **Análisis Predictivo**: Tendencias de tickets y forecasting
- **Reportes Personalizables**: Filtros por fechas, departamentos, usuarios

### **🔄 Workflow Inteligente de Tickets**

- **Estados Automáticos**: `NO_ATENDIDO` → `ATENDIDO` → `RESUELTO` → `FINALIZADO`
- **Asignación Inteligente**: Balanceo de carga automática entre técnicos
- **Evaluación del Usuario**: Aceptar/rechazar soluciones con comentarios
- **Solicitudes de Devolución**: Técnicos pueden solicitar reasignación con justificación

### **👥 Gestión Avanzada de Usuarios**

- **Jerarquía de Roles**: Permisos granulares y escalabilidad
- **Estados Dinámicos**: Activo/Inactivo/Bloqueado con políticas diferenciadas
- **Auditoría Total**: Trazabilidad completa de acciones y cambios
- **Autenticación Robusta**: JWT con refresh tokens y contexto de sesión

### **🔔 Sistema de Notificaciones Inteligente**

- **Categorización Automática**: Por tipo, prioridad y contexto
- **Estados de Lectura**: Seguimiento completo del engagement
- **Metadatos Ricos**: Referencias a entidades, acciones sugeridas
- **Expiración Automática**: Limpieza inteligente de notificaciones

### **🛡️ Seguridad y Compliance**

- **Auditoría Completa**: Logs estructurados con contexto completo (IP, User-Agent)
- **Trazabilidad JSON**: Valores anteriores/nuevos para cambios críticos
- **Soft Delete**: Preservación de datos para compliance y análisis
- **Protección Anti-Timing**: Prevención de ataques de enumeración

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

- **1.0.x**: Sistema base con CRUD completo
- **1.1.x**: Auditoría y notificaciones avanzadas
- **1.2.x**: Analytics y migración segura ← **Versión Actual**
- **1.3.x**: Dashboard web y exportación (próximamente)

**Versión Actual**: `1.2.0` con analytics avanzado y migración segura

---

## 👨‍💻 Créditos

**Desarrollador Principal**: Sebastian Conde - [@Akc9912](https://github.com/Akc9912)

**Colaboradores y Características**:
- ✅ Análisis de requerimientos y arquitectura empresarial
- ✅ Implementación de sistema de estadísticas avanzado
- ✅ Desarrollo de migración segura de base de datos
- ✅ Dashboard ejecutivo con métricas en tiempo real
- ✅ Sistema de auditoría completa con trazabilidad JSON

---

<div align="center">

**🎯 ApiTickets v1.2.0 - Sistema Empresarial de Gestión de Tickets**

[![GitHub](https://img.shields.io/badge/GitHub-Akc9912/apiTickets-blue.svg)](https://github.com/Akc9912/apiTickets)
[![Documentación](https://img.shields.io/badge/Docs-Swagger_UI-green.svg)](http://localhost:8080/swagger-ui/index.html)
[![Migración](https://img.shields.io/badge/Migration-Safe_Scripts-orange.svg)](./MIGRATION_GUIDE.md)
[![Analytics](https://img.shields.io/badge/Analytics-Advanced_Dashboard-purple.svg)](#-sistema-de-estadísticas-avanzado)

📖 [Documentación Técnica](./TECHNICAL_DOCS.md) •
🔄 [Guía de Migración](./MIGRATION_GUIDE.md) •
📈 [Changelog](./CHANGELOG.md) •
🐛 [Issues](https://github.com/Akc9912/apiTickets/issues)

**¿Preguntas? ¿Sugerencias? ¡Abre un [GitHub Discussion](https://github.com/Akc9912/apiTickets/discussions)!**

</div>
