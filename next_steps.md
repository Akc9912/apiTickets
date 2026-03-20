# 🚀 Plan de Desarrollo - Sistema de Tickets

## 📌 Contexto Importante

> 🔄 **Estamos en Iteración 01 - Migración de Stack y Arquitectura**
> 
> Foco: PostgreSQL 16 + Supabase Auth + Arquitectura Modular
> 
> Esta iteración define las **versiones 1.1 a 2.0**. Las versiones posteriores (2.1+) están fuera del alcance.

---

## 🎯 Alcance por Iteración

### ✅ **Iteración 01: Migración de Stack y Arquitectura** (ACTUAL)

**Duración estimada:** 4-5 semanas

**Estado:** 35% completado

**Objetivos:**
- ✅ Migrar MySQL → PostgreSQL 16
- ✅ JWT local → Supabase Auth (tokens no generados en backend)
- ✅ Int IDs → UUID en todas partes
- ✅ Arquitectura modular con límites claros
- ✅ Tests automatizados (ArchUnit)

**Módulos MVP en esta iteración:**
1. **module/auth** → Validador de tokens Supabase (NO generador)
2. **module/account** → Perfil de usuario (renombrado de user, desacoplado de auth)
3. **module/ticket** → CRUD de tickets con UUID, desacoplado de account
4. **shared/** → Componentes comunes, contratos

**Deliverables:**
- ✅ `architecture.md` actualizado (fuente de verdad)
- ✅ `iteracion-01-migracion-stack-y-arquitectura/` completado
- ✅ Backend compila sin errores
- ✅ PostgreSQL 16 configurado
- ✅ Supabase Auth integrado
- ✅ Módulos con límites verificados en CI

**NO entra en esta iteración:**
- ❌ module/support (v1.2)
- ❌ module/product (v1.3)
- ❌ module/notification (v1.4)
- ❌ WebSocket en tiempo real
- ❌ Notificaciones por email
- ❌ Sistema de eventos completo

---

### 🟡 **Iteración 02: Autenticación Segura** (FUTURA, v1.1.0)

**Duración estimada:** 2-3 semanas

**Módulo:** Extensión de `module/auth`

**Funcionalidades:**
- Validación de email con código de 6 dígitos
- Recuperación de contraseña vía Supabase
- Rate limiting y expiración de códigos
- Integración con Supabase Admin API (opcional)

**Estado:** Diseñado, no iniciado

---

### 🟡 **Iteración 03: Prioridades de Tickets** (FUTURA, v1.2.0)

**Duración estimada:** 2 semanas

**Módulo:** Extensión de `module/ticket`

**Funcionalidades:**
- 4 niveles de prioridad (CRITICAL, HIGH, MEDIUM, LOW)
- SLA automático por prioridad
- Escalamiento automático si se excede SLA
- Scheduler para monitoreo

**Dependencias:** Iteración 01 completada

**Estado:** Diseñado, no iniciado

---

### 🟢 **Iteración 04: Integración de Sistemas Externos** (FUTURA, v1.3.0)

**Duración estimada:** 3-4 semanas

**Módulo:** Nuevo `module/product` (CREAR)

**Funcionalidades:**
- Recepción de mensajes desde sistemas externos
- Cola de mensajes para Support
- Acciones: Resolver, Crear Ticket, Descartar
- API Key para autenticación externa

**Dependencias:** Iteración 01 completada

**Estado:** Arquitectura definida, no iniciado

---

### 🔵 **Iteración 05: Notificaciones y Eventos** (FUTURA, v1.4.0)

**Duración estimada:** 3-4 semanas

**Módulo:** Nuevo `module/notification` (CREAR) + actualizaciones en otros

**Funcionalidades:**
- Motor de eventos para todos los módulos
- WebSocket para notificaciones en tiempo real
- Notificaciones por Email (opcional)
- Templates de notificación
- Preferencias de usuario

**Dependencias:** Iteraciones 01-04

**Estado:** Arquitectura definida, no iniciado

---

## 🔐 Versión 1.1.0 - Sistema de Autenticación Segura

> ⏭️ **FUTURA** - Iteración 02 (no en alcance de Iteración 01)

**Justificación:** El módulo auth ya existe y contiene la base de autenticación. Agregaremos nuevas funcionalidades sin crear módulo nuevo.

### Objetivo
Mejorar la seguridad del sistema mediante validación de email y recuperación de contraseña.

### Funcionalidades

#### 1. Validación de Cuenta por Email
**Flujo:**
1. Usuario se registra con email y datos básicos (sin contraseña)
2. Sistema genera código de verificación (6 dígitos)
3. Envío de email con código de verificación
4. Usuario ingresa código para validar cuenta
5. Usuario crea su contraseña
6. Cuenta activada

**Componentes a Crear:**

**En `module/auth/model/`:**
- `VerificationCode` (Entity) - Nueva
  - id, userId, code, expiresAt, verified, createdAt
- `PasswordResetToken` (Entity) - Nueva
  - id, userId, token, expiresAt, used, createdAt

**En `module/auth/repository/`:**
- `VerificationCodeRepository` - Nueva
- `PasswordResetTokenRepository` - Nueva

**En `module/auth/service/`:**
- `EmailService` - Nueva (para envío de emails)
- `VerificationService` - Nueva
- `AuthService` - EXTENDER (ya existe)

**En `module/auth/dto/`:**
- `VerificationCodeDto` - Nueva
- `EmailVerificationRequestDto` - Nueva
- `SetPasswordRequestDto` - Nueva

**Endpoints:**
```
POST /api/v1/auth/register (sin contraseña)
POST /api/v1/auth/verify-email (código + userId)
POST /api/v1/auth/set-password (userId + password)
POST /api/v1/auth/resend-code
```

#### 2. Recuperación de Contraseña
**Flujo:**
1. Usuario solicita recuperación con su email
2. Sistema genera token temporal
3. Envío de email con código/link de recuperación
4. Usuario ingresa código y nueva contraseña
5. Contraseña actualizada

**Componentes a Crear:**
- `PasswordResetToken` (Entity)
  - id, userId, token, expiresAt, used, createdAt
- `PasswordResetTokenRepository`
- Actualizar `AuthService`

**Endpoints:**
```
POST /api/v1/auth/forgot-password
POST /api/v1/auth/reset-password
POST /api/v1/auth/validate-reset-token
```

### Implementación

#### Fase 1.1.1: Configuración de Email
- [ ] Agregar dependencias de JavaMailSender
- [ ] Configurar SMTP en application.properties
- [ ] Crear `EmailService` base
- [ ] Crear templates de email (HTML)
- [ ] Testing de envío de emails

**Dependencias (pom.xml):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring6</artifactId>
</dependency>
```

**Configuración:**
```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
app.mail.from=${EMAIL_FROM:noreply@tickets.com}
```

#### Fase 1.1.2: Modelos y Repositorios
- [ ] Crear `VerificationCode` entity en `module/auth/model/`
- [ ] Crear `PasswordResetToken` entity en `module/auth/model/`
- [ ] Crear repositorios en `module/auth/repository/`
- [ ] Agregar campos a `User` entity (ya existe en `module/user/model/`)
  - `emailVerified: Boolean`
  - `accountActive: Boolean`

#### Fase 1.1.3: Servicios
- [ ] Implementar `EmailService` en `module/auth/service/`
  - sendVerificationCode()
  - sendPasswordReset()
  - sendWelcomeEmail()
- [ ] Implementar `VerificationService` en `module/auth/service/`
  - generateCode()
  - verifyCode()
  - expireOldCodes()
- [ ] Actualizar `AuthService` (ya existe en `module/auth/service/`)

#### Fase 1.1.4: Controllers y DTOs
- [ ] Crear DTOs en `module/auth/dto/`
- [ ] Actualizar `AuthController` (ya existe en `module/auth/controller/`)
- [ ] Agregar validaciones
- [ ] Testing de endpoints

#### Fase 1.1.5: Seguridad
- [ ] Implementar rate limiting para códigos
- [ ] Expiración de códigos (15 minutos)
- [ ] Limitar intentos de verificación
- [ ] Logs de seguridad

**Tiempo estimado:** 2-3 semanas

---

## 📊 Versión 1.2.0 - Sistema de Prioridades de Tickets

> ⏭️ **FUTURA** - Iteración 03 (no en alcance de Iteración 01)

**Justificación:** La funcionalidad de prioridades es parte integral del sistema de tickets. Se agregará a la estructura existente.

### Objetivo
Implementar un sistema de 4 niveles de prioridad para gestión eficiente de tickets.

### Niveles de Prioridad

| Nivel | Nombre | SLA | Color | Descripción |
|-------|--------|-----|-------|-------------|
| 1 | CRÍTICA | 2 horas | 🔴 Rojo | Sistema caído, pérdida de datos, seguridad comprometida |
| 2 | ALTA | 8 horas | 🟠 Naranja | Funcionalidad importante afectada, múltiples usuarios |
| 3 | MEDIA | 24 horas | 🟡 Amarillo | Funcionalidad menor afectada, solución alternativa existe |
| 4 | BAJA | 72 horas | 🟢 Verde | Mejoras, preguntas, solicitudes no urgentes |

### Funcionalidades

#### 1. Sistema de Prioridades
**Componentes a Crear:**

**En `module/ticket/enums/`:**
- `TicketPriority` (Enum) - Nueva
  - CRITICAL, HIGH, MEDIUM, LOW

**En `module/ticket/model/`:**
- `PriorityConfig` (Entity) - Nueva - Configuración de SLA
- `PriorityRule` (Entity) - Nueva - Reglas automáticas

**Actualizaciones en `module/ticket/model/Ticket`:**
- Agregar campo `priority: TicketPriority`
- Agregar `priorityChangedAt: LocalDateTime` para auditoría
- Agregar `priorityChangedBy: User` para tracking

#### 2. Asignación Automática por Prioridad
**Lógica:**
- Tickets críticos → Asignación inmediata a developer disponible
- Tickets altos → Cola de prioridad alta
- Tickets medios/bajos → Cola estándar

#### 3. Escalamiento Automático
**Reglas:**
- Si un ticket no se resuelve en 80% del SLA → Escalar prioridad
- Notificar al Admin cuando se escala
- Reasignar si es necesario

### Implementación

#### Fase 1.2.1: Modelos
- [ ] Crear enum `TicketPriority` en `module/ticket/enums/`
- [ ] Crear `PriorityConfig` entity en `module/ticket/model/`
- [ ] Crear `PriorityRule` entity en `module/ticket/model/`
- [ ] Actualizar `Ticket` entity (ya existe en `module/ticket/model/`)
- [ ] Crear `PriorityConfigRepository` en `module/ticket/repository/`
- [ ] Crear `PriorityRuleRepository` en `module/ticket/repository/`
- [ ] Migración de base de datos

**Estructura `PriorityConfig`:**
```java
@Entity
public class PriorityConfig {
    @Id
    @GeneratedValue
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private TicketPriority priority;
    
    private Integer slaHours;
    private String color;
    private String description;
    private Boolean autoEscalate;
    private Integer escalateThresholdPercent; // 80% del SLA
}
```

#### Fase 1.2.2: Servicios
- [ ] Crear `PriorityService` en `module/ticket/service/`
  - calculatePriority()
  - checkSLAViolation()
  - escalatePriority()
- [ ] Crear `PriorityRuleEngine` en `module/ticket/service/`
  - applyRules()
  - evaluateConditions()
- [ ] Actualizar `TicketService` (ya existe en `module/ticket/service/`)

#### Fase 1.2.3: Scheduler para SLA
- [ ] Crear `SLAMonitoringService` en `module/ticket/service/`
  - Ejecutar cada 30 minutos
  - Verificar tickets cerca del SLA
  - Escalar automáticamente
  - Notificar responsables

```java
@Scheduled(cron = "0 */30 * * * *") // Cada 30 minutos
public void monitorSLA() {
    List<Ticket> ticketsAtRisk = ticketRepository.findTicketsNearSLA();
    // Lógica de escalamiento
}
```

#### Fase 1.2.4: API y DTOs
- [ ] Crear `PriorityDto` en `module/ticket/dto/`
- [ ] Actualizar `TicketRequestDto` (ya existe en `module/ticket/dto/`)
- [ ] Actualizar `TicketResponseDto` (ya existe en `module/ticket/dto/`)
- [ ] Actualizar `TicketController` (ya existe en `module/ticket/controller/`)
- [ ] Endpoints para gestión de prioridades

**Endpoints:**
```
GET /api/v1/tickets/priority/{priority}
PUT /api/v1/tickets/{id}/priority
GET /api/v1/admin/priority-config
PUT /api/v1/admin/priority-config/{priority}
GET /api/v1/tickets/sla-violations
```

#### Fase 1.2.5: Dashboard y Métricas
- [ ] Estadísticas por prioridad
- [ ] Tiempo promedio por prioridad
- [ ] Tickets en riesgo de SLA
- [ ] Historial de escalamientos

**Tiempo estimado:** 2 semanas

---

## 🔌 Versión 1.3.0 - Sistema de Productos/Sistemas Externos

### 📁 Estrategia de Módulos
**Módulo a crear:** `module/product` (NUEVO)

**Justificación:** Los productos/sistemas externos son una funcionalidad completamente nueva y separada. Amerita su propio módulo para mantener la separación de responsabilidades.

**Estructura del nuevo módulo:**
```
module/product/
├── controller/
│   ├── ProductController.java
│   ├── ExternalMessageController.java (API externa)
│   └── SupportMessageController.java (API interna)
├── dto/
│   ├── ProductDto.java
│   ├── ExternalMessageDto.java
│   └── MessageActionDto.java
├── enums/
│   ├── ProductCategory.java
│   └── MessageStatus.java
├── model/
│   ├── Product.java
│   ├── ExternalMessage.java
│   └── MessageAction.java
├── repository/
│   ├── ProductRepository.java
│   ├── ExternalMessageRepository.java
│   └── MessageActionRepository.java
└── service/
    ├── ProductService.java
    ├── ExternalMessageService.java
    └── MessageProcessingService.java
