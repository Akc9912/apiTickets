# fase-0-estabilizacion-tecnica-minima

## Contexto De La Fase

La iteracion requiere una base estable antes de tocar arquitectura, autenticacion y persistencia. Hoy existen errores de compilacion, metodos sin implementar y ruido de calidad que aumentan el riesgo de regression durante la migracion.

## Objetivo De La Fase

Dejar una linea base tecnica estable y verificable para que las fases de migracion se ejecuten con riesgo controlado.

## Alcance De La Fase

- relevar estado real de compilacion, tests y deuda tecnica bloqueante
- corregir errores bloqueantes y stubs que cortan flujo funcional
- congelar contrato API actual para comparar regresiones
- registrar evidencia de baseline antes de cambios estructurales

## Tareas De La Fase

- [ ] [0.1 Levantar linea base tecnica](0.1.md)
- [ ] [0.2 Corregir bloqueantes de compilacion](0.2.md)
- [ ] [0.3 Congelar contrato API baseline](0.3.md)
- [ ] [0.4 Cerrar baseline con evidencia tecnica](0.4.md)

## Checklist De Avance De Fase

- [ ] todas las tareas listadas en esta fase estan creadas
- [ ] cada tarea tiene comandos o verificacion explicita
- [ ] no hay tareas fuera del rango definido para la fase
- [ ] existe evidencia tecnica de cierre por tarea

## Criterio De Cierre De Fase

- [ ] tareas completadas y verificadas
- [ ] riesgos documentados
- [ ] estado de la fase actualizado en README de iteracion
