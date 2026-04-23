---
name: quick-audit
description: "Auditoría rápida y no invasiva (solo lectura) para entender cómo está construido un sistema: arquitectura, tecnologías, patrones de implementación y estado general."
---

## Activación

Ejecuta esta skill cuando el usuario pida:

- "Auditoría rápida del sistema"
- "Cómo está construido este sistema"
- "Qué stack usa este proyecto"
- "Analiza la implementación actual"
- "Haz un inventario tecnológico rápido"

## Modo de operación

**Esta skill solo LEE archivos y ejecuta comandos de consulta. Nunca modifica, escribe ni ejecuta cambios.**

## Entrada

- Ruta del proyecto o sistema a auditar (por defecto: directorio actual)
- Opcional: profundidad de análisis (básica/media/completa)

## Salida

1. **Reporte en Markdown** mostrado en el chat
2. **Archivo guardado** en `/docs/audit/audit-XXX.md` (numeración incremental)

## Instrucciones

### Paso 1: Exploración inicial (solo lectura)

Lee/ejecuta (según acceso) en este orden:

```bash
# 1. Identificar tipo de proyecto
ls -la                    # estructura general
cat package.json 2>/dev/null | jq .name,.scripts,.dependencies 2>/dev/null || cat package.json
cat requirements.txt 2>/dev/null
cat go.mod 2>/dev/null
cat Cargo.toml 2>/dev/null
cat composer.json 2>/dev/null
cat Gemfile 2>/dev/null
cat mix.exs 2>/dev/null
cat build.gradle 2>/dev/null
cat pom.xml 2>/dev/null

# 2. Detectar contenedores y orquestación
cat Dockerfile 2>/dev/null | head -30
cat docker-compose.yml 2>/dev/null | head -50
cat kubernetes/*.yaml 2>/dev/null | head -30
ls -la k8s/ 2>/dev/null

# 3. Configuración y entorno
cat .env* 2>/dev/null | grep -v "PASSWORD\|SECRET\|KEY" | head -20
cat config/*.yml 2>/dev/null | head -30
cat config/*.yaml 2>/dev/null | head -30
cat config/*.json 2>/dev/null | head -30

# 4. Estado (solo lectura)
ps aux | grep -E "node|python|java|nginx|apache|postgres|mysql|redis"
df -h
free -h
uptime
```

### Paso 2: Analizar arquitectura

Responde estas preguntas observando los archivos:

**¿Qué tipo de proyecto es?**

- **Backend API:** busca `routes/`, `controllers/`, `models/`
- **Frontend SPA:** busca `src/`, `components/`, `package.json` con React/Vue/Angular
- **Fullstack:** busca frontend + backend en mismo repo
- **Microservicios:** múltiples Dockerfiles o docker-compose.yml con varios servicios
- **Serverless:** busca `serverless.yml`, `template.yaml` (SAM), `functions/`

**¿Patrón de implementación?**

- **Monolito:** un solo main o app entry point
- **Modular:** múltiples paquetes o submódulos
- **Hexagonal/DDD:** busca `domain/`, `application/`, `infrastructure/`
- **REST API:** busca `routes/`, `@RestController`, `@RequestMapping`
- **GraphQL:** busca `*.graphql`, `schema.graphql`
- **gRPC:** busca `*.proto`

### Paso 3: Generar reporte

Crea este reporte en el chat Y guárdalo en archivo:

---

**📋 Auditoría Rápida: `{{nombre_del_proyecto}}`**

**🏗️ Arquitectura Inferida**

- **Tipo:** {{backend/frontend/fullstack/microservicios/serverless}}
- **Patrón:** {{monolito/modular/hexagonal/otros}}
- **API Style:** {{REST/GraphQL/gRPC/mixto}}

**🛠️ Stack Tecnológico**

**Backend**
| Componente | Tecnología | Versión (si detectable) |
|------------|------------|------------------------|
| Runtime | {{Node/Python/Java/Go/Rust/Elixir/PHP}} | {{x.y.z}} |
| Framework | {{Express/Django/Spring/Bootstrap}} | {{x.y.z}} |
| ORM | {{Prisma/SQLAlchemy/Hibernate}} | {{x.y.z}} |

**Frontend (si aplica)**
| Componente | Tecnología |
|------------|------------|
| Framework | {{React/Vue/Angular/Svelte}} |
| State Mgmt | {{Redux/Pinia/NgRx/ninguno}} |
| CSS | {{Tailwind/Sass/Styled-components}} |

**Base de Datos**

- **Principal:** {{PostgreSQL/MySQL/MongoDB/ninguna}}
- **Cache/Redis:** {{sí/no}}
- **Otros:** {{Elasticsearch/ClickHouse/etc}}

**Infraestructura**
| Componente | Detalle |
|------------|---------|
| Contenedores | {{Docker/docker-compose/K8s/ninguno}} |
| Web Server | {{Nginx/Apache/Caddy/ninguno}} |
| Proxy | {{Traefik/HAProxy/Cloudflare}} |

**📂 Estructura Detectada**

```
{{árbol de directorios relevante - solo primeros 2 niveles}}
```

**🔍 Hallazgos Clave**