```

**Integración con módulos existentes:**
- `module/ticket`: Para crear tickets desde mensajes
- `module/user`: Para asignar a Support

### Objetivo
Integrar sistema de productos externos donde Support gestiona mensajes iniciales y decide si crear ticket o resolver directamente.

### Arquitectura

```
Usuario Externo → Producto/Sistema → Message Queue → Support
                                                          ↓
                                            [Resolver] [Crear Ticket] [Descartar]
```

### Funcionalidades

#### 1. Gestión de Productos
**Componentes:**

**En `module/product/model/`:**
- `Product` (Entity) - Nueva
  - id, name, description, externalSystemId, apiKey, active

**En `module/product/enums/`:**
- `ProductCategory` (Enum) - Nueva

**En `module/product/repository/`:**
- `ProductRepository` - Nueva

#### 2. Sistema de Mensajes
**Componentes:**

**En `module/product/model/`:**
- `ExternalMessage` (Entity) - Nueva
  - id, productId, userId, subject, message, status, priority
- `MessageAction` (Entity) - Nueva (historial de acciones)

**En `module/product/enums/`:**
- `MessageStatus` (Enum) - Nueva
  - PENDING, IN_REVIEW, RESOLVED, TICKET_CREATED, DISCARDED

**En `module/product/repository/`:**
- `ExternalMessageRepository` - Nueva
- `MessageActionRepository` - Nueva

#### 3. Cola de Mensajes para Support
**Funcionalidades:**
- Lista de mensajes pendientes
- Filtros por producto, prioridad, fecha
- Asignación manual o automática
- Respuestas rápidas (templates)

#### 4. Acciones de Support
**Opciones:**
1. **Resolver directamente:**
   - Enviar respuesta al usuario
   - Marcar como resuelto
   
2. **Crear ticket:**
   - Convertir mensaje en ticket
   - Asignar a Developer
   - Notificar al usuario
   
3. **Descartar:**
   - Motivo de descarte
   - Opcional: enviar respuesta automática

### Implementación

#### Fase 1.3.1: Modelos y Base de Datos
- [ ] Crear módulo `module/product/` con estructura completa
- [ ] Crear `Product` entity en `module/product/model/`
- [ ] Crear `ProductCategory` enum en `module/product/enums/`
- [ ] Crear `ExternalMessage` entity en `module/product/model/`
- [ ] Crear `MessageStatus` enum en `module/product/enums/`
- [ ] Crear `MessageAction` entity en `module/product/model/`
- [ ] Crear repositorios en `module/product/repository/`

**Estructura `Product`:**
```java
@Entity
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    private String description;
    private String externalSystemId;
    private String apiKey; // Para autenticación
    
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
    
    private Boolean active;
    private String webhookUrl; // Para recibir mensajes
    private LocalDateTime createdAt;
}
```

**Estructura `ExternalMessage`:**
```java
@Entity
public class ExternalMessage {
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private Product product;
    
