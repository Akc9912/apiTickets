# Configuración de Variables de Entorno - API Tickets

## ✅ Estado Actual: CONFIGURADO Y FUNCIONANDO

Tu API está completamente configurada y corriendo en:

- **URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

## 🚀 Inicio Rápido

### Opción 1: Script Automático (RECOMENDADO)

```bash
# Ejecuta el script que carga automáticamente las variables del .env
.\start_api.bat
```

### Opción 2: Manual con PowerShell

```bash
$env:DB_URL="jdbc:mysql://localhost:3306/apiticket?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USER="root"
$env:DB_PASS="password"
./mvnw spring-boot:run
```

## Instrucciones de Configuración (COMPLETADAS)

### 1. Configuración Inicial

```bash
# Copia el archivo de ejemplo
cp .env.example .env

# Edita el archivo .env con tus valores reales
nano .env  # o usa tu editor preferido
```

### 2. Variables Importantes a Configurar

#### Base de Datos

- `DB_PASS`: Cambia por tu contraseña real de MySQL
- `DB_USER`: Usuario de base de datos (por defecto: root)
- `DB_URL`: URL de conexión a la base de datos

#### Seguridad JWT

- `JWT_SECRET`: **IMPORTANTE** - Cambia por una clave secreta fuerte (mínimo 256 bits)
- `JWT_EXPIRATION_MS`: Tiempo de expiración del token en milisegundos (por defecto: 10 horas)

#### Aplicación

- `APP_DEFAULT_PASSWORD`: Contraseña por defecto para nuevos usuarios
- `SERVER_PORT`: Puerto donde correrá la aplicación (por defecto: 8080)

### 3. Configuración para Diferentes Entornos

#### Desarrollo

```properties
SPRING_PROFILES_ACTIVE=mysql
DEBUG_MODE=true
SPRING_JPA_SHOW_SQL=true
LOGGING_LEVEL_COM_POO_MIAPI=DEBUG
```

#### Producción

```properties
SPRING_PROFILES_ACTIVE=mysql
DEBUG_MODE=false
SPRING_JPA_SHOW_SQL=false
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_COM_POO_MIAPI=INFO
```

### 4. Seguridad

**⚠️ IMPORTANTE:**

- Nunca commitees el archivo `.env` al repositorio
- El archivo `.env` está incluido en `.gitignore`
- Usa `.env.example` como plantilla para otros desarrolladores
- Cambia las claves secretas en producción

### 5. Extensiones Futuras

El archivo `.env` incluye configuraciones preparadas para:

- Sistema de correos electrónicos
- Cache con Redis
- Configuración CORS
- Subida de archivos

Estas pueden ser habilitadas cuando se implementen las funcionalidades correspondientes.
