# 📚 Documentación - ApiTickets

Bienvenido a la documentación completa del sistema ApiTickets. Aquí encontrarás toda la información necesaria para entender, desarrollar, desplegar y mantener el sistema.

## 📋 Índice de Documentación

### 🏗️ [Arquitectura](./architecture/)
Documentación técnica de la arquitectura del sistema, patrones de diseño y decisiones arquitectónicas.

- **Patrones Arquitectónicos**: Clean Architecture, DDD, CQRS
- **Estructura de Capas**: Presentation, Service, Repository, Domain
- **Modelo de Datos**: Entidades, relaciones y diseño de base de datos
- **Escalabilidad**: Estrategias de escalamiento horizontal y vertical
- **Seguridad**: Arquitectura de seguridad y control de acceso

### 📡 [API Documentation](./api/)
Documentación completa de todos los endpoints de la API REST.

- **Endpoints de Autenticación**: Login, registro, refresh tokens
- **Endpoints de Tickets**: CRUD completo y operaciones especiales
- **Endpoints de Usuarios**: Gestión de usuarios y perfiles
- **Endpoints de Estadísticas**: Analytics y reportes
- **Códigos de Error**: Manejo de errores y troubleshooting
- **Ejemplos de Uso**: Casos prácticos y flujos completos

### 👨‍💻 [Desarrollo](./development/)
Guías para desarrolladores y contribuidores del proyecto.

- **[Fases de Desarrollo](./development/phases.md)**: Roadmap completo del proyecto
- **Estándares de Código**: Convenciones y mejores prácticas
- **Setup de Desarrollo**: Configuración del ambiente local
- **Testing**: Estrategias de testing y coverage
- **Contribución**: Cómo contribuir al proyecto

### 🚢 [Despliegue](./deployment/)
Guías completas para el despliegue en diferentes ambientes.

- **Despliegue Local**: Setup para desarrollo
- **Docker**: Containerización y orquestación
- **Cloud Deployment**: AWS, Azure, GCP
- **Monitoreo**: Prometheus, Grafana, alertas
- **CI/CD**: Pipelines de integración y despliegue

### 📝 [Changelog](./CHANGELOG.md)
Historial detallado de cambios, versiones y mejoras del sistema.

---

## 🚀 Quick Start

### Para Desarrolladores
```bash
# 1. Clonar repositorio
git clone https://github.com/Akc9912/apiTickets.git

# 2. Leer documentación de desarrollo
open docs/development/phases.md

# 3. Setup local
./mvnw spring-boot:run
```

### Para DevOps
```bash
# 1. Revisar guía de despliegue
open docs/deployment/README.md

# 2. Despliegue con Docker
docker-compose up -d

# 3. Verificar health checks
curl http://localhost:8080/actuator/health
```

### Para Usuarios de API
```bash
# 1. Revisar documentación de API
open docs/api/README.md

# 2. Swagger UI interactivo
open http://localhost:8080/swagger-ui/index.html

# 3. Probar endpoint
curl http://localhost:8080/api/v1/auth/health
```

---

## 🎯 Navegación por Roles

### 👨‍💼 **Product Manager / Business**
- 📊 [Fases de Desarrollo](./development/phases.md) - Roadmap y features
- 📈 [Changelog](./CHANGELOG.md) - Progreso y releases
- 📋 [API Overview](./api/README.md) - Capacidades del sistema

### 👨‍💻 **Desarrollador**
- 🏗️ [Arquitectura](./architecture/README.md) - Diseño técnico
- 🛠️ [Fases de Desarrollo](./development/phases.md) - Trabajo pendiente
- 📡 [API Documentation](./api/README.md) - Endpoints y contratos

### 🔧 **DevOps / SRE**
- 🚢 [Deployment Guide](./deployment/README.md) - Guías de despliegue
- 📊 [Monitoring](./deployment/README.md#monitoreo-y-observabilidad) - Observabilidad
- 🏗️ [Architecture](./architecture/README.md#escalabilidad) - Escalabilidad

### 🧪 **QA / Tester**
- 📡 [API Testing](./api/README.md#ejemplos-de-uso) - Casos de prueba
- 🧪 [Development](./development/phases.md) - Criterios de calidad
- 📝 [Changelog](./CHANGELOG.md) - Features a testear

---

## 📚 Recursos Adicionales

### 🔗 Links Útiles
- **Repositorio**: [GitHub](https://github.com/Akc9912/apiTickets)
- **Issues**: [GitHub Issues](https://github.com/Akc9912/apiTickets/issues)
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

### 📖 Documentación Externa
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [MySQL 8.0 Documentation](https://dev.mysql.com/doc/refman/8.0/en/)
- [Docker Documentation](https://docs.docker.com/)

### 🛠️ Herramientas de Desarrollo
- **IDE**: IntelliJ IDEA, VS Code
- **API Testing**: Postman, Insomnia
- **Database**: MySQL Workbench, phpMyAdmin
- **Monitoring**: Prometheus, Grafana

---

## 🤝 Contribución a la Documentación

### 📝 Cómo Contribuir
1. **Fork** el repositorio
2. **Crear** rama `docs/nueva-documentacion`
3. **Editar** archivos Markdown
4. **Crear** Pull Request

### 📋 Estándares de Documentación
- **Formato**: Markdown con emojis para mejor navegación
- **Estructura**: Headers jerárquicos claros
- **Enlaces**: Links relativos entre documentos
- **Ejemplos**: Código de ejemplo funcional
- **Actualización**: Fecha de última actualización

---

## 📞 Soporte

### 🆘 Obtener Ayuda
- **Documentación**: Revisar esta documentación primero
- **Issues**: [Crear issue en GitHub](https://github.com/Akc9912/apiTickets/issues/new)
- **Discussions**: [GitHub Discussions](https://github.com/Akc9912/apiTickets/discussions)

### 📧 Contacto
- **Autor**: Sebastian Kc
- **Email**: akc9912@gmail.com
- **GitHub**: [@Akc9912](https://github.com/Akc9912)

---

## 🎯 Estado de la Documentación

| Documento | Estado | Última Actualización | Versión |
|-----------|--------|---------------------|---------|
| [README Principal](../README.md) | ✅ Completo | Oct 2025 | v2.0 |
| [Arquitectura](./architecture/README.md) | ✅ Completo | Oct 2025 | v1.0 |
| [API Docs](./api/README.md) | ✅ Completo | Oct 2025 | v1.0 |
| [Fases Desarrollo](./development/phases.md) | ✅ Completo | Oct 2025 | v1.0 |
| [Deployment](./deployment/README.md) | ✅ Completo | Oct 2025 | v1.0 |
| [Changelog](./CHANGELOG.md) | ✅ Completo | Oct 2025 | v1.0 |

---

*Esta documentación está en constante evolución. Última actualización: Octubre 2025*