    private String externalUserId; // Usuario del sistema externo
    private String externalUserEmail;
    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    
    @Enumerated(EnumType.STRING)
    private TicketPriority suggestedPriority; // AI/reglas
    
    @ManyToOne
    private Support assignedTo;
    
    @ManyToOne
    private Ticket ticketCreated; // Si se convirtió en ticket
    
    private String resolution;
    private String discardReason;
    
    private LocalDateTime receivedAt;
    private LocalDateTime processedAt;
}
```

#### Fase 1.3.2: API de Integración Externa
- [ ] Crear `ProductController` en `module/product/controller/`
- [ ] Crear `ExternalMessageController` en `module/product/controller/`
- [ ] Implementar webhook receiver
- [ ] Autenticación por API Key
- [ ] Validación de payloads

**Endpoints Externos (para sistemas externos):**
```
POST /api/external/v1/messages (apiKey en header)
GET /api/external/v1/messages/{messageId}/status
```

**Payload ejemplo:**
```json
{
  "productId": "SYS-001",
  "userId": "ext-user-123",
  "userEmail": "user@example.com",
  "subject": "Error al procesar pago",
  "message": "Descripción detallada...",
  "priority": "HIGH"
}
```

#### Fase 1.3.3: Panel de Support
- [ ] Crear `SupportMessageController` en `module/product/controller/`
- [ ] Lista de mensajes pendientes
- [ ] Vista detallada de mensaje
- [ ] Acciones (resolver, crear ticket, descartar)
- [ ] Templates de respuesta

**Endpoints Internos:**
```
GET /api/v1/support/messages
GET /api/v1/support/messages/{id}
POST /api/v1/support/messages/{id}/resolve
POST /api/v1/support/messages/{id}/create-ticket
POST /api/v1/support/messages/{id}/discard
GET /api/v1/support/templates
```

#### Fase 1.3.4: Servicios
- [ ] Crear `ProductService` en `module/product/service/`
- [ ] Crear `ExternalMessageService` en `module/product/service/`
- [ ] Crear `MessageProcessingService` en `module/product/service/`
- [ ] Integrar con `TicketService` (ya existe en `module/ticket/service/`)
- [ ] Sistema de templates

#### Fase 1.3.5: Inteligencia Automática
- [ ] Clasificación automática de mensajes
- [ ] Sugerencia de prioridad
- [ ] Detección de duplicados
- [ ] Sugerencia de templates

**Tiempo estimado:** 3-4 semanas

---

## 🔔 Versión 1.4.0 - Sistema de Notificaciones y Motor de Eventos

> ⏭️ **FUTURA** - Iteración 05 (no en alcance de Iteración 01)

**Justificación:** Las notificaciones son una funcionalidad transversal que afecta a todos los módulos. Merece su propio módulo para centralizar toda la lógica de eventos y notificaciones.

**Estructura del nuevo módulo:**
```
module/notification/
├── controller/
│   ├── NotificationController.java
│   └── NotificationWebSocketController.java
├── dto/
│   ├── NotificationDto.java
│   └── NotificationPreferenceDto.java
├── enums/
│   ├── NotificationType.java
│   ├── NotificationChannel.java
│   └── NotificationPriority.java
├── model/
│   ├── Notification.java
│   ├── NotificationTemplate.java
│   └── UserNotificationPreference.java
├── repository/
│   ├── NotificationRepository.java
│   ├── NotificationTemplateRepository.java
│   └── UserNotificationPreferenceRepository.java
├── service/
│   ├── NotificationService.java
│   ├── NotificationTemplateService.java
│   └── NotificationEngine.java
└── events/
    ├── BaseEvent.java
    ├── EventPublisher.java
    └── listeners/
        ├── TicketEventListener.java
        ├── AuthEventListener.java
        └── MessageEventListener.java
