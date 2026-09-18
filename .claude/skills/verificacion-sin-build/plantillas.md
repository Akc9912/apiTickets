# Plantillas de arnés

Esqueletos con lo mínimo para que arranquen. Copiá al scratchpad y sustituí `MiEntidad`,
`MiRepository`, `MiController`, `MiService`.

Todos usan este helper:

```java
static int fail = 0;

static void check(String what, boolean ok, String detail) {
    System.out.printf("%s  %-56s %s%n", ok ? "[OK]  " : "[FAIL]", what, detail == null ? "" : detail);
    if (!ok) fail++;
}
// al final de main():
System.out.println(fail == 0 ? "TODO OK (0 fallas)" : "FALLAS: " + fail);
System.exit(fail == 0 ? 0 : 1);
```

---

## 1. Mapeo JPA + consultas derivadas (H2, sin Docker)

Lo importante: **crear el repositorio con `JpaRepositoryFactory` es lo que parsea cada nombre de
método derivado.** Ahí explota un `findByXAndDeletedAtIsNull` cuando la propiedad de la entidad
se llama distinto — un fallo que tumba el arranque de Spring y que el compilador no ve.

Necesita el jar de H2. Si no es dependencia declarada suele estar igual en el repo local:
`find ~/.m2/repository/com/h2database -name "h2-*.jar"`, y agregalo al `-cp`.

```java
import org.hibernate.cfg.Configuration;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import jakarta.persistence.*;
import java.util.*;

Configuration cfg = new Configuration();
cfg.addAnnotatedClass(MiEntidad.class);
Properties p = new Properties();
p.putAll(Map.of(
        "hibernate.connection.driver_class", "org.h2.Driver",
        "hibernate.connection.url", "jdbc:h2:mem:harness;DB_CLOSE_DELAY=-1",
        "hibernate.connection.username", "sa",
        "hibernate.connection.password", "",
        "hibernate.hbm2ddl.auto", "create",
        "hibernate.show_sql", "false"));
cfg.setProperties(p);
// Sin hibernate.dialect: así también comprobás la autodetección.
EntityManagerFactory emf = cfg.buildSessionFactory();
EntityManager em = emf.createEntityManager();

MiRepository repo;
try {
    repo = new JpaRepositoryFactory(em).getRepository(MiRepository.class);
    check("Spring Data parsea todos los metodos derivados", true, "repositorio creado");
} catch (Exception e) {
    check("Spring Data parsea todos los metodos derivados", false, e.getMessage());
    System.exit(1); return;
}

em.getTransaction().begin();
em.persist(/* entidad via builder */);
em.flush();
em.getTransaction().commit();
```

Qué conviene verificar:

- Que los callbacks `@PrePersist`/`@PreUpdate` llenen los campos `NOT NULL` sin default.
- Que `@Builder.Default` se respete al construir por builder.
- Que una columna `updatable = false` no cambie en un update.
- **Cada consulta antes y después de un borrado lógico.** Es la única forma de detectar que a
  una consulta le falta el filtro de baja.
- Que una constraint `UNIQUE` siga aplicando aunque la otra fila esté dada de baja: persistí un
  duplicado y esperá la excepción. Eso determina si un `existsBy…` debe filtrar la baja o no.

**`persist()` no escribe.** El error de constraint aparece en el `flush()`/`commit()`, no en el
`persist()`. Si querés demostrar *cuándo* falla de verdad una operación, no llames a `flush()` y
dejá que explote el commit: eso prueba que "el método volvió sin excepción" no garantiza que la
fila se escribió, cuando hay una transacción más externa que commitea después.

---

## 2. Ruteo y binding (MockMvc standalone)

```java
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.mockito.Mockito;

MiService svc = Mockito.mock(MiService.class);
Mockito.when(svc.getById(Mockito.any())).thenReturn(dummy);

MockMvc mvc = MockMvcBuilders.standaloneSetup(new MiController(svc))
        .setControllerAdvice(new GlobalExceptionHandler())   // o ves 500 donde hay 400
        .build();

int st = mvc.perform(get("/base/search").param("name", "x"))
        .andReturn().getResponse().getStatus();
check("GET /search no se lo come /{id}", st == 200, "status=" + st);
Mockito.verify(svc).searchByName("x");                       // confirma a qué método ruteó
Mockito.verify(svc, Mockito.never()).getById(Mockito.any()); // y a cuál no

// Identidad del autenticado, si el controller la toma de Authentication.getName():
var auth = new UsernamePasswordAuthenticationToken("u@e.com", null, List.of());
mvc.perform(get("/base/perfil").principal(auth));
```

Qué verificar:

- **Rutas que compiten por el mismo patrón**: un path literal (`/search`) contra uno variable
  (`/{id}`). Usá `Mockito.verify(..., never())` para probar a dónde *no* fue.
- Conversión de `@PathVariable` a enum, y que un valor inexistente dé 400 sin llegar al service.
- Que un campo del body no pueda sobrescribir sobre qué recurso opera el endpoint
  (`clearInvocations` antes, y después `verify(eq(valorDelToken), any())`).
- Que los DTOs de request se deserialicen. Un DTO con `@Builder` y sin `@NoArgsConstructor`
  hace que Jackson tire `InvalidDefinitionException` y el endpoint devuelva 500.

