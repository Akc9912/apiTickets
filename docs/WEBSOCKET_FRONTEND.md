# WebSocket para Notificaciones - Documentación Frontend

## Configuración de Conexión WebSocket

### Endpoints disponibles:
- **Con SockJS (recomendado)**: `ws://localhost:8080/ws`
- **WebSocket nativo**: `ws://localhost:8080/ws-native`

### Canales de suscripción:

#### 1. **Notificaciones personales del usuario**
```javascript
// Suscribirse a notificaciones específicas del usuario
stompClient.subscribe('/user/' + usuarioId + '/notifications', function(notification) {
    const data = JSON.parse(notification.body);
    // Mostrar nueva notificación en la UI
    mostrarNuevaNotificacion(data);
});
```

#### 2. **Contador de notificaciones no leídas**
```javascript
// Suscribirse al contador de notificaciones no leídas
stompClient.subscribe('/user/' + usuarioId + '/counter', function(counter) {
    const data = JSON.parse(counter.body);
    // Actualizar badge de notificaciones
    actualizarContadorNotificaciones(data.noLeidas);
});
```

#### 3. **Eventos de lectura de notificaciones**
```javascript
// Suscribirse a eventos cuando se marca como leída
stompClient.subscribe('/user/' + usuarioId + '/read', function(readEvent) {
    const data = JSON.parse(readEvent.body);
    // Marcar notificación como leída en la UI
    marcarNotificacionComoLeidaEnUI(data.notificacionId);
});
```

#### 4. **Estado de conexión**
```javascript
// Suscribirse a eventos de conexión
stompClient.subscribe('/user/' + usuarioId + '/status', function(status) {
    const data = JSON.parse(status.body);
    console.log('Estado de conexión:', data);
});
```

#### 5. **Notificaciones broadcast (opcional)**
```javascript
// Suscribirse a notificaciones generales del sistema
stompClient.subscribe('/topic/notifications', function(notification) {
    const data = JSON.parse(notification.body);
    // Mostrar notificación general del sistema
    mostrarNotificacionSistema(data);
});
```

## Ejemplo de Implementación JavaScript

### Configuración inicial con SockJS y STOMP:

```javascript
// Importar librerías (usar CDN o npm)
// <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
// <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>

class NotificationWebSocketManager {
    constructor(usuarioId, baseUrl = 'http://localhost:8080') {
        this.usuarioId = usuarioId;
        this.baseUrl = baseUrl;
        this.stompClient = null;
        this.connected = false;
    }

    connect() {
        const socket = new SockJS(this.baseUrl + '/ws');
        this.stompClient = Stomp.over(socket);

        // Configurar headers de autenticación si es necesario
        const headers = {
            'Authorization': 'Bearer ' + getAuthToken() // Implementar según tu sistema de auth
        };

        this.stompClient.connect(headers,
            (frame) => this.onConnected(frame),
            (error) => this.onError(error)
        );
    }

    onConnected(frame) {
        console.log('Conectado a WebSocket:', frame);
        this.connected = true;

        // Suscribirse a todos los canales relevantes
        this.subscribeToChannels();

        // Solicitar estado inicial
        this.requestInitialStatus();
    }

    onError(error) {
        console.error('Error de WebSocket:', error);
        this.connected = false;

        // Reintento de conexión después de 5 segundos
        setTimeout(() => {
            console.log('Intentando reconectar...');
            this.connect();
        }, 5000);
    }

    subscribeToChannels() {
        // Canal de notificaciones personales
        this.stompClient.subscribe(`/user/${this.usuarioId}/notifications`, (message) => {
            const notification = JSON.parse(message.body);
            this.onNewNotification(notification);
        });

        // Canal de contador
        this.stompClient.subscribe(`/user/${this.usuarioId}/counter`, (message) => {
            const counter = JSON.parse(message.body);
            this.onCounterUpdate(counter.noLeidas);
        });

        // Canal de eventos de lectura
        this.stompClient.subscribe(`/user/${this.usuarioId}/read`, (message) => {
            const readEvent = JSON.parse(message.body);
            this.onNotificationRead(readEvent.notificacionId);
        });

        // Canal de estado
        this.stompClient.subscribe(`/user/${this.usuarioId}/status`, (message) => {
            const status = JSON.parse(message.body);
            this.onStatusUpdate(status);
        });
    }

    requestInitialStatus() {
        // Solicitar estado inicial de notificaciones
        if (this.stompClient && this.connected) {
            this.stompClient.send('/app/notifications/status', {}, '{}');
        }
    }

    markAsRead(notificacionId) {
        // Marcar notificación como leída vía WebSocket
        if (this.stompClient && this.connected) {
            this.stompClient.send('/app/notifications/read', {},
                JSON.stringify({ notificacionId: notificacionId })
            );
        }
    }

    disconnect() {
        if (this.stompClient && this.connected) {
            this.stompClient.disconnect(() => {
                console.log('Desconectado de WebSocket');
                this.connected = false;
            });
        }
    }

    // Callbacks a implementar en tu aplicación
    onNewNotification(notification) {
        console.log('Nueva notificación recibida:', notification);

        // Ejemplo de implementación
        showToast(notification.titulo, notification.mensaje, notification.severidad);
        updateNotificationList(notification);
    }

    onCounterUpdate(count) {
        console.log('Contador actualizado:', count);

        // Ejemplo de implementación
        updateNotificationBadge(count);
    }

    onNotificationRead(notificacionId) {
        console.log('Notificación leída:', notificacionId);

        // Ejemplo de implementación
        markNotificationAsReadInUI(notificacionId);
    }

    onStatusUpdate(status) {
        console.log('Estado actualizado:', status);
    }
}

// Uso del manager
const notificationManager = new NotificationWebSocketManager(123); // Reemplazar con ID real
notificationManager.connect();

// Desconectar al cerrar la página
window.addEventListener('beforeunload', () => {
    notificationManager.disconnect();
});
```