```

**Integración con todos los módulos existentes:**
- `module/auth`: Eventos de autenticación
- `module/ticket`: Eventos de tickets
- `module/user`: Eventos de usuarios
- `module/product`: Eventos de mensajes externos

### Objetivo
Implementar sistema completo de notificaciones en tiempo real con WebSockets y motor de eventos para automatización.

### Arquitectura

```
Evento → EventPublisher → EventListener → NotificationEngine
                                               ↓
                                    [Email] [WebSocket] [Database]
```

### Funcionalidades

#### 1. Motor de Eventos
**Eventos del Sistema:**

**En `module/notification/events/`:**
- `BaseEvent` - Clase base abstracta
- `EventPublisher` - Servicio publicador

**Eventos específicos (en subcarpetas por módulo):**
- Auth: `UserRegisteredEvent`, `EmailVerifiedEvent`
- Ticket: `TicketCreatedEvent`, `TicketAssignedEvent`, `TicketPriorityChangedEvent`, `TicketStatusChangedEvent`
- Product: `MessageReceivedEvent`
- Otros: `SLAViolationEvent`, `RefundRequestedEvent`

**En `module/notification/events/listeners/`:**
- `TicketEventListener` - Escucha eventos de tickets
- `AuthEventListener` - Escucha eventos de autenticación
- `MessageEventListener` - Escucha eventos de mensajes

#### 2. Sistema de Notificaciones
**Tipos de Notificación:**
- In-App (WebSocket)
- Email
- Persistentes (base de datos)

**Componentes:**

**En `module/notification/model/`:**
- `Notification` (Entity) - Nueva
- `NotificationTemplate` (Entity) - Nueva
- `UserNotificationPreference` (Entity) - Nueva

**En `module/notification/enums/`:**
- `NotificationType` (Enum) - Nueva
- `NotificationChannel` (Enum) - Nueva
- `NotificationPriority` (Enum) - Nueva

#### 3. WebSocket en Tiempo Real
**Funcionalidades:**
- Conexión por usuario
- Notificaciones instantáneas
- Indicador de no leídas
- Historial de notificaciones

**Componentes:**

**En `module/notification/controller/`:**
- `NotificationWebSocketController` - Nueva

**En `shared/config/`:**
- `WebSocketConfig` - EXTENDER (ya existe)

#### 4. Preferencias de Usuario
**Configuración:**
- Habilitar/deshabilitar canales
- Frecuencia de emails (inmediato, resumen diario)
- Tipos de eventos a recibir

### Implementación

#### Fase 1.4.1: Motor de Eventos
- [ ] Crear módulo `module/notification/` con estructura completa
- [ ] Crear `BaseEvent` en `module/notification/events/`
- [ ] Implementar `EventPublisher` en `module/notification/events/`
- [ ] Crear eventos específicos para cada módulo
- [ ] Crear listeners en `module/notification/events/listeners/`
- [ ] Testing de eventos

**Estructura Base:**
```java
// Base Event
public abstract class BaseEvent extends ApplicationEvent {
    private final LocalDateTime occurredAt;
    private final String eventType;
    private final Map<String, Object> metadata;
    
