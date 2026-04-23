# Skill: ImplementPlan

## Descripción
Basado en la auditoría del sistema existente (resultado de QuickAudit), genera un plan de implementación detallado o roadmap para añadir una funcionalidad específica, respetando la arquitectura, stack y patrones actuales.

## Activación
Ejecuta esta skill cuando el usuario pida:
- "Quiero implementar [funcionalidad] en este sistema"
- "Dame un plan para añadir [feature]"
- "Cómo implementaría [X] respetando la arquitectura actual"
- "Roadmap para agregar [Y] al proyecto"
- "Guía de implementación de [Z] en este stack"

## Dependencia
**Requiere el resultado de `QuickAudit`** guardado en `/docs/audit/audit-XXX.md`.

Si no existe ningún archivo de auditoría:
1. Informar al usuario que debe ejecutar QuickAudit primero
2. Ofrecer ejecutar QuickAudit automáticamente

Si existen múltiples auditorías, usar la **más reciente** (mayor número).

## Entrada requerida
1. **Archivo de auditoría** (`/docs/audit/audit-XXX.md`) - se lee automáticamente
2. **Funcionalidad a implementar** (descripción del usuario)
3. **Restricciones opcionales** (tiempo, recursos, calidad, seguridad)

## Salida
1. **Plan mostrado en el chat** (Markdown estructurado)
2. **Archivo guardado** en `/docs/plan/plan-XXX.md` (numeración incremental)

Contenido del plan:
- Enfoque recomendado (por qué este camino)
- Desglose por fases (días/sprints)
- Tareas concretas por capa/componente
- Ejemplos de código adaptados al stack actual
- Consideraciones de integración
- Riesgos y mitigaciones
- Criterios de éxito

## Instrucciones

### Paso 1: Leer auditoría más reciente

1. **Verificar existencia de auditorías:**
   ```bash
   ls docs/audit/
   ```

2. **Identificar archivo más reciente:**
   - Buscar el mayor número: `audit-001.md`, `audit-002.md`, etc.
   - Ejemplo: si existe `audit-003.md`, usar ese

3. **Leer contenido:**
   ```bash
   cat docs/audit/audit-{NNN}.md
   ```

4. **Extraer información clave:**
   - Stack tecnológico (lenguaje, framework, BD)
   - Patrón de arquitectura
   - Estructura de directorios
   - Recomendaciones previas

5. **Si no existe auditoría:**
   > ⚠️ No se encontró auditoría previa. Ejecuta QuickAudit primero:
   > 
   > ```bash
   > # Crear docs/audit/ y generar audit-001.md
   > ```

### Paso 2: Analizar funcionalidad y contexto

Pregunta al usuario para clarificar:
- ¿Qué debe hacer exactamente la funcionalidad? (inputs, outputs, comportamiento)
- ¿Integración con sistemas externos?
- ¿Requisitos de rendimiento/seguridad?
- ¿Plazo estimado? (inmediato/1 semana/1 mes/flexible)

### Paso 3: Mapear contra arquitectura actual

Usa la información leída del archivo de auditoría para determinar:

| Aspecto | Qué respetar |
|---------|---------------|
| Tipo de proyecto | Backend/ Frontend/ Fullstack/ Microservicio |
| Patrón actual | Monolito/ Modular/ Hexagonal/ DDD |
| Lenguaje/framework | Node/Python/Java/Go + framework específico |
| Base de datos | PostgreSQL/MySQL/MongoDB/Redis |
| Infraestructura | Docker/K8s/Serverless/VM |
| Estilo de API | REST/GraphQL/gRPC |

### Paso 4: Generar el roadmap

Crea un plan con esta estructura exacta:

---

**🗺️ Roadmap: `{{funcionalidad}}`**  
*Basado en auditoría del {{fecha}}*  
*Stack actual: {{resumen del stack}}*

**🎯 Objetivo**  
{{descripción clara de qué implementar y por qué}}

**🧠 Enfoque recomendado**  
**Estrategia:** {{por qué este enfoque vs alternativas}}

