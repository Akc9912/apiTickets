# iteracion-01-migracion-stack-y-arquitectura

## Objetivo De La Iteracion

Completar la migracion del backend apiTickets desde el estado actual (MySQL + JWT local + estructura legacy) hacia la arquitectura objetivo definida en engineering: Modular Monolith por capas, Package by Feature, PostgreSQL y Supabase Auth.

Resultado esperado de negocio: backend MVP modular, predecible y mantenible, listo para evolucion incremental sin mezclar codigo nuevo con codigo legacy.

## Alcance

- estabilizar la linea base tecnica para ejecutar la migracion sin ruido operativo
- migrar persistencia base a PostgreSQL y preparar transicion a UUID por modulo
- implementar auth como validador de JWT de Supabase (sin emision local de tokens)
- separar account como modulo de perfil y contratos para consumo interno
- refactorizar ticket para desacoplar dependencias directas con user/account
- cerrar la iteracion con verificaciones tecnicas y actualizacion documental

## Fuera De Alcance

- implementacion de module/product y module/notification
- introduccion de Redis, RabbitMQ, Kafka, microservicios o Kubernetes
- optimizaciones avanzadas de performance sin metrica previa
- funcionalidades de tiempo real y email transaccional post-MVP

## Estado De Fases

- [ ] Fase 0 - estabilizacion tecnica minima: [README de fase](fase-0-estabilizacion-tecnica-minima/README.md)
- [ ] Fase 1 - fundacion arquitectura por capas: [README de fase](fase-1-fundacion-arquitectura-por-capas/README.md)
- [ ] Fase 2 - migracion persistencia y uuid base: [README de fase](fase-2-migracion-db-y-uuid-base/README.md)
- [ ] Fase 3 - auth supabase validador: [README de fase](fase-3-auth-supabase-validador/README.md)
- [ ] Fase 4 - account modular por contratos: [README de fase](fase-4-account-modular-por-contratos/README.md)
- [ ] Fase 5 - ticket modular desacoplado: [README de fase](fase-5-ticket-modular-desacoplado/README.md)
- [ ] Fase 6 - limites modulares y pruebas: [README de fase](fase-6-limites-modulares-y-pruebas/README.md)
- [ ] Fase 7 - cutover limpieza y documentacion: [README de fase](fase-7-cutover-limpieza-y-documentacion/README.md)

## Desglose Por Fase

| Fase                                        | Rango de tareas | README de fase                                                                                         | Estado |
| ------------------------------------------- | --------------- | ------------------------------------------------------------------------------------------------------ | ------ |
| Fase 0 - estabilizacion tecnica minima      | 0.1 a 0.4       | [fase-0-estabilizacion-tecnica-minima/README.md](fase-0-estabilizacion-tecnica-minima/README.md)       | [ ]    |
| Fase 1 - fundacion arquitectura por capas   | 1.1 a 1.4       | [fase-1-fundacion-arquitectura-por-capas/README.md](fase-1-fundacion-arquitectura-por-capas/README.md) | [ ]    |
| Fase 2 - migracion persistencia y uuid base | 2.1 a 2.4       | [fase-2-migracion-db-y-uuid-base/README.md](fase-2-migracion-db-y-uuid-base/README.md)                 | [ ]    |
| Fase 3 - auth supabase validador            | 3.1 a 3.4       | [fase-3-auth-supabase-validador/README.md](fase-3-auth-supabase-validador/README.md)                   | [ ]    |
| Fase 4 - account modular por contratos      | 4.1 a 4.3       | [fase-4-account-modular-por-contratos/README.md](fase-4-account-modular-por-contratos/README.md)       | [ ]    |
| Fase 5 - ticket modular desacoplado         | 5.1 a 5.4       | [fase-5-ticket-modular-desacoplado/README.md](fase-5-ticket-modular-desacoplado/README.md)             | [ ]    |
| Fase 6 - limites modulares y pruebas        | 6.1 a 6.3       | [fase-6-limites-modulares-y-pruebas/README.md](fase-6-limites-modulares-y-pruebas/README.md)           | [ ]    |
| Fase 7 - cutover limpieza y documentacion   | 7.1 a 7.3       | [fase-7-cutover-limpieza-y-documentacion/README.md](fase-7-cutover-limpieza-y-documentacion/README.md) | [ ]    |

## Riesgos Y Mitigaciones

- riesgo: ruptura de contrato API durante refactor por capas -> mitigacion: congelar baseline de endpoints y validar diffs por fase
- riesgo: acoplamiento residual ticket-user -> mitigacion: introducir contratos de aplicacion y bloquear imports directos entre modulos nuevos
- riesgo: migracion de int a UUID con datos inconsistentes -> mitigacion: migracion incremental por modulo y validaciones de integridad en cada corte
- riesgo: errores de autenticacion al pasar a Supabase JWT -> mitigacion: coexistencia controlada en entorno de prueba con checklist de claims y autorizacion

## Criterio De Cierre De Iteracion

- [ ] todas las fases cerradas
- [ ] todas las tareas verificadas
- [ ] documentacion actualizada
- [ ] metricas y evidencia registradas
- [ ] no hay dependencias directas no permitidas entre modulos migrados

## Referencias Engineering

- [engineering/manifesto.md](../../engineering/manifesto.md)
- [engineering/architecture/backend-architecture.md](../../engineering/architecture/backend-architecture.md)
- [engineering/architecture/java-backend-architecture-adaptation.md](../../engineering/architecture/java-backend-architecture-adaptation.md)
- [engineering/stack/java-backend-stack.md](../../engineering/stack/java-backend-stack.md)
- [engineering/rules/code-documentation-generation-rules.md](../../engineering/rules/code-documentation-generation-rules.md)
