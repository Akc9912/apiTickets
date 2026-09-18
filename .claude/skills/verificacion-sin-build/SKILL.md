---
name: verificacion-sin-build
description: Use when you need to verify Java/Spring behavior but the normal test path is unavailable — the build does not compile, the Spring context cannot start, or the failure is in a layer tests don't reach. Covers compiling one module in isolation and running throwaway harnesses against it: JPA mappings, Spring Data derived queries, MockMvc routing, @PreAuthorize rules, bean proxying, and SQL schema validation against a real database in Docker. Also use before claiming a dependency upgrade introduced no errors, or that documentation matches the code.
---

# Verificar sin build verde

Cuando el proyecto no compila, o el contexto de Spring no arranca, `mvn test` no es una
herramienta disponible: ningún test llega a correr. Eso **no** impide verificar. Se compila el
módulo que interesa en aislado y se corren `main()` desechables contra ese classpath.

Vale también con el build en verde, para todo lo que los tests no alcanzan: cómo mapea
Hibernate un tipo, si una consulta derivada resuelve, qué código HTTP devuelve un handler, si
un `@PreAuthorize` deniega.

En Spring, "compila" es una garantía débil. Ruteo, nombres de consultas derivadas, reglas de
autorización, deserialización de DTOs y mapeo de tipos SQL **fallan todos en runtime** con el
compilador en verde.

## Regla primera: el conteo de errores es un piso

**javac aborta después de la fase de resolución.** Si hay imports o símbolos sin resolver,
nunca analiza los cuerpos de los métodos. Consecuencias:

- Arreglar símbolos hace **aparecer** errores nuevos: returns faltantes, métodos que ya no
  existen, casts inválidos. No lo reportes como regresión; era una segunda ola siempre latente.
- Esos errores de cuerpo **no se reportan** mientras haya errores de resolución en otra parte
  del proyecto. Para verlos, compilá el módulo en aislado: ahí sí corre el análisis de flujo.
- `mvn compile` imprime cada error dos veces. Deduplicá con `sort -u` antes de contar.

```bash
mvn -B compile -DskipTests 2>&1 | grep "^\[ERROR\] /home" | sort -u | wc -l
```

## El pipeline

Sustituí `<módulo>` por el paquete que estés verificando.

```bash
S=<tu directorio de scratchpad>

# 1. Classpath. Sin -DincludeScope=test no tenés spring-test ni Mockito.
mvn -B -o dependency:build-classpath -Dmdep.outputFile=$S/cp.txt -q
mvn -B -o dependency:build-classpath -Dmdep.outputFile=$S/cptest.txt -Dmdep.includeScope=test -q

# 2. Compilar SOLO el módulo sano. -proc:full es obligatorio o Lombok no corre:
#    sin getters ni builders generados, todo falla por "cannot find symbol".
mkdir -p $S/out
javac -proc:full --release <version> -cp "$(cat $S/cp.txt)" -d $S/out \
  $(find src/main/java/.../<módulo> -name "*.java")
# Salida vacía = limpio. Agregá los archivos de shared/ que el arnés necesite
# (por ejemplo el @ControllerAdvice), siempre que compilen.

# 3. Compilar y correr el arnés. -proc:none acá: el arnés no usa Lombok.
javac -proc:none --release <version> -cp "$S/out:$(cat $S/cptest.txt)" -d $S/out $S/MiArnes.java
java -cp "$S/out:$(cat $S/cptest.txt)" MiArnes
```

Comprobá que Lombok efectivamente corrió: tienen que aparecer clases `*Builder*.class` en
`$S/out` si las entidades usan `@Builder`.

Escribí los arneses con un `check(descripción, boolean, detalle)` que cuente fallas y termine
en `System.exit(fail == 0 ? 0 : 1)`. Filtrá el ruido de logs al mostrar resultados:

```bash
java -cp ... MiArnes 2>&1 | grep -E "^\[OK\]|^\[FAIL\]|^TODO OK|^FALLAS|Exception in thread"
```

Las plantillas de los cinco tipos de arnés están en `plantillas.md`.

## Qué arnés para qué pregunta

| Pregunta | Herramienta | Detalle que importa |
|---|---|---|
| ¿El mapeo JPA y las consultas derivadas son válidos? | Hibernate + `JpaRepositoryFactory` sobre H2 | Crear el repositorio es lo que parsea **cada** nombre derivado |
| ¿Las rutas resuelven? ¿Bindea el body? | `MockMvcBuilders.standaloneSetup` | **No aplica seguridad** |
| ¿Las reglas `@PreAuthorize` funcionan? | contexto Spring con `@EnableMethodSecurity` | El bean tiene que salir proxeado |
| ¿Se puede seguir inyectando la clase concreta? | contexto con `@EnableTransactionManagement` | Tras agregar una interfaz a un `@Transactional` |
| ¿El SQL coincide con las entidades? | `hbm2ddl=validate` contra la DB real en Docker | Es el chequeo definitivo |
| ¿Qué tipo SQL genera Hibernate? | `hbm2ddl.scripts.action=create` con el dialecto destino | Generalo, no lo adivines |

