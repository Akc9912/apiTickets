# apiTickets

Backend Spring Boot de gestión de tickets, en medio de un refactor grande. Lo que sigue son
los hechos que no se deducen rápido leyendo el código, y las trampas que cuestan intentos.

## Estado: el build está roto

`mvn compile` falla con **45 errores únicos, todos en `module/ticket`**.

- **`module/users`, `module/auth` y `shared/` compilan con 0 errores.**
- Los 45 restantes referencian las clases eliminadas `Admin`, `Developer`, `Support`,
  `Superadmin`, los DTOs viejos (`UserResponseDto`, `DeveloperResponseDto`) y los roles
  `DEVELOPER`/`SUPPORT`, que ya no existen en `UserRole`.
- `shared/config/DataInitializer` **fue eliminado**: sembraba usuarios con las subclases
  borradas y se decidió no usarlo más.

**45 es un piso, no el total.** javac aborta después de la fase de resolución y nunca analiza
los cuerpos de los métodos. Cuando se resuelvan los símbolos de ticket van a aparecer una
segunda ola: `user.getRole()` (hoy es `getGlobalRole()`), `UserRole.SUPPORT`, y los
`instanceof Support`.

No tomes un conteo de errores estable como señal de que no avanzaste: arreglar `auth` bajó el
total de 59 a 45, pero varios de sus errores nunca habían estado contados.

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

## Decisión de auth: tokens locales, no Supabase

`architecture.md`, `README.md`, `next_steps.md` y `docs/iteracion-01-.../fase-3-*` dicen que
Supabase Auth emite los tokens y que el backend "NO genera tokens". **Eso quedó descartado:**
el backend emite y rota access y refresh tokens. Si leés esos documentos, esa parte está vieja.

## Modelo del principal: resuelto

`module/auth/security/UserPrincipal` implementa `UserDetails` envolviendo un `User`, para no
acoplar la entidad JPA a Spring Security. Emite el authority como **`ROLE_` + `globalRole`**:
`hasAnyRole` agrega ese prefijo solo, y `ADMIN` pelado da 403 sin explicación visible.

Sólo una cuenta `ACTIVE` y no borrada está habilitada, y `SUSPENDED` cuenta como bloqueada. El
filtro JWT lo chequea en cada request: el access token no es revocable, así que suspender a
alguien tiene efecto inmediato aunque su token siga vigente.

## Queda una decisión abierta

**Ids de ticket.** Las entidades de ticket usan `int` y no pueden tener FK a `users.id`
(`binary(16)`). O migran a UUID, o referencian al usuario por una columna sin relación JPA.
Hasta resolverlo, las tablas de ticket no tienen integridad referencial.

## Convenciones del módulo auth

- **`module/auth/api/AuthApi` es el contrato de entrada.** `AuthController` depende de la
  interfaz, no de `AuthService`.
- **La app no arranca sin `JWT_SECRET`.** `JwtService` valida el largo del secreto al construir
  el bean, no en el primer login: HS256 exige 256 bits, y el default del
  `application.properties` tiene 16 caracteres a propósito, para que falle fuerte y temprano en
  vez de firmar con un secreto débil. Exportá `JWT_SECRET` con 32+ caracteres.
- **`/api/auth/**` ya NO es `permitAll` en bloque.** La lista de rutas públicas vive en
  `shared/security/PublicEndpoints` y es **coincidencia exacta**, usada tanto por
  `SecurityConfig` como por `JwtAuthenticationFilter`. `logout` y `change-password` requieren
  token. Si agregás un endpoint público, va ahí y en ningún otro lado.
- **Nunca loguees el token.** El filtro lo hacía en INFO. Los eventos de seguridad van por
  `SecurityAuditLog` (logger `security`) y no incluyen tokens ni secretos.
- **Los tokens se guardan hasheados con SHA-256, nunca en claro.** No uses BCrypt para esto
  aunque `next_steps.md` lo sugiera: saltea, así que rompe el UNIQUE de la columna y la
  búsqueda por hash.
  - El código de verificación es de 6 dígitos, así que su hash se calcula sobre
    `userId + ":" + código`. Sin mezclar el usuario, dos personas con el mismo código
    colisionarían contra el UNIQUE. Por eso `verify-email` pide el email además del código.
- **`consumeVerificationCode` y `rotateRefreshToken` llevan
  `@Transactional(noRollbackFor = IllegalArgumentException.class)`.** No es cosmético: las dos
  guardan algo (el contador de intentos, la revocación en cascada) y **después** lanzan. Con el
  rollback por defecto se perdía justo ese efecto, y ni el límite de intentos ni la defensa
  contra el reuso de refresh tokens hacían nada.
- **`register` no es `@Transactional` a propósito.** Compone pasos transaccionales y termina
  mandando un mail, que es irreversible: en una sola transacción el mail saldría antes del
  commit.
- jjwt está en 0.13.0 con la API nueva (`Jwts.parser().verifyWith(...)`), no la de 0.11.x.

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
