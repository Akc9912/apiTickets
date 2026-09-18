# apiTickets

Backend Spring Boot de gestión de tickets, en medio de un refactor grande. Lo que sigue son
los hechos que no se deducen rápido leyendo el código, y las trampas que cuestan intentos.

## Estado: el build está roto

`mvn compile` falla con **59 errores únicos**. No es una regresión nueva: es deuda del
refactor que unificó las subclases de usuario.

- **`module/users` es el único módulo limpio.** Compila con 0 errores.
- Los 59 errores están en `module/ticket` (41), `module/auth` (10) y
  `shared/config/DataInitializer` (4). Todos referencian las clases eliminadas `Admin`,
  `Developer`, `Support`, `Superadmin`, los DTOs viejos (`UserResponseDto`, `UserRequestDto`,
  `DeveloperResponseDto`) y los roles `DEVELOPER`/`SUPPORT`, que ya no existen en `UserRole`.

**59 es un piso, no el total.** javac aborta después de la fase de resolución y nunca analiza
los cuerpos de los métodos. Cuando se resuelvan los símbolos van a aparecer una segunda ola:
`user.getRole()` (hoy es `getGlobalRole()`), `user.setPassword()`, `user.isActive()`,
`user.setChangePassword()`, `UserRole.SUPPORT`, el cast `(UserDetails) usuario` en
`CustomUserDetailsService` y `userRepository.findByEmail()`.

No tomes un conteo de errores estable como señal de que no avanzaste.

## Comandos

```bash
mvn -B compile -DskipTests          # el build
mvn -B compile 2>&1 | grep "^\[ERROR\] /home" | sort -u | wc -l   # errores únicos (con dups son ~118)
```

- **No uses `./mvnw`**: no tiene permiso de ejecución. Invocarlo como `sh mvnw` o `bash mvnw`
  falla distinto (`mvnw/.mvn/wrapper/maven-wrapper.properties: No es un directorio`), porque
  el script deriva su directorio de `$0`. Usá el `mvn` del sistema.
- `mvn compile` imprime cada error **dos veces**. Para contar, deduplicá con `sort -u`.
- No hay tests reales: `MiapiApplicationTests` es solo un `contextLoads`, y no va a pasar
  hasta que Spring pueda arrancar. Para verificar cosas sin build verde, ver el skill
  `verificacion-sin-build`.

## Entorno

- **El único JDK instalado es el 21** (`/usr/lib/jvm/java-21-openjdk`). El pom apunta a 21;
  si alguien lo sube, el build deja de compilar en esta máquina.
- Spring Boot **4.1.1** (última estable — `4.2.0-M1` es milestone), que arrastra Spring
  Framework 7.0.9, Spring Security 7.1.1 y Hibernate ORM 7.4.5. No hace falta pinnear
  Hibernate: lo gestiona el parent.
- springdoc-openapi **3.1.1**. La línea 2.x no soporta Framework 7; no la bajes.
- Docker está disponible y sirve para levantar `mysql:8.0` real.

## Base de datos: es MySQL, no PostgreSQL

`architecture.md`, `README.md` y `next_steps.md` dicen que la migración a PostgreSQL 16 está
hecha o en curso. **No empezó.** El pom solo trae `mysql-connector-j`, `application.properties`
usa `com.mysql.cj.jdbc.Driver` y `docker-compose.yml` corre `mysql:8.0`. Tratá los docs como
intención, no como estado.

- El esquema vigente es `script/database/00-init.sql` (se monta en el contenedor).
  `script/database/10-ticket-pending.sql` **no se aplica**.
- No declares `hibernate.dialect` a mano. Se quitó a propósito: `MySQL8Dialect` fue eliminado
  en Hibernate 7 y hacía fallar el arranque. Hibernate lo autodetecta.
- `ddl-auto=update` está activo, así que el script y las entidades tienen que coincidir.
  Hibernate 7 mapea **`UUID` → `binary(16)`** en MySQL (no `char(36)`), `LocalDateTime` →
  `datetime(6)`, y los enums de Java → `ENUM` nativo.

## Modelo de usuario

Una sola entidad `User`, sin subclases. El rol es el enum `globalRole`
(`SUPERADMIN` | `ADMIN` | `USER`) y el ciclo de vida es `status`
(`PENDING_VERIFICATION` | `ACTIVE` | `INACTIVE` | `SUSPENDED` | `DELETED`). Ids UUID.

