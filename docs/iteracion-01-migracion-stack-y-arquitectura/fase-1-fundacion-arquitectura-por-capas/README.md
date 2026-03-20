# fase-1-fundacion-arquitectura-por-capas

## Contexto De La Fase

Con la baseline estable, se debe crear la fundacion estructural para que todo el codigo nuevo siga las reglas de engineering: Modular Monolith por capas, Package by Feature y contratos explicitos.

## Objetivo De La Fase

Definir y aplicar la estructura base por capas para los modulos que se migran en Iteracion 01, sin mezclar codigo nuevo con paquetes legacy.

## Alcance De La Fase

- definir estructura de paquetes presentation/application/domain/persistence/infrastructure
- establecer contratos internos para comunicacion entre modulos
- preparar convenciones de mapeo DTO/domain/entity
- asegurar lineamientos de transacciones en capa application

## Tareas De La Fase

- [ ] [1.1 Definir estructura de paquetes por capas](1.1.md)
- [ ] [1.2 Crear contratos internos entre modulos](1.2.md)
- [ ] [1.3 Establecer estrategia de mapeo DTO-domain-entity](1.3.md)
- [ ] [1.4 Verificar reglas de capas y transacciones](1.4.md)

## Checklist De Avance De Fase

- [ ] todas las tareas listadas en esta fase estan creadas
- [ ] cada tarea tiene comandos o verificacion explicita
- [ ] no hay tareas fuera del rango definido para la fase
- [ ] existe evidencia tecnica de cierre por tarea

## Criterio De Cierre De Fase

- [ ] tareas completadas y verificadas
- [ ] riesgos documentados
- [ ] estado de la fase actualizado en README de iteracion
