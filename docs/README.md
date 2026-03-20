# docs - indice de iteraciones

## Objetivo

Centralizar en un solo lugar el resumen de iteraciones, su foco funcional y su estado actual de avance.

## Estado Global

| Iteracion    | Enfoque Principal                        | Estado Documental | Estado De Ejecucion                          | Detalle                                                                                                                          |
| ------------ | ---------------------------------------- | ----------------- | -------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| Iteracion 01 | Migracion de stack y arquitectura MVP    | Completa          | Pendiente de ejecucion (checklist en blanco) | [iteracion-01-migracion-stack-y-arquitectura/README.md](iteracion-01-migracion-stack-y-arquitectura/README.md)                   |
| Iteracion 02 | Hardening auth, calidad y testing base   | Completa          | Pendiente de ejecucion (checklist en blanco) | [iteracion-02-hardening-auth-calidad-y-testing/README.md](iteracion-02-hardening-auth-calidad-y-testing/README.md)               |
| Iteracion 03 | Prioridades ticket, SLA y operacion      | Completa          | Pendiente de ejecucion (checklist en blanco) | [iteracion-03-ticket-prioridades-sla-y-operacion/README.md](iteracion-03-ticket-prioridades-sla-y-operacion/README.md)           |
| Iteracion 04 | Product e integraciones externas         | Completa          | Pendiente de ejecucion (checklist en blanco) | [iteracion-04-product-integraciones-externas/README.md](iteracion-04-product-integraciones-externas/README.md)                   |
| Iteracion 05 | Eventos, notificaciones y observabilidad | Completa          | Pendiente de ejecucion (checklist en blanco) | [iteracion-05-eventos-notificaciones-y-observabilidad/README.md](iteracion-05-eventos-notificaciones-y-observabilidad/README.md) |

## Detalle Por Iteracion

### Iteracion 01 - migracion-stack-y-arquitectura

- Proposito: cerrar el MVP modular base (PostgreSQL, Supabase Auth, account/ticket desacoplados y limites de arquitectura).
- Estructura: 8 fases (0 a 7), desde baseline tecnico hasta cutover y cierre documental.
- Estado actual: documentacion completa de iteracion, fases y tareas; ejecucion tecnica pendiente.
- Referencia: [iteracion-01-migracion-stack-y-arquitectura/README.md](iteracion-01-migracion-stack-y-arquitectura/README.md)

### Iteracion 02 - hardening-auth-calidad-y-testing

- Proposito: fortalecer seguridad de autenticacion y dejar base de calidad automatizada post-MVP.
- Estructura: 3 fases (hardening auth, testing base, observabilidad/readiness).
- Estado actual: documentacion completa y naming de tareas normalizado por fase (1.x, 2.x, 3.x); ejecucion pendiente.
- Referencia: [iteracion-02-hardening-auth-calidad-y-testing/README.md](iteracion-02-hardening-auth-calidad-y-testing/README.md)

### Iteracion 03 - ticket-prioridades-sla-y-operacion

- Proposito: incorporar prioridades y SLA en ticket con reglas operativas y regresion funcional.
- Estructura: 3 fases (modelo SLA, operacion/escalamiento, testing funcional).
- Estado actual: documentacion completa y naming por fase consistente; ejecucion pendiente.
- Referencia: [iteracion-03-ticket-prioridades-sla-y-operacion/README.md](iteracion-03-ticket-prioridades-sla-y-operacion/README.md)

### Iteracion 04 - product-integraciones-externas

- Proposito: crear module/product para ingreso de mensajes externos y conversion controlada a ticket.
- Estructura: 3 fases (core/API externa, bandeja support, resiliencia/integracion).
- Estado actual: documentacion completa de fases y 9 tareas (1.1 a 3.3); ejecucion pendiente.
- Referencia: [iteracion-04-product-integraciones-externas/README.md](iteracion-04-product-integraciones-externas/README.md)

### Iteracion 05 - eventos-notificaciones-y-observabilidad

- Proposito: habilitar backbone de eventos modular y capacidades de notificacion multi-canal.
- Estructura: 3 fases (eventos base, notificaciones, pruebas E2E y cierre operacional).
- Estado actual: documentacion completa de fases y 9 tareas (1.1 a 3.3); ejecucion pendiente.
- Referencia: [iteracion-05-eventos-notificaciones-y-observabilidad/README.md](iteracion-05-eventos-notificaciones-y-observabilidad/README.md)

## Roadmap Relacionado

- Vista consolidada post-MVP: [roadmap-post-mvp-1x/README.md](roadmap-post-mvp-1x/README.md)

## Fecha De Actualizacion

- 2026-03-20
