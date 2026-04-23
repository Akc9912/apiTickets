# Sprint 3: Módulo Users y Modelado de Roles

**Based on:** docs/plan/plan-001.md - Fase 3  
**Date:** 2026-04-23  
**Duration:** 20h  
**Dependencies:** sprint-002.md

## Sprint Goal

Implementar el módulo de usuarios (users) y el nuevo modelo de roles basado en UserProfile y UserRole (enum), eliminando herencia.

## Tasks

### Critical (P0)

```java
// P0 FEATURE [api] Definir UsersApi y DTOs (2h)
// P0 FEATURE [controller] Implementar UsersController con endpoints CRUD (3h)
// P0 FEATURE [service] Implementar UsersServiceImpl con lógica de negocio y validación de roles (4h)
// P0 FEATURE [entity] Crear entidad UserProfile con campo UserRole (enum) en users/entity/ (2h)
// P0 REFACTOR [entity] Eliminar herencia en usuarios, migrar a modelo único (2h)
// P0 FEATURE [repository] Crear UsersRepository en users/repository/ (1h)
// P0 TEST [service] Pruebas unitarias y de integración para Users (3h)
```

### Important (P1)

```java
// P1 DOCS [api] Documentar endpoints Users en Swagger/OpenAPI (1h)
```

### Optional (P2/P3)

```java
// P2 TECHDEBT [service] Mejorar validaciones y manejo de errores en Users (1h)
```

## Definition of Done

- [ ] CRUD de usuarios funcional
- [ ] UserProfile y UserRole implementados sin herencia
- [ ] Pruebas unitarias y de integración completas
- [ ] Documentación Swagger actualizada

## Next Sprint

- **Blocks:** sprint-004.md (Módulo Ticketing)