    // constructors, getters
}

// Ejemplo de evento específico
public class TicketCreatedEvent extends BaseEvent {
    private final Ticket ticket;
    private final User creator;
    
    public TicketCreatedEvent(Object source, Ticket ticket, User creator) {
        super(source);
        this.ticket = ticket;
        this.creator = creator;
    }
}

// Publisher
@Service
public class EventPublisher {
    @Autowired
    private ApplicationEventPublisher publisher;
    
    public void publishTicketCreated(Ticket ticket, User creator) {
        TicketCreatedEvent event = new TicketCreatedEvent(this, ticket, creator);
        publisher.publishEvent(event);
    }
}

// Listener
@Component
public class TicketEventListener {
    @EventListener
    @Async
    public void handleTicketCreated(TicketCreatedEvent event) {
        // Crear notificaciones
        // Enviar emails
        // Actualizar estadísticas
    }
}
```

#### Fase 1.4.2: Modelos de Notificación
- [ ] Crear `Notification` entity en `module/notification/model/`
- [ ] Crear `NotificationTemplate` entity en `module/notification/model/`
- [ ] Crear `UserNotificationPreference` entity en `module/notification/model/`
- [ ] Crear enums en `module/notification/enums/`
- [ ] Crear repositorios en `module/notification/repository/`

**Estructura `Notification`:**
```java
@Entity
public class Notification {
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private User recipient;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    private NotificationPriority priority;
    