- **Baja lógica**: `deletedAt` es la fuente de verdad de todas las consultas.
  `status = DELETED` se mantiene en sync solo para mostrar. `changeStatus()` rechaza
  `DELETED` a propósito: para eso está `softDelete()`.
- **El `UNIQUE` de email aplica a la tabla completa**, incluidos los dados de baja: un
  usuario con `deletedAt` sigue ocupando su email. Por eso `UserRepository.existsByEmail`
  es el único método que deliberadamente **no** filtra `deletedAt`. Si lo "arreglás" para
  que filtre, el alta pasa la validación y revienta contra la constraint.
- `createdAt`/`updatedAt` son `NOT NULL` sin default y los llenan `@PrePersist`/`@PreUpdate`
  de la entidad.

## Dos decisiones abiertas que bloquean todo

1. **Modelo del principal.** `User` no implementa `UserDetails` y nadie emite authorities.
   Hay que elegir entre que la entidad lo implemente o introducir un `UserPrincipal` aparte.
   Hasta entonces **los 9 endpoints de `users` responden 403** — falla cerrado, es
   intencional. `getAuthorities()` tiene que devolver **`ROLE_ADMIN`, con prefijo**;
   `hasAnyRole` lo agrega solo y `ADMIN` pelado no alcanza.
2. **Ids de ticket.** Las entidades de ticket usan `int` y no pueden tener FK a
   `users.id` (`binary(16)`). O migran a UUID, o referencian al usuario por una columna sin
   relación JPA. Hasta resolverlo, las tablas de ticket no tienen integridad referencial.

## Convenciones del módulo users

- **`module/users/api/UserApi` es el contrato de entrada.** Los consumidores externos deben
  inyectar `UserApi`, no `UserService`. Hoy `AuthService`, `TicketService`,
  `TicketRefundRequestService` y `TicketController` todavía inyectan `UserService`: funciona
  porque es el mismo bean, pero rompe el límite modular.
  - No pongas `spring.aop.proxy-target-class=false`: con `UserService` implementando una
    interfaz, el proxy pasaría a ser JDK dinámico y todo el que inyecte la clase concreta
    falla al arrancar.
  - `findById`, `findByEmail` y `save` exponen la entidad en el contrato. Es una excepción
    documentada, no un descuido: `Ticket` la referencia con `@ManyToOne` y `auth` la necesita
    para firmar el JWT.
- **`UserApi.create()` no tiene endpoint.** El único llamador es `AuthService.register()`.
  No hay login automático: la cuenta se valida por mail. Devuelve `void` y la confirmación es
  la ausencia de excepción. Ojo: si `register()` es `@Transactional`, el INSERT recién se
  confirma al salir de `register()`, así que el mail de verificación va **después** del commit.
- **No corre `@Valid` en el alta** (no hay endpoint), así que las validaciones dentro de
  `UserService.create()` son las únicas que existen. No las saques.
- **Seguridad por método.** Al unificar los controllers se perdió el `@PreAuthorize` de
  clase: cada método lleva el suyo, y un método nuevo sin anotar queda accesible a cualquier
  autenticado. `SecurityConfig` exige el rol por URL para `/api/admin/**` como red de
  contención. `@EnableMethodSecurity` es obligatorio: sin eso Spring ignora los
  `@PreAuthorize` en silencio.
- **DTOs con `@Builder` necesitan `@NoArgsConstructor`** si Jackson los deserializa.
  `@Builder` suprime el constructor por defecto y el endpoint devuelve 500.
  `CreateUserRequest` todavía no lo tiene: si `register()` lo bindea desde un body, falla.
- **Nombrá siempre `@PathVariable("x")` y `@RequestParam(name = "x")`.** Sin nombre explícito
  el ruteo depende del flag `-parameters`, que hoy viene del parent de Boot.

## Documentación

`endpoints.md`, `architecture.md` y `README.md` están actualizados **solo en la parte de
usuarios**. Las secciones de Developer, Support y Tickets describen el esquema legacy (ids
enteros, roles eliminados) y código que no compila. Si tocás algo de ticket, actualizalas.
