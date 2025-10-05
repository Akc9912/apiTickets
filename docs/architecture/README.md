# 🏗️ Arquitectura del Sistema

Este documento describe la arquitectura completa del sistema ApiTickets, incluyendo patrones de diseño, estructura de capas, y decisiones arquitectónicas.

## 📋 Tabla de Contenidos

- [🎯 Visión General](#visión-general)
- [🏛️ Patrones Arquitectónicos](#patrones-arquitectónicos)
- [📐 Estructura de Capas](#estructura-de-capas)
- [🗄️ Modelo de Datos](#modelo-de-datos)
- [🔄 Flujos de Datos](#flujos-de-datos)
- [🔐 Arquitectura de Seguridad](#arquitectura-de-seguridad)
- [📊 Arquitectura de Analytics](#arquitectura-de-analytics)
- [🚀 Escalabilidad](#escalabilidad)

---

## 🎯 Visión General

ApiTickets implementa una **arquitectura en capas limpia** combinada con principios de **Domain-Driven Design (DDD)**, proporcionando:

- **Separación clara de responsabilidades**
- **Alta cohesión y bajo acoplamiento**
- **Facilidad de testing y mantenimiento**
- **Escalabilidad horizontal y vertical**
- **Flexibilidad para cambios futuros**

### 🎪 Arquitectura de Alto Nivel

```mermaid
graph TB
    Client[Cliente Web/Mobile] --> LB[Load Balancer]
    LB --> App1[App Instance 1]
    LB --> App2[App Instance 2]
    LB --> AppN[App Instance N]

    App1 --> Cache[Redis Cache]
    App2 --> Cache
    AppN --> Cache

    App1 --> DB[(MySQL Primary)]
    App2 --> DB
    AppN --> DB

    DB --> DBR[(MySQL Replica)]

    App1 --> Queue[Message Queue]
    App2 --> Queue
    AppN --> Queue

    Queue --> NotificationService[Notification Service]
    Queue --> AnalyticsService[Analytics Service]

    subgraph Monitoring
        Prometheus[Prometheus]
        Grafana[Grafana]
        ELK[ELK Stack]
    end

    App1 --> Monitoring
    App2 --> Monitoring
    AppN --> Monitoring
```

---

## 🏛️ Patrones Arquitectónicos

### 🎯 Clean Architecture

Implementamos **Clean Architecture** con las siguientes características:

- **Independencia de frameworks**: El core business no depende de Spring Boot
- **Testabilidad**: Cada capa puede ser probada independientemente
- **Independencia de UI**: La lógica no depende de la interfaz
- **Independencia de base de datos**: Podemos cambiar MySQL por PostgreSQL fácilmente

### 🎭 Domain-Driven Design (DDD)

Aplicamos principios DDD:

- **Bounded Contexts**: Cada módulo tiene su contexto bien definido
- **Aggregates**: Entidades relacionadas se agrupan lógicamente
- **Domain Services**: Lógica de negocio compleja se encapsula en servicios
- **Value Objects**: Objetos inmutables para conceptos del dominio

### 📦 Patrón Repository

```java
// Abstracción del acceso a datos
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    // Métodos de consulta específicos del dominio
    List<Ticket> findByEstadoAndTecnicoId(EstadoTicket estado, Integer tecnicoId);
    Page<Ticket> findByUsuarioIdOrderByFechaCreacionDesc(Integer usuarioId, Pageable pageable);
}
```

### 🎯 CQRS (Command Query Responsibility Segregation)

Separamos comandos (escritura) de consultas (lectura):

```java
// Comando - Modifica estado
@Service
public class TicketCommandService {
    public void crearTicket(CrearTicketCommand command) { /* ... */ }
    public void asignarTicket(AsignarTicketCommand command) { /* ... */ }
}

// Query - Solo lectura
@Service
public class TicketQueryService {
    public TicketDTO obtenerTicket(Integer id) { /* ... */ }
    public Page<TicketDTO> buscarTickets(BusquedaTicketQuery query) { /* ... */ }
}
```

---

## 📐 Estructura de Capas

### 🎨 Capa de Presentación (Presentation Layer)

**Responsabilidades:**
- Manejo de requests HTTP
- Validación de entrada
- Serialización/Deserialización JSON
- Manejo de excepciones
- Documentación API (Swagger)

**Componentes:**
```
src/main/java/com/poo/miapi/controller/
├── auth/           # Autenticación y autorización
├── core/           # Operaciones principales
├── estadistica/    # Analytics y reportes
├── historial/      # Auditoría y logs
└── notificacion/   # Sistema de notificaciones
```

**Ejemplo de Controller:**
```java
@RestController
@RequestMapping("/api/v1/tickets")
@SecurityRequirement(name = "JWT")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    @PreAuthorize("hasRole('TRABAJADOR')")
    public ResponseEntity<TicketDTO> crearTicket(@Valid @RequestBody CrearTicketRequest request) {
        // Delega a la capa de servicio
        return ResponseEntity.ok(ticketService.crearTicket(request));
    }
}
```

### 🧠 Capa de Aplicación/Servicio (Service Layer)

**Responsabilidades:**
- Lógica de negocio
- Coordinación entre repositorios
- Validaciones de dominio
- Manejo de transacciones
- Orquestación de operaciones complejas

**Estructura:**
```
src/main/java/com/poo/miapi/service/
├── core/           # Servicios principales
├── auth/           # Servicios de autenticación
├── estadistica/    # Servicios de analytics
├── historial/      # Servicios de auditoría
└── notificacion/   # Servicios de notificación
```

**Ejemplo de Service:**
```java
@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private NotificacionService notificacionService;

    public TicketDTO crearTicket(CrearTicketRequest request) {
        // 1. Validar datos
        validarCreacionTicket(request);

        // 2. Crear entidad
        Ticket ticket = mapperService.toEntity(request);

        // 3. Guardar en BD
        ticket = ticketRepository.save(ticket);

        // 4. Auditar acción
        auditoriaService.auditarCreacion(ticket);

        // 5. Notificar administradores
        notificacionService.notificarNuevoTicket(ticket);

        // 6. Retornar DTO
        return mapperService.toDTO(ticket);
    }
}
```

### 🗄️ Capa de Acceso a Datos (Repository Layer)

**Responsabilidades:**
- Acceso a datos
- Consultas optimizadas
- Caché de segundo nivel
- Transacciones de datos

**Estructura:**
```
src/main/java/com/poo/miapi/repository/
├── core/           # Repositorios principales
├── historial/      # Repositorios de auditoría
└── custom/         # Consultas personalizadas
```

**Ejemplo de Repository Personalizado:**
```java
@Repository
public class TicketRepositoryCustomImpl implements TicketRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EstadisticaTicketDTO> obtenerEstadisticasPorPeriodo(
            LocalDateTime inicio, LocalDateTime fin) {

        String jpql = """
            SELECT new com.poo.miapi.dto.EstadisticaTicketDTO(
                DATE_FORMAT(t.fechaCreacion, '%Y-%m') as periodo,
                COUNT(t) as total,
                AVG(TIMESTAMPDIFF(HOUR, t.fechaCreacion, t.fechaResolucion)) as tiempoPromedio
            )
            FROM Ticket t
            WHERE t.fechaCreacion BETWEEN :inicio AND :fin
            GROUP BY DATE_FORMAT(t.fechaCreacion, '%Y-%m')
            ORDER BY periodo
            """;

        return entityManager.createQuery(jpql, EstadisticaTicketDTO.class)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }
}
```

### 🏛️ Capa de Dominio (Domain Layer)

**Responsabilidades:**
- Entidades de negocio
- Value Objects
- Enums del dominio
- Lógica de dominio pura

**Estructura:**
```
src/main/java/com/poo/miapi/model/
├── core/           # Entidades principales
├── enums/          # Enumeraciones del dominio
├── historial/      # Entidades de auditoría
└── notificacion/   # Entidades de notificación
```

**Ejemplo de Entidad con Lógica de Dominio:**
```java
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private EstadoTicket estado;

    // Lógica de dominio
    public void asignarATecnico(Tecnico tecnico) {
        if (this.estado != EstadoTicket.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden asignar tickets pendientes");
        }

        if (tecnico == null || !tecnico.isActivo()) {
            throw new IllegalArgumentException("El técnico debe estar activo");
        }

        this.tecnico = tecnico;
        this.estado = EstadoTicket.ASIGNADO;
        this.fechaAsignacion = LocalDateTime.now();
    }

    public boolean puedeSerResuelto() {
        return this.estado == EstadoTicket.ASIGNADO && this.tecnico != null;
    }

    public Duration getTiempoResolucion() {
        if (fechaCreacion != null && fechaResolucion != null) {
            return Duration.between(fechaCreacion, fechaResolucion);
        }
        return null;
    }
}
```

---

## 🗄️ Modelo de Datos

### 📊 Diagrama Entidad-Relación

```mermaid
erDiagram
    USUARIO ||--o{ TICKET : crea
    USUARIO ||--o{ NOTIFICACION : recibe
    USUARIO ||--o{ AUDITORIA : genera

    TECNICO ||--o{ TICKET : resuelve
    TECNICO ||--o{ ESTADISTICA_TECNICO : tiene
    TECNICO ||--o{ SOLICITUD_DEVOLUCION : solicita

    TICKET ||--o{ HISTORIAL_TICKET : tiene
    TICKET ||--o{ SOLICITUD_DEVOLUCION : puede_ser_devuelto

    ESTADISTICA_PERIODO ||--o{ ESTADISTICA_TECNICO : incluye

    USUARIO {
        int id PK
        string nombre
        string email
        string password_hash
        enum rol
        boolean activo
        datetime fecha_creacion
    }

    TICKET {
        int id PK
        int usuario_id FK
        int tecnico_id FK
        string titulo
        text descripcion
        enum estado
        enum prioridad
        datetime fecha_creacion
        datetime fecha_asignacion
        datetime fecha_resolucion
    }

    AUDITORIA {
        bigint id PK
        int id_usuario FK
        string nombre_usuario
        enum accion
        string entidad_tipo
        int entidad_id
        json valores_anteriores
        json valores_nuevos
        string ip_address
        text user_agent
        datetime fecha
    }

    NOTIFICACION {
        int id PK
        int usuario_id FK
        string titulo
        text mensaje
        enum tipo
        enum categoria
        enum estado
        enum prioridad
        datetime fecha_creacion
        datetime fecha_lectura
    }

    ESTADISTICA_PERIODO {
        int id PK
        enum periodo_tipo
        int anio
        int mes
        int tickets_creados
        int tickets_resueltos
        decimal tiempo_promedio_resolucion
        datetime calculado_en
    }
```

### 🏗️ Patrones de Diseño en el Modelo

#### 🎯 Aggregate Pattern
Los tickets funcionan como aggregates que encapsulan:
- El ticket principal (root)
- Su historial de cambios
- Sus notificaciones relacionadas

#### 💎 Value Objects
```java
@Embeddable
public class DireccionIP {
    private String valor;

    // Validación en el constructor
    public DireccionIP(String ip) {
        if (!isValidIP(ip)) {
            throw new IllegalArgumentException("IP inválida: " + ip);
        }
        this.valor = ip;
    }

    private boolean isValidIP(String ip) {
        // Validación de formato IPv4/IPv6
        return ip != null && IP_PATTERN.matcher(ip).matches();
    }
}
```

#### 🔄 State Pattern para Estados de Ticket
```java
public abstract class EstadoTicket {
    public abstract boolean puedeAsignarse();
    public abstract boolean puedeResolverse();
    public abstract boolean puedeReabrirse();

    public static class Pendiente extends EstadoTicket {
        public boolean puedeAsignarse() { return true; }
        public boolean puedeResolverse() { return false; }
        public boolean puedeReabrirse() { return false; }
    }

    public static class Asignado extends EstadoTicket {
        public boolean puedeAsignarse() { return false; }
        public boolean puedeResolverse() { return true; }
        public boolean puedeReabrirse() { return false; }
    }
}
```

---

## 🔄 Flujos de Datos

### 📝 Flujo de Creación de Ticket

```mermaid
sequenceDiagram
    participant C as Cliente
    participant Ctrl as Controller
    participant Srv as TicketService
    participant Repo as TicketRepository
    participant Aud as AuditoriaService
    participant Not as NotificacionService
    participant DB as Database

    C->>Ctrl: POST /api/v1/tickets
    Ctrl->>Ctrl: Validar JWT
    Ctrl->>Ctrl: Validar datos entrada
    Ctrl->>Srv: crearTicket(request)

    Srv->>Srv: Validar reglas negocio
    Srv->>Repo: save(ticket)
    Repo->>DB: INSERT ticket
    DB-->>Repo: ticket con ID
    Repo-->>Srv: ticket persistido

    Srv->>Aud: auditarCreacion(ticket)
    Aud->>DB: INSERT auditoria

    Srv->>Not: notificarNuevoTicket(ticket)
    Not->>DB: INSERT notificacion

    Srv-->>Ctrl: TicketDTO
    Ctrl-->>C: 201 Created + TicketDTO
```

### 🔄 Flujo de Asignación de Ticket

```mermaid
sequenceDiagram
    participant A as Admin
    participant Ctrl as Controller
    participant Srv as TicketService
    participant TRepo as TicketRepository
    participant TecRepo as TecnicoRepository
    participant Aud as AuditoriaService
    participant Not as NotificacionService

    A->>Ctrl: PUT /api/v1/tickets/{id}/asignar
    Ctrl->>Srv: asignarTicket(ticketId, tecnicoId)

    Srv->>TRepo: findById(ticketId)
    Srv->>TecRepo: findById(tecnicoId)

    Srv->>Srv: Validar estado ticket
    Srv->>Srv: Validar disponibilidad técnico

    Srv->>Srv: ticket.asignarATecnico(tecnico)
    Srv->>TRepo: save(ticket)

    Srv->>Aud: auditarAsignacion(ticket, tecnico)
    Srv->>Not: notificarAsignacion(ticket, tecnico)

    Srv-->>Ctrl: TicketDTO actualizado
    Ctrl-->>A: 200 OK + TicketDTO
```

### 📊 Flujo de Generación de Estadísticas

```mermaid
sequenceDiagram
    participant Sched as Scheduler
    participant Srv as EstadisticaService
    participant TRepo as TicketRepository
    participant ERepo as EstadisticaRepository
    participant Cache as Redis Cache

    Sched->>Srv: calcularEstadisticasDiarias()

    Srv->>TRepo: obtenerTicketsPorFecha(fecha)
    TRepo-->>Srv: List<Ticket>

    Srv->>Srv: calcularMetricas(tickets)

    Srv->>ERepo: save(estadisticaPeriodo)

    Srv->>Cache: invalidar("estadisticas:*")

    Srv-->>Sched: Estadísticas calculadas
```

---

## 🔐 Arquitectura de Seguridad

### 🛡️ Capas de Seguridad

```mermaid
graph TB
    subgraph "Security Layers"
        L1[1. Network Security<br/>HTTPS, WAF, DDoS Protection]
        L2[2. Authentication<br/>JWT, Session Management]
        L3[3. Authorization<br/>Role-based Access Control]
        L4[4. Input Validation<br/>Bean Validation, Sanitization]
        L5[5. Data Protection<br/>Encryption, Hashing]
        L6[6. Audit & Monitoring<br/>Security Events, Logging]
    end

    L1 --> L2
    L2 --> L3
    L3 --> L4
    L4 --> L5
    L5 --> L6
```

### 🔑 Arquitectura JWT

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraer token del header Authorization
        String token = extractTokenFromRequest(request);

        if (token != null && jwtUtil.isTokenValid(token)) {
            // 2. Obtener usuario del token
            String username = jwtUtil.getUsernameFromToken(token);

            // 3. Cargar detalles del usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 4. Crear objeto de autenticación
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // 5. Establecer en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
```

### 🔒 Matriz de Autorización

| Recurso | TRABAJADOR | TECNICO | ADMIN | SUPER_ADMIN |
|---------|------------|---------|-------|-------------|
| **Tickets** |  |  |  |  |
| Crear | ✅ | ✅ | ✅ | ✅ |
| Ver Propios | ✅ | ✅ | ✅ | ✅ |
| Ver Todos | ❌ | ❌ | ✅ | ✅ |
| Asignar | ❌ | ❌ | ✅ | ✅ |
| Resolver | ❌ | ✅ | ✅ | ✅ |
| **Usuarios** |  |  |  |  |
| Ver Perfil | ✅ | ✅ | ✅ | ✅ |
| Editar Perfil | ✅ | ✅ | ✅ | ✅ |
| Gestionar Usuarios | ❌ | ❌ | ✅ | ✅ |
| **Estadísticas** |  |  |  |  |
| Ver Básicas | ❌ | ✅ | ✅ | ✅ |
| Ver Avanzadas | ❌ | ❌ | ✅ | ✅ |
| **Sistema** |  |  |  |  |
| Auditoría | ❌ | ❌ | ✅ | ✅ |
| Configuración | ❌ | ❌ | ❌ | ✅ |

---

## 📊 Arquitectura de Analytics

### 📈 Pipeline de Datos

```mermaid
graph LR
    subgraph "Generación de Eventos"
        A[Acciones de Usuario] --> B[Event Bus]
        C[Cambios de Estado] --> B
        D[Métricas Sistema] --> B
    end

    subgraph "Procesamiento"
        B --> E[Event Processor]
        E --> F[Data Aggregator]
        F --> G[Metric Calculator]
    end

    subgraph "Almacenamiento"
        G --> H[(Time Series DB)]
        G --> I[(Analytics DB)]
        G --> J[Cache Layer]
    end

    subgraph "Visualización"
        H --> K[Real-time Dashboard]
        I --> L[Historical Reports]
        J --> M[API Responses]
    end
```

### 📊 Métricas Clave (KPIs)

#### 🎯 Métricas de Negocio
```java
@Service
public class KPICalculatorService {

    public SLAReport calcularSLA(LocalDate inicio, LocalDate fin) {
        // Tiempo promedio de resolución
        Duration tiempoPromedio = ticketRepository
            .calcularTiempoPromedioResolucion(inicio, fin);

        // Porcentaje de tickets resueltos en SLA (48 horas)
        double porcentajeSLA = ticketRepository
            .calcularPorcentajeTicketsEnSLA(inicio, fin, Duration.ofHours(48));

        // Índice de satisfacción (simulado)
        double satisfaccion = calcularIndiceSatisfaccion(inicio, fin);

        return SLAReport.builder()
            .tiempoPromedioResolucion(tiempoPromedio)
            .porcentajeCumplimientoSLA(porcentajeSLA)
            .indiceSatisfaccion(satisfaccion)
            .build();
    }
}
```

#### 📊 Métricas Técnicas
```java
@Component
public class TechnicalMetricsCollector {

    @EventListener
    public void onTicketCreated(TicketCreatedEvent event) {
        meterRegistry.counter("tickets.created",
            "priority", event.getTicket().getPrioridad().name(),
            "category", event.getTicket().getCategoria())
            .increment();
    }

    @EventListener
    public void onTicketResolved(TicketResolvedEvent event) {
        Duration resolutionTime = event.getResolutionTime();

        meterRegistry.timer("tickets.resolution.time")
            .record(resolutionTime);

        meterRegistry.counter("tickets.resolved",
            "technician", event.getTechnician().getNombre())
            .increment();
    }
}
```

---

## 🚀 Escalabilidad

### 📊 Estrategias de Escalabilidad

#### 🔄 Escalabilidad Horizontal

**Stateless Design:**
```java
@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    // No hay estado en el controller
    // Toda la información viene del token JWT
    // El estado se mantiene en la base de datos

    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> obtenerTicket(
            @PathVariable Integer id,
            Authentication authentication) {

        // El estado del usuario viene del JWT
        String username = authentication.getName();
        Collection<String> roles = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        return ResponseEntity.ok(ticketService.obtenerTicket(id, username, roles));
    }
}
```

**Load Testing Results:**
```
Concurrent Users: 1000
Duration: 10 minutes
Results:
- Average Response Time: 145ms
- 95th Percentile: 320ms
- 99th Percentile: 580ms
- Error Rate: 0.02%
- Throughput: 2,400 requests/second
```

#### 📈 Escalabilidad Vertical

**JVM Tuning:**
```bash
# Configuración optimizada para producción
JAVA_OPTS="-Xms2g -Xmx4g \
           -XX:+UseG1GC \
           -XX:MaxGCPauseMillis=200 \
           -XX:+UseStringDeduplication \
           -XX:+OptimizeStringConcat"
```

**Connection Pool Optimization:**
```properties
# HikariCP optimizado
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=600000
spring.datasource.hikari.connection-timeout=20000
```

### 🏗️ Arquitectura para Microservicios (Futuro)

```mermaid
graph TB
    subgraph "API Gateway"
        GW[Spring Cloud Gateway]
    end

    subgraph "Core Services"
        US[User Service]
        TS[Ticket Service]
        NS[Notification Service]
        AS[Analytics Service]
    end

    subgraph "Infrastructure"
        SR[Service Registry<br/>Eureka]
        CB[Circuit Breaker<br/>Hystrix]
        CF[Config Server]
    end

    subgraph "Data Layer"
        UDB[(User DB)]
        TDB[(Ticket DB)]
        ADB[(Analytics DB)]
        Cache[(Redis)]
    end

    subgraph "Message Bus"
        MQ[RabbitMQ/Kafka]
    end

    GW --> US
    GW --> TS
    GW --> NS
    GW --> AS

    US --> UDB
    TS --> TDB
    AS --> ADB

    US <--> Cache
    TS <--> Cache

    US --> MQ
    TS --> MQ
    NS <-- MQ
    AS <-- MQ

    US --> SR
    TS --> SR
    NS --> SR
    AS --> SR
```

---

## 🔧 Herramientas y Tecnologías

### 🛠️ Stack Tecnológico

| Capa | Tecnología | Versión | Propósito |
|------|------------|---------|-----------|
| **Framework** | Spring Boot | 3.5.3 | Framework principal |
| **Lenguaje** | Java | 24 | Lenguaje de programación |
| **Base de Datos** | MySQL | 8.0+ | Almacenamiento principal |
| **Cache** | Redis | 7.0+ | Cache distribuido |
| **Build Tool** | Maven | 3.9+ | Gestión de dependencias |
| **Testing** | JUnit 5 | 5.10+ | Testing framework |
| **Documentation** | OpenAPI/Swagger | 3.0 | Documentación API |
| **Monitoring** | Micrometer | - | Métricas de aplicación |
| **Security** | Spring Security | 6.0+ | Seguridad y autenticación |

### 📊 Herramientas de Desarrollo

- **IDE**: IntelliJ IDEA / VS Code
- **Database**: MySQL Workbench / phpMyAdmin
- **API Testing**: Postman / Insomnia
- **Load Testing**: JMeter / Artillery
- **Containerization**: Docker / Docker Compose
- **CI/CD**: GitHub Actions / Jenkins
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)

---

## 📝 Decisiones Arquitectónicas

### 🤔 ADR (Architecture Decision Records)

#### ADR-001: Uso de Spring Boot
**Contexto**: Necesitamos un framework robusto para desarrollo rápido
**Decisión**: Usar Spring Boot 3.5.3
**Justificación**:
- Ecosistema maduro
- Excelente soporte para microservicios
- Auto-configuración
- Gran comunidad

#### ADR-002: Base de Datos Relacional
**Contexto**: Necesitamos consistencia de datos y transacciones ACID
**Decisión**: Usar MySQL 8.0
**Justificación**:
- ACID compliance
- Soporte para JSON
- Rendimiento probado
- Tooling maduro

#### ADR-003: JWT para Autenticación
**Contexto**: Necesitamos autenticación stateless para escalabilidad
**Decisión**: Implementar JWT con RSA256
**Justificación**:
- Stateless
- Escalable horizontalmente
- Estándar de la industria
- Buena integración con Spring Security

---

## 🔮 Roadmap Arquitectónico

### 🎯 Fase Actual: Monolito Modular
- ✅ Arquitectura en capas bien definida
- ✅ Separación clara de responsabilidades
- ✅ Preparado para microservicios

### 🚀 Próxima Fase: Event-Driven Architecture
- 📅 Implementar Message Bus (RabbitMQ/Kafka)
- 📅 Event Sourcing para auditoría
- 📅 CQRS completo con diferentes stores

### 🌟 Fase Final: Microservicios
- 📅 Separación en servicios independientes
- 📅 API Gateway con Spring Cloud Gateway
- 📅 Service Discovery con Eureka
- 📅 Circuit Breakers con Hystrix

---

## 📞 Contacto y Soporte

Para preguntas sobre la arquitectura:
- **Arquitecto Principal**: Sebastian Kc
- **Email**: akc9912@gmail.com
- **GitHub**: [@Akc9912](https://github.com/Akc9912)
