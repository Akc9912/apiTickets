# iteracion-05-eventos-notificaciones-y-observabilidad

## Objetivo De La Iteracion

Introducir backbone de eventos modular y modulo notification para soportar notificaciones in-app/email/realtime con observabilidad operativa.

## Alcance

- eventos de dominio e integracion entre modulos
- modulo notification con templates y preferencias
- canal in-app, email y websocket en alcance controlado
- testing E2E cross-modulo y readiness operacional

## Fuera De Alcance

- event streaming avanzado con Kafka (solo si existe presion real)
- distribucion en microservicios
- multi-region y escala global

## Estado De Fases

- [ ] Fase 1 - backbone eventos modulares: [README de fase](fase-1-backbone-eventos-modulares/README.md)
- [ ] Fase 2 - notificaciones inapp email realtime: [README de fase](fase-2-notificaciones-inapp-email-realtime/README.md)
- [ ] Fase 3 - testing e2e operacion y cierre: [README de fase](fase-3-testing-e2e-operacion-y-cierre/README.md)

## Desglose Por Fase

| Fase                                         | Rango de tareas | README de fase                                                                                               | Estado |
| -------------------------------------------- | --------------- | ------------------------------------------------------------------------------------------------------------ | ------ |
| Fase 1 - backbone eventos modulares          | 1.1 a 1.3       | [fase-1-backbone-eventos-modulares/README.md](fase-1-backbone-eventos-modulares/README.md)                   | [ ]    |
| Fase 2 - notificaciones inapp email realtime | 2.1 a 2.3       | [fase-2-notificaciones-inapp-email-realtime/README.md](fase-2-notificaciones-inapp-email-realtime/README.md) | [ ]    |
| Fase 3 - testing e2e operacion y cierre      | 3.1 a 3.3       | [fase-3-testing-e2e-operacion-y-cierre/README.md](fase-3-testing-e2e-operacion-y-cierre/README.md)           | [ ]    |

## Riesgos Y Mitigaciones

- riesgo: tormenta de eventos entre modulos -> mitigacion: politicas de publicacion y idempotencia
- riesgo: ruido de notificaciones al usuario -> mitigacion: preferencias y throttling por canal
- riesgo: baja trazabilidad operativa -> mitigacion: logs estructurados + metricas + correlacion por evento

## Criterio De Cierre De Iteracion

- [ ] backbone de eventos funcionando en modulos objetivo
- [ ] notificaciones operativas en canales definidos
- [ ] suite E2E cross-modulo en verde
- [ ] documentacion y evidencia tecnica actualizadas
