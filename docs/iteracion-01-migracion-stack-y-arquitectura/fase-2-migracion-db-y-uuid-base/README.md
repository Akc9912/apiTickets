# fase-2-migracion-persistencia-y-uuid-base

## Contexto De La Fase

El stack oficial de engineering exige PostgreSQL como persistencia base y migracion incremental a UUID. El repositorio todavia opera con MySQL e IDs enteros, lo que bloquea la alineacion con el contrato objetivo.

## Objetivo De La Fase

Completar la base tecnica de persistencia para operar en PostgreSQL y preparar la transicion a UUID por modulo sin refactor global disruptivo.

## Alcance De La Fase

- actualizar dependencias y configuracion para PostgreSQL
- definir estrategia de migracion incremental int -> UUID
- aplicar primera ola de cambios de esquema y entidades base
- verificar compilacion, consultas y compatibilidad de repositorios

## Tareas De La Fase

- [ ] [2.1 Migrar dependencia y configuracion a PostgreSQL](2.1.md)
- [ ] [2.2 Definir estrategia incremental de UUID por modulo](2.2.md)
- [ ] [2.3 Aplicar cambios base de esquema y entidades](2.3.md)
- [ ] [2.4 Verificar persistencia y repositorios post-migracion](2.4.md)

## Checklist De Avance De Fase

- [ ] todas las tareas listadas en esta fase estan creadas
- [ ] cada tarea tiene comandos o verificacion explicita
- [ ] no hay tareas fuera del rango definido para la fase
- [ ] existe evidencia tecnica de cierre por tarea

## Criterio De Cierre De Fase

- [ ] tareas completadas y verificadas
- [ ] riesgos documentados
- [ ] estado de la fase actualizado en README de iteracion
