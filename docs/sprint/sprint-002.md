# Sprint 2: Módulo Auth

**Based on:** docs/plan/plan-001.md - Fase 2  
**Date:** 2026-04-23  
**Duration:** 20h  
**Dependencies:** sprint-001.md

## Sprint Goal

Implementar el módulo de autenticación (auth) con endpoints, lógica JWT y pruebas.

## Tasks

### Critical (P0)

```java
// P0 FEATURE [api] Definir AuthApi y DTOs (2h)
// P0 FEATURE [controller] Implementar AuthController con endpoints de login y registro (3h)
// P0 FEATURE [service] Implementar AuthServiceImpl con lógica de autenticación y JWT (4h)
// P0 SECURITY [security] Configurar SecurityConfig para JWT y reglas de acceso (3h)
// P0 FEATURE [entity] Crear entidad User en auth/entity/ (2h)
// P0 FEATURE [repository] Crear UserRepository en auth/repository/ (1h)
// P0 TEST [service] Pruebas unitarias y de integración para Auth (3h)
```

### Important (P1)

```java
// P1 DOCS [api] Documentar endpoints Auth en Swagger/OpenAPI (1h)
```

### Optional (P2/P3)

```java
// P2 TECHDEBT [service] Mejorar validaciones y manejo de errores en Auth (1h)
```

## Definition of Done

- [ ] Endpoints de autenticación funcionales
- [ ] Seguridad JWT implementada
- [ ] Pruebas unitarias y de integración completas
- [ ] Documentación Swagger actualizada

## Next Sprint

- **Blocks:** sprint-003.md (Módulo Users y Modelado de Roles)
