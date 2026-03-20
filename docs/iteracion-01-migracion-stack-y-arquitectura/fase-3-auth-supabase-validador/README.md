# fase-3-auth-supabase-validador

## Contexto De La Fase

El manifiesto y la adaptacion Java exigen contratos claros, dominio desacoplado y capas explicitas por modulo. El estado actual mantiene auth acoplado a user y gestion local de password/JWT.

## Objetivo De La Fase

Implementar el nuevo modulo auth en arquitectura por capas para validar JWT emitido por Supabase, eliminando emision de token local en el backend.

## Alcance De La Fase

- crear estructura por capas de auth en el codigo nuevo
- mover auth a validacion de JWT externo (Supabase)
- retirar responsabilidades de emision local de token y password login legacy
- exponer contrato de autenticacion reutilizable para otros modulos

## Tareas De La Fase

- [ ] [3.1 Crear estructura por capas para auth](3.1.md)
- [ ] [3.2 Implementar validacion JWT Supabase](3.2.md)
- [ ] [3.3 Reemplazar filtro y principal de seguridad](3.3.md)
- [ ] [3.4 Verificar autorizacion por roles y claims](3.4.md)

## Checklist De Avance De Fase

- [ ] todas las tareas listadas en esta fase estan creadas
- [ ] cada tarea tiene comandos o verificacion explicita
- [ ] no hay tareas fuera del rango definido para la fase
- [ ] existe evidencia tecnica de cierre por tarea

## Criterio De Cierre De Fase

- [ ] tareas completadas y verificadas
- [ ] riesgos documentados
- [ ] estado de la fase actualizado en README de iteracion
