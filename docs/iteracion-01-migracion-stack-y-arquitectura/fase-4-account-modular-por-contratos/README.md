# fase-4-account-modular-por-contratos

## Contexto De La Fase

Luego de auth, se debe extraer el dominio de perfil de usuario a account para separar identidad/autenticacion de datos de negocio, respetando limites modulares y evitando acoplamiento al modelo legacy.

## Objetivo De La Fase

Implementar account como modulo por capas con contratos claros para consulta de perfil y roles, sin gestion de credenciales dentro del modulo.

## Alcance De La Fase

- crear estructura por capas para account
- migrar perfil y estado de usuario al nuevo modulo
- publicar puertos/contratos para consumo por ticket y otros modulos

## Tareas De La Fase

- [ ] [4.1 Crear estructura por capas de account](4.1.md)
- [ ] [4.2 Migrar perfil de usuario a account](4.2.md)
- [ ] [4.3 Publicar contratos account para otros modulos](4.3.md)

## Checklist De Avance De Fase

- [ ] todas las tareas listadas en esta fase estan creadas
- [ ] cada tarea tiene comandos o verificacion explicita
- [ ] no hay tareas fuera del rango definido para la fase
- [ ] existe evidencia tecnica de cierre por tarea

## Criterio De Cierre De Fase

- [ ] tareas completadas y verificadas
- [ ] riesgos documentados
- [ ] estado de la fase actualizado en README de iteracion
