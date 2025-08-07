# 🗄️ Setup de Base de Datos - Sistema de Tickets

## 📋 Un Solo Script - Todo Incluido

### **create_database.sql**

Este script único hace **TODO** lo necesario:

- ✅ Crea la base de datos `apiticket`
- ✅ Crea toda la estructura de tablas
- ✅ Configura enum `Rol` correctamente
- ✅ Inserta datos iniciales
- ✅ Optimiza con índices
- ✅ Compatible con Spring Boot 3.5.3

## 🚀 Instalación

### **Paso 1: Ejecutar script**

```bash
mysql -u root -p < create_database.sql
```

### **Paso 2: Iniciar aplicación**

```bash
./mvnw spring-boot:run
```

**¡Eso es todo!** 🎉

## 🔐 Credenciales

El SuperAdmin se crea automáticamente:

- **Email**: `superadmin@sistema.com`
- **Password**: `secret`

⚠️ **CAMBIAR** inmediatamente después del primer login.

## 🔄 Re-instalación

Si necesitas limpiar y empezar de nuevo:

```bash
# El mismo script limpia y recrea todo
mysql -u root -p < create_database.sql
./mvnw spring-boot:run
```

## 📊 Estructura

- **Usuarios**: SuperAdmin, Admin, Técnico, Trabajador
- **Tickets**: Con estados y asignaciones
- **Auditoría**: Log completo del sistema
- **Notificaciones**: Sistema de mensajes
- **Estadísticas**: Métricas del sistema

**Simple, limpio y funcional** ✨
