# fase-5-ticket-modular-desacoplado

## Contexto De La Fase

Ticket es el modulo funcional central del MVP y hoy tiene acoplamiento bidireccional con user. Esta fase aplica desacople por contratos para alinear el dominio con la arquitectura modular.

## Objetivo De La Fase

Completar la migracion del modulo ticket a arquitectura por capas desacoplada y dejar su integracion con account/auth mediante contratos explicitos.

## Alcance De La Fase

- refactor de ticket a capas presentation/application/domain/persistence/infrastructure
- eliminacion de relaciones JPA directas ticket-user/developer en el codigo nuevo
- actualizacion de endpoints de ticket sobre servicios de aplicacion
- verificacion funcional del flujo ticket completo

## Tareas De La Fase

- [ ] [5.1 Refactor ticket a arquitectura por capas](5.1.md)
- [ ] [5.2 Eliminar acoplamientos directos ticket-user](5.2.md)
- [ ] [5.3 Adaptar endpoints y DTOs de ticket](5.3.md)
- [ ] [5.4 Verificar flujo funcional de ticket](5.4.md)

## Checklist De Avance De Fase

- [ ] todas las tareas listadas en esta fase estan creadas
- [ ] cada tarea tiene comandos o verificacion explicita
- [ ] no hay tareas fuera del rango definido para la fase
- [ ] existe evidencia tecnica de cierre por tarea

## Criterio De Cierre De Fase

- [ ] tareas completadas y verificadas
- [ ] riesgos documentados
- [ ] estado de la fase actualizado en README de iteracion