- ✅ **Puntos fuertes:** {{lo que está bien implementado}}
- ⚠️ **A mejorar:** {{lo que podría optimizarse}}
- ❓ **Sin detectar:** {{tecnologías comunes que no se encontraron}}

**📊 Estado Actual (si accesible)**

- **Uptime:** {{uptime}}
- **RAM:** {{usada/total}}
- **Disco:** {{usado/total}}
- **Procesos críticos:** {{servicios clave funcionando}}

**💡 Recomendaciones Rápidas**

1. {{recomendación 1}}
2. {{recomendación 2}}
3. {{recomendación 3}}

---

### Paso 4: Guardar auditoría en archivo

1. **Crear directorio si no existe:**

   ```bash
   mkdir -p docs/audit
   ```

2. **Determinar número secuencial:**
   - Listar archivos existentes: `ls docs/audit/`
   - Buscar el mayor número: `audit-001.md`, `audit-002.md`, etc.
   - Siguiente número = último + 1 (con 3 dígitos: 001, 002, ...)

3. **Generar nombre de archivo:**
   - Formato: `docs/audit/audit-{NNN}.md`
   - Ejemplo: `docs/audit/audit-001.md`, `docs/audit/audit-002.md`

4. **Guardar contenido:** Guardar el reporte completo en el archivo generado

5. **Informar al usuario:**
   > ✅ Auditoría guardada en: `docs/audit/audit-{NNN}.md`

### Paso 5: Ejemplos de inferencia automática

| Si encuentras...                                       | Entonces...                                          |
| ------------------------------------------------------ | ---------------------------------------------------- |
| `package.json` con express                             | Backend Node.js + Express, REST API probable         |
| `Dockerfile` + `docker-compose.yml` con db, app, cache | Microservicios o app containerizada con 3+ servicios |
| `k8s/` o `helm/`                                       | Kubernetes, probablemente en cloud (GKE/EKS/AKS)     |
| `.github/workflows/`                                   | CI/CD con GitHub Actions                             |
| `serverless.yml`                                       | AWS Lambda, arquitectura serverless                  |

### Paso 6: Resumen ejecutivo

Termina con un resumen de 3 líneas:

> **Resumen:** Sistema {{tipo}} construido con {{stack principal}}. Implementado mediante {{contenedores/serverless/vm}}. {{Frase sobre estado actual}}.

---

## Ejemplo de uso

**Usuario:** "Haz una auditoría rápida para saber cómo está construido este sistema"

**Asistente:** (lee archivos sin modificar nada)

---

**📋 Auditoría Rápida: `ecommerce-backend`**

**🏗️ Arquitectura Inferida**

- Tipo: Backend API
- Patrón: Monolito modular
- API Style: REST + GraphQL (Apollo)

**🛠️ Stack Tecnológico**

- Runtime: Node.js v20
- Framework: Express + Apollo Server
- ORM: Prisma
- BD: PostgreSQL (principal) + Redis (cache)
- Infra: Docker + docker-compose (3 servicios)

**🔍 Hallazgos Clave**

- ✅ Buen uso de variables de entorno
- ✅ Health checks implementados
- ⚠️ Dockerfile no multietapa
- ⚠️ Sin tests detectados

**📊 Estado Actual**

- Uptime: 23 días
- RAM: 2.1/7.8GB
- Procesos: Node, Redis, Postgres OK

**💡 Recomendaciones**

1. Implementar Docker multietapa para reducir imagen
2. Agregar tests unitarios (Jest)
3. Considerar rate limiting

> **Resumen:** Backend Node.js/Express con PostgreSQL y Redis, containerizado con Docker, estable pero mejorable en testing y optimización de imagen.

---

## Estructura de archivos de auditoría

```
docs/
└── audit/
    ├── audit-001.md    # Primera auditoría
    ├── audit-002.md    # Segunda auditoría
    ├── audit-003.md    # Tercera auditoría
    └── ...
```

**Notas:**

- Nunca sobrescribir archivos existentes
- Numeración continua (no reutilizar números eliminados)
- Mantener todos los archivos históricos para trazabilidad

## Restricciones de seguridad

- **NUNCA** ejecutar `rm`, `mv`, `chmod`, `sudo`
- **NUNCA** modificar archivos a menos que el usuario lo autorice explícitamente
- **NUNCA** leer archivos con `PASSWORD`, `SECRET`, `KEY` en su contenido (filtrarlos)
- **SIEMPRE** pedir confirmación si se necesita acceder a rutas fuera del proyecto

---

## Variante rápida (30 segundos)

Si el usuario pide "ultra rápido", solo responde con:

| Campo               | Valor                                |
| ------------------- | ------------------------------------ |
| Tipo de proyecto    | {{backend/frontend/fullstack}}       |
| Lenguaje principal  | {{Node.js/Python/Java/Go/etc}}       |
| Framework principal | {{Express/Django/Spring/etc}}        |
| Base de datos       | {{PostgreSQL/MySQL/MongoDB/ninguna}} |
| Contenedores        | {{sí/no}}                            |

---

> **Nota:** Esta skill es **solo lectura**, no ejecuta cambios, y te da una visión clara de cómo está construido el sistema en menos de un minuto.

¿Necesitas que ajuste la profundidad o añada algún detector específico?
