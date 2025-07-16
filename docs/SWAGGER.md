# 📚 Documentación API con Swagger

## 🚀 Acceso a Swagger UI

Una vez que ejecutes la aplicación, puedes acceder a la documentación interactiva:

```
🌐 Swagger UI: http://localhost:8080/swagger-ui.html
📄 OpenAPI JSON: http://localhost:8080/api-docs
```

## 🔐 Cómo Autenticarse en Swagger

### 1. **Obtener Token JWT**

```bash
# Hacer login para obtener token
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "superadmin@sistema.com",
  "password": "secret"
}
```

### 2. **Configurar Autorización en Swagger**

1. Copia el token JWT de la respuesta
2. En Swagger UI, haz clic en el botón **"Authorize"** 🔒
3. Ingresa: `Bearer TU_TOKEN_JWT_AQUI`
4. Haz clic en **"Authorize"**

### 3. **Probar Endpoints**

Ahora puedes probar cualquier endpoint directamente desde Swagger UI.

## 📋 Endpoints Documentados

### 🔐 **Autenticación** (`/api/auth`)

- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/cambiar-password` - Cambiar contraseña
- `POST /api/auth/reiniciar-password` - Reiniciar contraseña

### 👑 **SuperAdmin** (`/api/superadmin`)

- Gestión completa de usuarios
- Promoción/degradación de roles
- Estadísticas del sistema
- Gestión avanzada de tickets

### 👨‍💼 **Admin** (`/api/admin`)

- Gestión de usuarios no-admin
- Administración de tickets
- Estadísticas limitadas

### 🎫 **Tickets** (`/api/tickets`)

- CRUD de tickets
- Asignación de técnicos
- Cambios de estado

### 🔔 **Notificaciones** (`/api/notificaciones`)

- Listar notificaciones
- Marcar como leídas

## 🎯 **Características de la Documentación**

### ✨ **Funcionalidades Incluidas:**

- 📖 **Descripciones detalladas** de cada endpoint
- 🔒 **Autenticación JWT integrada**
- 📝 **Ejemplos de requests y responses**
- 🏷️ **Agrupación por funcionalidad**
- ⚡ **Pruebas en vivo** desde la interfaz
- 📊 **Modelos de datos documentados**

### 🛡️ **Seguridad:**

- Todos los endpoints requieren autenticación JWT (excepto login)
- Roles claramente definidos en la documentación
- Ejemplos de respuestas de error

## 🔧 **Configuración Técnica**

### **Dependencias (ya incluidas):**

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### **Configuración en application.properties:**

```properties
# Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.packages-to-scan=com.poo.miapi.controller
```

## 📱 **Uso en Desarrollo**

### **Para Desarrolladores:**

1. **Explorar API**: Ver todos los endpoints disponibles
2. **Probar funcionalidad**: Ejecutar requests sin Postman
3. **Validar responses**: Verificar estructura de datos
4. **Documentar cambios**: Los cambios se reflejan automáticamente

### **Para Testing:**

- Usar Swagger UI para pruebas manuales rápidas
- Verificar validaciones de entrada
- Comprobar códigos de respuesta HTTP
- Testear diferentes roles de usuario

## 🚀 **Próximos Pasos**

### **Mejoras Recomendadas:**

1. **Agregar más ejemplos** a los DTOs
2. **Documentar todos los controladores** con anotaciones
3. **Incluir schemas de error** detallados
4. **Agregar descripciones** a los modelos de datos

### **Para Producción:**

```properties
# Deshabilitar Swagger en producción (opcional)
springdoc.swagger-ui.enabled=false
springdoc.api-docs.enabled=false
```

---

## 💡 **Tips de Uso**

- 🔄 **Auto-refresh**: Los cambios en código se reflejan automáticamente
- 🎨 **Personalización**: Puedes customizar colores y estilos
- 📋 **Export**: Puedes exportar la especificación OpenAPI
- 🔗 **Sharing**: Comparte la URL de Swagger con tu equipo

**¡Tu API ahora tiene documentación profesional e interactiva!** 🎉