**Por qué este camino:**
- Respeta el patrón {{actual}} existente
- Reutiliza {{componentes existentes}}
- Minimiza cambios estructurales
- Escala con la arquitectura actual

**📅 Fases de implementación**

**Fase 1: Preparación (Día 1)**  
**Duración:** {{X horas/días}}
- [ ] Crear branch `feature/{{nombre}}`
- [ ] Revisar dependencias necesarias ({{librerías}})
- [ ] Definir contratos/interfaces ({{link a diseño}})
- [ ] Configurar entorno de pruebas

**Fase 2: Backend/Core (Días 2-{{N}})**

*Capa de Modelo/Datos*
```{{lenguaje}}
// Ejemplo adaptado al stack actual
{{código ejemplo específico}}
```

**Tareas:**
- Crear modelo/entidad {{nombre}}
- Implementar migración de BD (si aplica)
- Añadir repositorio/DAO (siguiendo patrón {{actual}})
- Escribir tests unitarios

*Capa de Lógica/Negocio*
```{{lenguaje}}
// Ejemplo de servicio/use case
{{código ejemplo}}
```

**Tareas:**
- Implementar servicio/use case
- Añadir validaciones
- Manejar errores (estilo {{framework}})
- Tests de integración

*Capa de API/Controllers*
```{{lenguaje}}
// Endpoint ejemplo (REST/GraphQL)
{{código ejemplo}}
```

**Tareas:**
- Añadir endpoint/mutation/query
- Implementar autenticación/autorización
- Documentar con Swagger/GraphQL schema
- Tests de API

**Fase 3: Frontend (si aplica) (Días {{X-Y}})**  
*Basado en framework detectado: {{React/Vue/Angular/etc}}*

```{{lenguaje}}
// Componente ejemplo
{{código ejemplo específico del stack}}
```

**Tareas:**
- Crear componente/página
- Añadir servicio/API client
- Manejar estados ({{Redux/Pinia/etc}})
- Tests E2E/componente

**Fase 4: Integración y despliegue (Días {{Y-Z}})**  
*Infraestructura detectada: {{Docker/K8s/Serverless}}*

```dockerfile
# Dockerfile adaptado (si aplica)
{{ejemplo}}
```

**Tareas:**
- Actualizar Dockerfile/docker-compose
- Configurar variables de entorno
- Añadir health checks
- Actualizar CI/CD ({{GitHub Actions/GitLab CI}})
- Plan de rollback

**Fase 5: Testing y documentación (Día {{Z}})**
- Tests de carga/rendimiento
- Tests de seguridad básicos
- Actualizar README/documentación
- Ejecutar pruebas de regresión
- Preparar changelog

**📝 Ejemplos específicos por stack**  
*Basado en el stack real detectado, proporciona ejemplos concretos:*

*Si es Node.js + Express:*
```javascript
// routes/{{feature}}.js
router.post('/api/{{resource}}', auth, validate, controller.create);

// services/{{feature}}Service.js
class FeatureService {
  async create(data) { /* implementación */ }
}
```

*Si es Python + Django:*
```python
# models.py
class FeatureModel(models.Model): pass

# views.py
@api_view(['POST'])
def create_feature(request): pass
```

*Si es Java + Spring Boot:*
```java
@RestController
@RequestMapping("/api/feature")
public class FeatureController {
    @PostMapping
    public ResponseEntity<?> create(@RequestBody FeatureDto dto) { }
}
```

*Si es Go + Gin:*
```go
func (h *Handler) CreateFeature(c *gin.Context) {
    var req CreateRequest
    c.BindJSON(&req)
}
```

**⚠️ Riesgos y mitigaciones**
| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|-------------|---------|------------|
| {{riesgo 1}} | Alta/Media/Baja | Alto/Medio/Bajo | {{acción}} |
| {{riesgo 2}} | ... | ... | ... |
| {{riesgo 3}} | ... | ... | ... |