    private Boolean read;
    private LocalDateTime readAt;
    
    private String relatedEntityType; // "TICKET", "MESSAGE", etc.
    private Long relatedEntityId;
    
    private String actionUrl; // Link para redirección
    
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
```

#### Fase 1.4.3: WebSocket
- [ ] Actualizar `WebSocketConfig` en `shared/config/` (ya existe)
- [ ] Crear `NotificationWebSocketController` en `module/notification/controller/`
- [ ] Implementar handshake con JWT
- [ ] Implementar envío de notificaciones
- [ ] Cliente de prueba

**Configuración WebSocket:**
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

**Endpoints WebSocket:**
```
/topic/notifications - Notificaciones globales
/user/queue/notifications - Notificaciones personales
/app/notifications/mark-read - Marcar como leída
```

#### Fase 1.4.4: Servicios de Notificación
- [ ] Crear `NotificationService` en `module/notification/service/`
- [ ] Crear `NotificationTemplateService` en `module/notification/service/`
- [ ] Crear `NotificationEngine` en `module/notification/service/`
- [ ] Integrar con EventListeners

**Componentes del Engine:**
```java
@Service
public class NotificationEngine {
    
    public void processEvent(BaseEvent event) {
        // 1. Determinar destinatarios
        List<User> recipients = determineRecipients(event);
        
        // 2. Obtener template
        NotificationTemplate template = getTemplate(event.getEventType());
        
        // 3. Generar contenido
        String content = renderTemplate(template, event);
        
        // 4. Verificar preferencias
        for (User user : recipients) {
            UserNotificationPreference pref = getPreferences(user);
            
            // 5. Enviar por canales activos
            if (pref.isInAppEnabled()) {
                sendInApp(user, content);
            }
            if (pref.isEmailEnabled()) {
                sendEmail(user, content);
            }
        }
        
        // 6. Guardar en BD
        saveNotification(recipients, content, event);
    }
}
```

#### Fase 1.4.5: Templates
- [ ] Crear sistema de templates
- [ ] Templates por tipo de evento
- [ ] Variables dinámicas
- [ ] Preview de templates

**Ejemplo Template:**
```java
@Entity
public class NotificationTemplate {
    @Id
    @GeneratedValue
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    
    private String titleTemplate;
    private String messageTemplate;
    private String emailSubjectTemplate;
    private String emailBodyTemplate;
    