**Corré este arnés compilando el módulo SIN `-parameters`.** Si pasa igual, el ruteo no depende
de ese flag del compilador, que es lo deseable aunque el parent de Boot hoy lo active. Si falla
con `Name for argument of type … not specified`, faltan nombres explícitos en
`@PathVariable("x")` / `@RequestParam(name = "x")`.

---

## 3. Reglas `@PreAuthorize` (contexto Spring real)

```java
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

@Configuration
@EnableMethodSecurity
static class Cfg {
    @Bean MiService miService() { /* mock */ }
    @Bean MiController miController(MiService s) { return new MiController(s); }
}

var ctx = new AnnotationConfigApplicationContext(Cfg.class);
MiController c = ctx.getBean(MiController.class);
check("el controller esta proxeado", !c.getClass().equals(MiController.class),
      c.getClass().getSimpleName());

// Sin args: anónimo. Con args: autenticado con esos authorities.
// SimpleGrantedAuthority("") LANZA excepción: para "autenticado sin roles" usá List.of().
static void as(String... authorities) {
    SecurityContextHolder.clearContext();
    if (authorities.length == 0) return;
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken("u@e.com", null,
            Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList()));
}

enum Result { OK, DENIED, NO_AUTH }
static Result call(Runnable r) {
    try { r.run(); return Result.OK; }
    catch (AccessDeniedException e) { return Result.DENIED; }
    catch (AuthenticationCredentialsNotFoundException e) { return Result.NO_AUTH; }
}
```

Casos que no pueden faltar: anónimo, rol insuficiente, rol correcto, **el authority sin prefijo
`ROLE_`** (tiene que dar DENIED) y autenticado sin ningún authority.

---

## 4. Tipo de proxy tras agregar una interfaz a un `@Transactional`

Cuando una clase `@Transactional` empieza a implementar una interfaz, el proxy puede pasar a JDK
dinámico, y entonces **todo el que inyecte la clase concreta falla al arrancar**. Boot usa CGLIB
por defecto, pero conviene comprobarlo y dejar documentado el caso contrario.

```java
@Configuration @EnableTransactionManagement @EnableAspectJAutoProxy(proxyTargetClass = true)
static class CglibCfg extends Base { }        // el default de Boot

@Configuration @EnableTransactionManagement(proxyTargetClass = false)
@EnableAspectJAutoProxy(proxyTargetClass = false)
static class JdkCfg extends Base { }          // spring.aop.proxy-target-class=false

// Base aporta los mocks de las dependencias y un PlatformTransactionManager mockeado.
```

Con la config de Boot tienen que resolver **las dos** (`getBean(MiInterfaz.class)` y
`getBean(MiClase.class)`) y ser el mismo bean. Con `JdkCfg`, la clase concreta debe fallar: eso
es la evidencia de por qué no conviene activar esa property.

---

## 5. SQL contra base real + `hbm2ddl=validate`

El chequeo definitivo de que el script de esquema y las entidades coinciden. Si el script usa un
tipo distinto del que espera la entidad, `validate` falla al construir la SessionFactory.

Primero, **generá el DDL desde la entidad en vez de adivinar los tipos**:

```java
var reg = new StandardServiceRegistryBuilder().applySettings(Map.of(
        AvailableSettings.DIALECT, "<clase del dialecto destino>",
        AvailableSettings.HBM2DDL_SCRIPTS_ACTION, "create",
        AvailableSettings.HBM2DDL_SCRIPTS_CREATE_TARGET, "salida.sql",
        "hibernate.temp.use_jdbc_metadata_defaults", "false")).build();
new MetadataSources(reg).addAnnotatedClass(MiEntidad.class)
        .buildMetadata().buildSessionFactory().close();
```

No pases `HBM2DDL_CHARSET_NAME`: espera un charset de Java y con uno de base de datos revienta.

Esto contesta cosas que se suelen asumir mal: a qué tipo mapea un `UUID` en cada motor, si un
`LocalDateTime` lleva precisión de fracción de segundo, y si un enum de Java sale como tipo
nativo o como varchar. Importa sobre todo con `ddl-auto=update`, porque ahí el script y el ORM
se pelean por cualquier diferencia.

Después, contra el contenedor (esperando la línea del log, ver SKILL.md):

```bash
docker exec dbcheck mysql -uroot -pr -e "DROP DATABASE IF EXISTS <db>; CREATE DATABASE <db> CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
docker exec -i dbcheck mysql -uroot -pr <db> < <script.sql> > $S/err.txt 2>&1; echo "exit=$?"
# aplicalo dos veces para probar idempotencia
```

El `> archivo` en vez de un pipe a `grep` es a propósito: detrás de un pipe, `$?` es del grep.

Y la validación, con el puerto publicado:

```java
p.putAll(Map.of(
        "hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver",
        "hibernate.connection.url", "jdbc:mysql://127.0.0.1:<puerto>/<db>",
        "hibernate.connection.username", "root",
        "hibernate.connection.password", "r",
        "hibernate.hbm2ddl.auto", "validate"));   // validar, no crear
```

Si `buildSessionFactory()` no lanza, el esquema y la entidad están alineados. Después hacé un
ida y vuelta real de datos: que un id generado sobreviva al tipo de la columna, que un hash de
password no se trunque por largo de campo, que los timestamps conserven la precisión declarada,
y que las constraints se comporten como dice el comentario del script.

Para sostener que un script estaba roto, aplicá la versión de git:
`git show HEAD:<ruta.sql> > old.sql`. "Esto nunca funcionó" se demuestra con el error del motor,
no leyendo el archivo.