**✅ Criterios de éxito (Definition of Done)**
- [ ] La funcionalidad funciona en entorno local
- [ ] Tests pasan (unitarios + integración)
- [ ] Documentación actualizada
- [ ] Code review aprobado
- [ ] Desplegado en staging
- [ ] Validado por QA/PO
- [ ] Métricas de rendimiento aceptables ({{latencia < Xms}})
- [ ] Sin deuda técnica crítica identificada

**📊 Estimación de esfuerzo**
| Fase | Tiempo estimado | Dependencias |
|------|-----------------|--------------|
| Preparación | {{X}}h | - |
| Backend | {{Y}}h | Preparación |
| Frontend | {{Z}}h | Backend |
| Integración | {{W}}h | Backend + Frontend |
| Testing | {{V}}h | Todo lo anterior |
| **Total** | **{{total}} días** | - |

**🔄 Alternativas consideradas**

*Alternativa 1: {{nombre}}*
- Pros: {{ventajas}}
- Contras: {{desventajas}}
- Por qué no la elegimos: {{razón}}

*Alternativa 2: {{nombre}}*
- Pros: {{ventajas}}
- Contras: {{desventajas}}
- Por qué no la elegimos: {{razón}}

**📚 Recursos útiles para este stack**
- Documentación oficial: {{link}}
- Librería recomendada: {{nombre}} ({{por qué}})
- Patrón de referencia: {{link}}
- Ejemplo similar en código base: {{ruta/archivo}}

**🚀 Próximos pasos inmediatos**
1. Confirmar este plan con el equipo/stakeholders
2. Crear issues/tareas en el gestor de proyectos
3. Configurar el entorno de desarrollo
4. Ejecutar la Fase 1

---

### Paso 5: Adaptar ejemplos al stack real

Usa la tabla de mapeo para generar ejemplos auténticos:

| Stack detectado | Patrón de controlador | Patrón de servicio | ORM típico |
|----------------|----------------------|--------------------|-------------|
| Node + Express | router.post() | class Service | Prisma/TypeORM |
| Node + NestJS | @Post() decorator | @Injectable() | TypeORM/Mongoose |
| Python + Django | api_view decorator | class Service | Django ORM |
| Python + FastAPI | @app.post | async def | SQLAlchemy |
| Java + Spring | @PostMapping | @Service | JPA/Hibernate |
| Go + Gin | c.BindJSON() | struct methods | GORM |
| PHP + Laravel | Route::post | class Service | Eloquent |

### Paso 6: Guardar plan en archivo

1. **Crear directorio si no existe:**
   ```bash
   mkdir -p docs/plan
   ```

2. **Determinar número secuencial:**
   - Listar archivos existentes: `ls docs/plan/`
   - Buscar el mayor número: `plan-001.md`, `plan-002.md`, etc.
   - Siguiente número = último + 1 (con 3 dígitos)

3. **Generar nombre de archivo:**
   - Formato: `docs/plan/plan-{NNN}.md`
   - Ejemplo: `docs/plan/plan-001.md`

4. **Incluir referencia a auditoría base:**
   - Agregar en el encabezado: `Basado en: docs/audit/audit-{NNN}.md`

5. **Guardar contenido:** Guardar el roadmap completo en el archivo

6. **Informar al usuario:**
   > ✅ Plan guardado en: `docs/plan/plan-{NNN}.md`

### Paso 7: Ejemplo concreto de uso

**Usuario:** "Basado en la auditoría, quiero implementar un sistema de notificaciones push"

**Asistente:** (usando resultado de QuickAudit que detectó Node.js + Express + PostgreSQL + Redis)

---

**🗺️ Roadmap: Sistema de notificaciones push**  
*Basado en auditoría del 2025-03-20*  
*Stack actual: Node.js v20 + Express + PostgreSQL + Redis*

**🎯 Objetivo**  
Enviar notificaciones push a usuarios (web push y mobile push)

**🧠 Enfoque recomendado**  
**Estrategia:** Cola asíncrona con Bull (Redis) + Web Push API