## Trampas

**`standaloneSetup` no ejercita `@PreAuthorize`.** La seguridad de método es AOP y la aplica el
contenedor. Con MockMvc standalone verificás ruteo y binding, nada más. Para las reglas de rol
hace falta un `AnnotationConfigApplicationContext` con `@EnableMethodSecurity` y el controller
como bean; confirmá que salió proxeado (`getClass()` debe contener `$$SpringCGLIB$$`).

**Registrá el `@ControllerAdvice` o ves los códigos equivocados.**
`standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler())` — sin eso las
excepciones se propagan y no comprobás si una excepción de dominio se mapea a 400 o queda en 500.

**`SimpleGrantedAuthority("")` lanza excepción.** Para simular "autenticado pero sin roles" usá
una lista de authorities vacía, no un authority vacío.

**`hasAnyRole`/`hasRole` agregan el prefijo `ROLE_`.** Un authority sin ese prefijo da 403.
Verificá los dos casos explícitamente: es el error más difícil de diagnosticar en runtime.

**`HBM2DDL_CHARSET_NAME` espera un charset de Java**, no uno de la base (`utf8mb4` tira
`UnsupportedEncodingException` al generar nombres de constraints). No lo seteés.

**Un contenedor de base de datos responde a `ping` antes de terminar de inicializar.** Arrancar
ahí da `Access denied`. Esperá la línea de "listo" en el log, no el ping:

```bash
docker run -d --name dbcheck -p <puerto>:3306 -e MYSQL_ROOT_PASSWORD=r -e MYSQL_DATABASE=<db> mysql:8.0
for i in $(seq 1 90); do
  docker logs dbcheck 2>&1 | grep -q "port: 3306  MySQL Community Server" && break; sleep 1
done
```

Al terminar: `docker rm -f dbcheck`.

**Cuidado con el exit code detrás de un pipe.** `cmd | grep -v ruido; echo $?` devuelve el
código del `grep`, no del comando. Redirigí a un archivo y capturá el código aparte.

## Antes de afirmar que un upgrade no rompió nada: build de control

No alcanza con que el conteo de errores no crezca. Guardá el pom, volvé a las versiones
anteriores, compilá, y **diffeá los sets de errores**:

```bash
cp pom.xml $S/pom.nuevo.xml
sed -e 's|<version>NUEVA</version>|<version>ANTERIOR</version>|' $S/pom.nuevo.xml > pom.xml
mvn -B compile -DskipTests > $S/build-control.log 2>&1
cp $S/pom.nuevo.xml pom.xml          # restaurar SIEMPRE, incluso si falló

grep "^\[ERROR\] /home" $S/build-control.log | sort -u > $S/control.txt
comm -13 $S/control.txt $S/nuevo.txt   # errores que solo aparecen con la versión nueva
```

Sets idénticos = el upgrade no introdujo nada. Eso es verificado; "compila igual" no lo es.

Si el upgrade arrastra una librería que no es compatible hacia atrás (un starter que exige la
versión nueva del framework), volvela también en el pom de control, o el diff mide otra cosa.

## Verificar que la doc coincide con el código

Extraé las rutas del controller y buscalas en la doc, en vez de revisar a ojo:

```python
import re, pathlib
src = pathlib.Path("<ruta al controller>").read_text()
base = re.search(r'@RequestMapping\("([^"]+)"\)', src).group(1)
rutas = {(m.group(1).upper(), base + m.group(2))
         for m in re.finditer(r'@(Get|Put|Post|Delete)Mapping\("([^"]+)"\)', src)}
# 1. cada ruta del código tiene que aparecer en la doc
# 2. en sentido inverso: las rutas que eliminaste no deben seguir documentadas
```

Chequeá también las anclas del índice. Si validás slugs de Markdown, **conservá los caracteres
unicode** (`re.sub(r'[^\w\s-]', '', h, flags=re.UNICODE)`): sacar las tildes genera falsos
positivos en títulos acentuados.

## Alcance honesto

Estos arneses no levantan la aplicación. Si el proyecto no compila del todo, no hay contexto
Spring completo, así que **no** verifican la cadena de filtros de seguridad de punta a punta,
los filtros propios, la autoconfiguración de Boot ni la integración real entre módulos.

Cuando reportes resultados, decí explícitamente qué quedó sin ejercitar. Un arnés que prueba
ruteo no prueba autorización, y decir "verificado" sin esa distinción es afirmar de más.