    // Variables disponibles: ${ticket.id}, ${user.name}, etc.
}
```

#### Fase 1.4.6: API y Controllers
- [ ] Crear `NotificationController` en `module/notification/controller/`
- [ ] Endpoints CRUD
- [ ] Marcar como leída
- [ ] Obtener no leídas
- [ ] Preferencias de usuario

**Endpoints:**
```
GET /api/v1/notifications
GET /api/v1/notifications/unread
PUT /api/v1/notifications/{id}/read
PUT /api/v1/notifications/read-all
DELETE /api/v1/notifications/{id}

GET /api/v1/notifications/preferences
PUT /api/v1/notifications/preferences

# Admin
GET /api/v1/admin/notification-templates
PUT /api/v1/admin/notification-templates/{id}
POST /api/v1/admin/notifications/broadcast
```

#### Fase 1.4.7: Integraciones
- [ ] Integrar eventos en todos los módulos
- [ ] Actualizar `TicketService` (en `module/ticket/service/`)
- [ ] Actualizar `AuthService` (en `module/auth/service/`)
- [ ] Actualizar `ExternalMessageService` (en `module/product/service/`)
- [ ] Testing end-to-end

**Tiempo estimado:** 3-4 semanas

---

## 📋 Plan de Implementación General

### Timeline Propuesto

```
Semana 1-3:   v1.1.0 - Autenticación Segura
Semana 4-5:   v1.2.0 - Sistema de Prioridades
Semana 6-9:   v1.3.0 - Productos/Sistemas Externos
Semana 10-13: v1.4.0 - Notificaciones y Eventos
```

### Consideraciones Técnicas

#### Dependencias Adicionales
```xml
<!-- Email -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- WebSocket -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<!-- Template Engine -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Redis (opcional para notificaciones distribuidas) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Scheduler -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

#### Variables de Entorno Necesarias
```properties
# Email
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
EMAIL_FROM=noreply@tickets.com

# Security
VERIFICATION_CODE_EXPIRY_MINUTES=15
PASSWORD_RESET_TOKEN_EXPIRY_HOURS=24
MAX_VERIFICATION_ATTEMPTS=3

# WebSocket
WEBSOCKET_ALLOWED_ORIGINS=http://localhost:3000,https://yourdomain.com

# External Systems
EXTERNAL_API_SECRET=your-secret-key
```

### Testing Strategy

#### Para cada versión:
1. **Unit Tests**
   - Servicios
   - Utilidades
   - Validaciones

2. **Integration Tests**
   - Controllers
   - Repositorios
   - WebSocket connections

3. **E2E Tests**
   - Flujos completos
   - Casos de uso reales

### Deployment Strategy

#### Por cada versión:
1. Desarrollo en rama `feature/vX.X.X`
2. Testing en rama `develop`
3. Merge a `main` cuando esté listo
4. Tag de versión
5. Deploy a staging
6. Testing en staging
7. Deploy a producción

### Documentación

#### Para cada versión actualizar:
- [ ] README.md
- [ ] CHANGELOG.md
- [ ] endpoints.md
- [ ] Swagger/OpenAPI specs
- [ ] Diagramas de arquitectura

---

## 🎯 Próximos Pasos Inmediatos

### Para Empezar con v1.1.0:

1. **Crear rama de desarrollo:**
   ```bash
   git checkout -b feature/v1.1.0-auth-security
   ```

2. **Configurar dependencias:**
   - Agregar spring-boot-starter-mail a pom.xml
   - Configurar application.properties

3. **Crear estructura base:**
   ```
   module/auth/          (EXISTENTE - Extender)
   ├── model/
   │   ├── VerificationCode.java      (NUEVA)
   │   └── PasswordResetToken.java    (NUEVA)
   ├── repository/
   │   ├── VerificationCodeRepository.java      (NUEVA)
   │   └── PasswordResetTokenRepository.java    (NUEVA)
   ├── service/
   │   ├── EmailService.java           (NUEVA)
   │   └── VerificationService.java    (NUEVA)
   └── dto/
       ├── VerificationCodeDto.java    (NUEVA)
       └── SetPasswordRequestDto.java  (NUEVA)
   ```

4. **Implementar EmailService básico**

5. **Testing inicial**

---

## 📝 Notas Importantes

### Seguridad
- Nunca almacenar códigos de verificación en texto plano
- Usar BCrypt para tokens
- Implementar rate limiting en todos los endpoints sensibles
- Logs de auditoría para acciones críticas

### Performance
- Usar @Async para envío de emails
- Cache para templates
- Índices en base de datos para queries frecuentes
- Paginación en todas las listas

### Escalabilidad
- Considerar Redis para notificaciones en múltiples instancias
- Queue system (RabbitMQ/Kafka) para mensajes externos
- CDN para assets estáticos
- Load balancing preparado

### Monitoring
- Logs estructurados
- Métricas de rendimiento
- Alertas para SLA violations
- Health checks

---

**Última actualización:** 2026-02-01
**Versión actual:** 1.0.0
**Próxima versión:** 1.1.0