**Por qué:**
- Respeta el patrón actual (servicios + repositorios)
- Reutiliza Redis existente (cache)
- No bloquea peticiones principales

**📅 Fases (Total: 5 días)**

**Fase 1: Preparación (0.5 días)**
- `npm install web-push bull`
- Crear branch `feature/notifications`

**Fase 2: Backend (2 días)**

*Modelo:*
```javascript
// models/Notification.js
const NotificationSchema = new Schema({
  userId: String,
  type: String,
  payload: Object,
  sentAt: Date
})
```

*Servicio:*
```javascript
// services/PushService.js
class PushService {
  async send(userId, payload) {
    await queue.add('push', { userId, payload })
  }
}
```

*API:*
```javascript
// routes/notifications.js
router.post('/api/notifications/subscribe', auth, controller.subscribe)
```

**Fase 3: Worker (1 día)**
```javascript
// workers/pushWorker.js
const worker = new Worker('push', async job => {
  await webpush.sendNotification(...)
})
```

**Fase 4: Integración (1 día)**
- Actualizar docker-compose (Redis ya existe)
- Añadir variable VAPID_KEYS
- Health check del worker

**Fase 5: Testing (0.5 días)**
- Tests unitarios del servicio
- Tests de cola fallback

**⚠️ Riesgos principales:**
| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|-------------|---------|------------|
| Push API no soportada en navegadores viejos | Media | Medio | Fallback a email |
| Redis se cae | Baja | Alto | Cola en memoria temporal |

**✅ Criterios de éxito:**
- [ ] Notificación entregada en <500ms
- [ ] 99.9% de workers activos
- [ ] Logs de notificaciones fallidas

---

> ¿Empiezo con la Fase 1 o necesitas ajustar algo?

## Estructura de archivos de planificación

```
docs/
├── audit/               # Auditorías generadas por QuickAudit
│   ├── audit-001.md
│   ├── audit-002.md
│   └── ...
└── plan/                # Planes generados por ImplementPlan
    ├── plan-001.md     # Referencia: audit-001.md
    ├── plan-002.md     # Referencia: audit-002.md
    └── ...
```

**Notas:**
- Cada plan debe referenciar la auditoría base en su encabezado
- Numeración independiente (plan-001 puede basarse en audit-003)
- Mantener histórico para trazabilidad de decisiones

## Consejos de implementación

1. **Sé específico**: No des consejos genéricos, usa el stack real del proyecto
2. **Respeta convenciones**: Si el proyecto usa `camelCase` o `snake_case`, mantenlo
3. **Reutiliza existente**: Si hay un logger, úsalo; si hay middleware de auth, aplícalo
4. **Tamaño de tareas**: Cada tarea debe ser de 1-4 horas máximo
5. **Incluye ejemplos reales**: El código debe ser copiable y ejecutable

## Personalización por tipo de funcionalidad

| Si pide... | Enfoque principal |
|------------|-------------------|
| Autenticación | JWT/Sessions, middleware, rotación de tokens |
| Pagos | Webhooks, idempotencia, logging transaccional |
| Reportes | Procesamiento async, caché, rate limiting |
| WebSockets | Redis adapter, sticky sessions, reconexión |
| Búsqueda | Índices, Elasticsearch (si aplica), paginación |
| Subida archivos | Multipart, validación, CDN, thumbnails |

## Formato ultra-rápido (si pide "plan rápido")

Si el usuario tiene prisa, simplifica a:

```markdown
## 🚀 Plan rápido: {{funcionalidad}}

**Día 1:** {{tareas principales}}
**Día 2:** {{backend + API}}
**Día 3:** {{frontend + tests}}
**Día 4:** {{deploy + docs}}

**Ejemplo clave:**
```código mínimo funcional```

**Riesgo principal:** {{qué puede salir mal}}

Esta skill te permite pasar de "sé cómo está construido" a "sé cómo extenderlo" respetando todo lo existente. ¿Necesitas ejemplos para algún tipo específico de funcionalidad?