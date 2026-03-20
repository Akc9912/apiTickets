# roadmap-post-mvp-1x

## Objetivo

Definir el roadmap posterior al MVP 1.0.0 de forma incremental, priorizando valor funcional, calidad tecnica y testing continuo.

## Criterio De Reagrupacion

- se usa next_steps como referencia funcional, no como secuencia cerrada de versiones
- se fusiona hardening auth con calidad/testing en una sola iteracion para estabilizar la base post-MVP
- se mantiene ticket SLA separado de integraciones externas por riesgo operativo
- se separa integraciones externas de notificaciones/eventos por complejidad transversal

## Iteraciones Post-MVP Definidas

1. [iteracion-02-hardening-auth-calidad-y-testing](../iteracion-02-hardening-auth-calidad-y-testing/README.md)
2. [iteracion-03-ticket-prioridades-sla-y-operacion](../iteracion-03-ticket-prioridades-sla-y-operacion/README.md)
3. [iteracion-04-product-integraciones-externas](../iteracion-04-product-integraciones-externas/README.md)
4. [iteracion-05-eventos-notificaciones-y-observabilidad](../iteracion-05-eventos-notificaciones-y-observabilidad/README.md)

## Estrategia De Testing Post-MVP

- iteracion 02: base de testing (piramide, contratos, regresion, gates de CI)
- iteracion 03: testing funcional y de reglas de negocio en ticket/SLA
- iteracion 04: testing de integracion externa, idempotencia y resiliencia
- iteracion 05: testing E2E cross-modulo, performance basica y readiness operacional

## Regla Operativa

Cada iteracion se implementa por fases y tareas verificables en docs, con evidencia de cierre tecnica y funcional.

## Referencias

- [engineering/manifesto.md](../../engineering/manifesto.md)
- [engineering/architecture/backend-architecture.md](../../engineering/architecture/backend-architecture.md)
- [engineering/stack/java-backend-stack.md](../../engineering/stack/java-backend-stack.md)
- [next_steps.md](../../next_steps.md)
