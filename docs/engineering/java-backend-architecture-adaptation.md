# Java Backend Architecture Adaptation

## Propósito

Este documento adapta la arquitectura backend base al stack Java 21+, Spring Boot, MySQL 8+, usando Modular Monolith, organización Package by Feature y Vertical Slices para simplificar el desarrollo y mantener cohesión.

Referencia base:

- [java-backend-stack.md](java-backend-stack.md)
- [basic-coding-guidelines.md](basic-coding-guidelines.md)

---

## Estructura por Módulo (Feature)

Cada funcionalidad o módulo de negocio de la aplicación debe ser autocontenido y organizarse con la siguiente estructura interna:

- **Api**: Interfaz pública del módulo (Facade o Contract) y DTOs de entrada/salida. Es el único punto de acceso permitido para otros módulos, garantizando el Principio de Responsabilidad Única (SRP) a nivel de diseño modular.
- **Controller**: Controladores REST para exponer los endpoints HTTP al exterior (Frontend/Mobile).
- **Service**: Implementación de la interfaz del API y lógica de negocio interna del módulo.
- **Repository**: Interfaces de Spring Data JPA y repositorios custom para el acceso a datos.
- **Entity**: Clases de dominio mapeadas a la base de datos (JPA Entities).
- **Enum**: Enumeraciones específicas utilizadas por el módulo.

---

## Estructura Sugerida

```text
src/main/java/com/acme/
  modulo_reservas/           <-- Ejemplo de un módulo (Feature)
    api/                     <-- Contrato público
      ReservaFacade.java     <-- Interfaz de entrada al módulo para uso interno de otros módulos
      dto/                   <-- Objetos de transferencia (Request/Response)
    controller/              <-- Endpoints REST
    entity/                  <-- Modelos JPA
    enum/                    <-- Enumeraciones propias del módulo
    repository/              <-- Interfaces Spring Data JPA
    service/                 <-- Lógica de negocio (Implementa ReservaFacade)
```

---

## Reglas Especificas

- **Encapsulamiento de Módulos**: Un módulo no debe invocar directamente al `Service`, `Entity` o `Repository` de otro módulo. Si el módulo A necesita interactuar con el módulo B, debe hacerlo a través de la interfaz definida en la carpeta `api/` del módulo B.
- No colocar lógica de negocio en los `Controllers`. Su única responsabilidad es recibir peticiones HTTP, validarlas, delegar a la capa interna y retornar la respuesta HTTP adecuada.
- Las transacciones (`@Transactional`) deben definirse en la capa `Service`.
- Exponer DTOs en lugar de `Entities` tanto para la comunicación hacia el exterior a través del `Controller`, como para la comunicación entre módulos.

---

## Testing Recomendado

- service: Unit tests para validar exhaustivamente la lógica de negocio.
- controller: Integration tests (MockMvc) para validar los endpoints de la API REST.
- repository: Data JPA tests (DataJpaTest) solo si existen consultas personalizadas complejas o proyecciones.
