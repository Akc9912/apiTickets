# iteracion-03-ticket-prioridades-sla-y-operacion

## Objetivo De La Iteracion

Implementar prioridades, reglas SLA y flujo operativo de ticket para mejorar respuesta de soporte con control de riesgo funcional.

## Alcance

- prioridades de ticket y configuracion SLA
- motor de reglas de escalamiento operativo
- bandeja y acciones operativas alineadas a prioridad
- testing funcional/regresion especifico de ticket

## Fuera De Alcance

- integraciones externas de product
- notificaciones en tiempo real y eventos globales
- mensajeria distribuida (Rabbit/Kafka)

## Estado De Fases

- [ ] Fase 1 - prioridades y reglas SLA: [README de fase](fase-1-prioridades-y-reglas-sla/README.md)
- [ ] Fase 2 - asignacion escalamiento y operacion: [README de fase](fase-2-asignacion-escalamiento-y-operacion/README.md)
- [ ] Fase 3 - testing funcional y regresion ticket: [README de fase](fase-3-testing-funcional-y-regresion-ticket/README.md)

## Desglose Por Fase

| Fase                                          | Rango de tareas | README de fase                                                                                                 | Estado |
| --------------------------------------------- | --------------- | -------------------------------------------------------------------------------------------------------------- | ------ |
| Fase 1 - prioridades y reglas SLA             | 1.1 a 1.3       | [fase-1-prioridades-y-reglas-sla/README.md](fase-1-prioridades-y-reglas-sla/README.md)                         | [ ]    |
| Fase 2 - asignacion escalamiento y operacion  | 2.1 a 2.3       | [fase-2-asignacion-escalamiento-y-operacion/README.md](fase-2-asignacion-escalamiento-y-operacion/README.md)   | [ ]    |
| Fase 3 - testing funcional y regresion ticket | 3.1 a 3.3       | [fase-3-testing-funcional-y-regresion-ticket/README.md](fase-3-testing-funcional-y-regresion-ticket/README.md) | [ ]    |

## Riesgos Y Mitigaciones

- riesgo: reglas SLA inconsistentes -> mitigacion: tabla unica de configuracion y tests de reglas
- riesgo: escalamiento excesivo por scheduler -> mitigacion: umbrales y ventanas de evaluacion configurables
- riesgo: regression en flujo ticket existente -> mitigacion: regression suite y comparativa de contrato API

## Criterio De Cierre De Iteracion

- [ ] prioridades y SLA activos en ticket
- [ ] reglas operativas de escalamiento verificadas
- [ ] regression funcional de ticket en verde
- [ ] documentacion y evidencia tecnica actualizadas
