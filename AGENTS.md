# Skills disponibles en el proyecto

## 1. ImplementPlan

Basado en la auditoría del sistema existente (resultado de QuickAudit), genera un plan de implementación detallado o roadmap para añadir una funcionalidad específica, respetando la arquitectura, stack y patrones actuales.

- **Activación:** Cuando se solicita un plan para implementar una funcionalidad.
- **Dependencia:** Requiere el resultado de QuickAudit.
- **Salida:** Plan detallado en Markdown y archivo en `/docs/plan/plan-XXX.md`.

## 2. QuickAudit

Auditoría rápida y no invasiva (solo lectura) para entender cómo está construido un sistema: arquitectura, tecnologías, patrones de implementación y estado general.

- **Activación:** Cuando se solicita una auditoría rápida o inventario tecnológico.
- **Salida:** Reporte en Markdown y archivo en `/docs/audit/audit-XXX.md`.

## 3. tech-adoption-planning

Evalúa y planifica la adopción de nuevas tecnologías (cache, mensajería, búsqueda, auth, observabilidad, etc.) con un backlog de implementación por fases, riesgos y tareas priorizadas.

- **Activación:** Cuando se evalúa o planifica la adopción de una tecnología nueva.
- **Salida:** Documento de planificación y recomendaciones de tickets.

## 4. TodoTriageBackend

Convierte comentarios TODO en backlog priorizado y accionable para proyectos Java/Spring Boot backend. También genera tareas sprint-ready a partir de planes de implementación.

- **Activación:** Al revisar TODOs en código backend o al convertir planes en tareas.
- **Salida:** Lista de tareas priorizadas y listas para ticketing.