### Funciones auxiliares de ejemplo:

```javascript
function showToast(title, message, severity) {
    // Implementar toast/notification según tu framework
    // Ejemplo con toast genérico
    const toastClass = severity === 'ERROR' ? 'error' :
                     severity === 'WARNING' ? 'warning' : 'info';

    showToastMessage(title + ': ' + message, toastClass);
}

function updateNotificationBadge(count) {
    const badge = document.getElementById('notification-badge');
    if (badge) {
        badge.textContent = count > 0 ? count : '';
        badge.style.display = count > 0 ? 'inline' : 'none';
    }
}

function markNotificationAsReadInUI(notificacionId) {
    const element = document.getElementById(`notification-${notificacionId}`);
    if (element) {
        element.classList.add('read');
        element.classList.remove('unread');
    }
}

function getAuthToken() {
    // Implementar según tu sistema de autenticación
    return localStorage.getItem('authToken') ||
           sessionStorage.getItem('authToken') ||
           getCookie('authToken');
}
```

## Eventos del Sistema

### Tipos de notificaciones que se envían automáticamente:
- **TICKET_ASIGNADO**: Cuando se asigna un ticket a un técnico
- **MARCA_ASIGNADA**: Cuando se registra una marca
- **NOTIFICACION_CREADA**: Notificaciones creadas por administradores
- **CONTADOR_ACTUALIZADO**: Cuando cambia el número de no leídas

### Configuración de CORS para producción:
En el archivo `WebSocketConfig.java`, cambiar:
```java
.setAllowedOriginPatterns("*") // Desarrollo
```
por:
```java
.setAllowedOrigins("https://tudominio.com", "https://app.tudominio.com") // Producción
```

## Troubleshooting

### Problemas comunes:
1. **Conexión rechazada**: Verificar que el servidor esté ejecutándose en el puerto correcto
2. **Autenticación fallida**: Verificar que el token JWT sea válido y se esté enviando correctamente
3. **Mensajes no llegan**: Verificar que la suscripción use el `usuarioId` correcto
4. **Reconexión automática**: El manager incluye lógica de reconexión automática

### Debug:
```javascript
// Habilitar logs de STOMP para debug
stompClient.debug = function(str) {
    console.log('STOMP: ' + str);
};
```
