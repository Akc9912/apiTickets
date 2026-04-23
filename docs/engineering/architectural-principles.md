# Architectural Principles

## Propósito

Este documento define los principios arquitectónicos fundamentales que guían el diseño, evolución y mantenimiento de todos los sistemas.

Los principios arquitectónicos son reglas de pensamiento que permiten:

- tomar decisiones técnicas coherentes
- evitar sobreingeniería
- construir sistemas evolutivos
- mantener calidad a largo plazo

Estos principios son independientes de tecnologías específicas, pero en este proyecto se aplican sobre un stack Java 21+, Spring Boot, MySQL 8+, Modular Monolith, organización Package by Feature y Vertical Slices.

---

# 1. Simplicidad como Estrategia

La arquitectura debe ser tan simple como sea posible,
pero lo suficientemente flexible para evolucionar.

La complejidad solo se introduce cuando:

- existe presión técnica real
- existe impacto medible en usuarios o negocio

La complejidad anticipada es deuda técnica futura.

---

# 2. Modularidad como Base de Escalabilidad

Los sistemas deben dividirse en módulos funcionales independientes (features), siguiendo la convención de organización por paquete/feature de Java/Spring Boot.

Cada módulo debe:

- encapsular su lógica
- exponer contratos claros vía interfaces en `api/`
- minimizar dependencias externas

La escalabilidad comienza con la modularidad interna y la estructura Package by Feature.

---

# 3. Separación de Responsabilidades

Cada capa del sistema debe tener un propósito claro y alinearse a la arquitectura por capas adaptada a Java/Spring Boot:

- api/: contrato público
- controller/: endpoints REST
- service/: lógica de negocio
- repository/: acceso a datos
- entity/: modelos JPA
- enum/: enumeraciones

La mezcla de responsabilidades produce:

- código difícil de entender
- testing complejo
- refactors riesgosos
- evolución limitada

---

# 4. Dominio en el Centro

La lógica de negocio debe ser el núcleo del sistema.
En Java/Spring Boot, el dominio se implementa en las capas service/entity, desacoplado de frameworks y bases de datos mediante interfaces y DTOs.
El dominio debe poder sobrevivir cambios tecnológicos.

---

# 5. Evolución sobre Predicción

Los sistemas no deben diseñarse para necesidades futuras hipotéticas.

Deben diseñarse para poder evolucionar cuando esas necesidades aparezcan.

La capacidad de adaptación es más valiosa que la sofisticación inicial.

---

# 6. Contratos Explícitos

Las interacciones entre módulos y capas deben realizarse mediante contratos claros, preferentemente interfaces en `api/`, DTOs y eventos.
Esto permite desacoplamiento, evolución independiente y testing aislado.

---

# 7. Dependencias Controladas

Las dependencias deben fluir en una sola dirección.

Las capas internas no deben depender de detalles externos.

Esto reduce:

- acoplamiento
- efectos colaterales
- complejidad cognitiva

---

# 8. Arquitectura como Facilitador de Cambio

Una buena arquitectura no evita el cambio.

Facilita el cambio.

El objetivo no es diseñar sistemas perfectos,
sino sistemas adaptables.

---

# 9. Optimización Basada en Evidencia

Las optimizaciones deben realizarse únicamente cuando:

- existen métricas que lo justifiquen
- el impacto es significativo

Optimizar sin datos genera:

- complejidad innecesaria
- mantenimiento costoso

---

# 10. Observabilidad como Capacidad Evolutiva

Los sistemas deben poder volverse observables cuando el contexto lo requiera.

Esto incluye:

- métricas
- logs estructurados
- tracing

Pero estas capacidades se introducen progresivamente.

---

# 11. Cohesión Alta, Acoplamiento Bajo

Cada módulo debe ser altamente cohesivo internamente,
pero débilmente acoplado externamente.

Esto permite:

- cambios locales
- testing aislado
- evolución independiente

---

# 12. Arquitectura Incremental

Las mejoras arquitectónicas deben implementarse de forma gradual.

Las migraciones grandes son riesgosas.

La arquitectura debe evolucionar mediante pequeñas decisiones controladas.

---

# 13. Código como Reflejo del Diseño

La estructura del código debe reflejar la estructura conceptual del sistema.

Un código bien organizado permite:

- comprender el dominio rápidamente
- detectar responsabilidades
- reducir errores

---

# Regla Final

Si una decisión técnica contradice estos principios,
debe justificarse formalmente o reconsiderarse.
