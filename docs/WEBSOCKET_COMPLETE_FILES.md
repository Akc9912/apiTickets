# Archivos WebSocket - Versiones Completas

Una vez que Maven haya descargado las dependencias de WebSocket, estos archivos necesitan ser actualizados:

## 1. WebSocketConfig.java - Versión Completa

```java
package com.poo.miapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker de mensajes en memoria simple para enviar mensajes a los clientes
        config.enableSimpleBroker("/topic", "/queue", "/user");

        // Define el prefijo para los mensajes destinados a métodos anotados con @MessageMapping
        config.setApplicationDestinationPrefixes("/app");

        // Configura el prefijo para mensajes dirigidos a usuarios específicos
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra el endpoint WebSocket que será usado por los clientes para conectarse
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // En producción, especificar dominios exactos
                .withSockJS(); // Habilita fallback SockJS para navegadores que no soportan WebSocket

        // Endpoint adicional sin SockJS para clientes que soporten WebSocket nativo
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns("*");
    }
}
```

## 2. NotificacionWebSocketService.java - Versión Completa

```java
package com.poo.miapi.service.websocket;

import com.poo.miapi.dto.notificacion.NotificacionResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificacionWebSocketService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionWebSocketService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public NotificacionWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        logger.info("NotificacionWebSocketService inicializado");
    }

    // Enviar notificación a un usuario específico
    public void enviarNotificacionAUsuario(int usuarioId, NotificacionResponseDto notificacion) {
        try {
            String destination = "/user/" + usuarioId + "/notifications";
            logger.info("Enviando notificación WebSocket a usuario {} en {}", usuarioId, destination);

            messagingTemplate.convertAndSendToUser(String.valueOf(usuarioId), "/notifications", notificacion);

            logger.debug("Notificación enviada: {}", notificacion.getTitulo());
        } catch (Exception e) {
            logger.error("Error enviando notificación WebSocket a usuario {}: {}", usuarioId, e.getMessage());
        }
    }

    // Enviar notificación a todos los usuarios (broadcast)
    public void enviarNotificacionBroadcast(NotificacionResponseDto notificacion) {
        try {
            String destination = "/topic/notifications";
            logger.info("Enviando notificación broadcast a {}", destination);

            messagingTemplate.convertAndSend(destination, notificacion);

            logger.debug("Notificación broadcast enviada: {}", notificacion.getTitulo());
        } catch (Exception e) {
            logger.error("Error enviando notificación broadcast: {}", e.getMessage());
        }
    }

    // Enviar contador de notificaciones no leídas
    public void enviarContadorNotificaciones(int usuarioId, long cantidadNoLeidas) {
        try {
            String destination = "/user/" + usuarioId + "/counter";
            logger.info("Enviando contador de notificaciones a usuario {} en {}", usuarioId, destination);

            messagingTemplate.convertAndSendToUser(String.valueOf(usuarioId), "/counter",
                Map.of("noLeidas", cantidadNoLeidas, "timestamp", System.currentTimeMillis()));

            logger.debug("Contador de notificaciones enviado: {}", cantidadNoLeidas);
        } catch (Exception e) {
            logger.error("Error enviando contador a usuario {}: {}", usuarioId, e.getMessage());
        }
    }

    // Notificar cuando una notificación es leída (actualizar UI)
    public void notificarNotificacionLeida(int usuarioId, int notificacionId) {
        try {
            String destination = "/user/" + usuarioId + "/read";
            logger.info("Notificando lectura de notificación {} a usuario {}", notificacionId, usuarioId);

            messagingTemplate.convertAndSendToUser(String.valueOf(usuarioId), "/read",
                Map.of("notificacionId", notificacionId, "leida", true, "timestamp", System.currentTimeMillis()));

            logger.debug("Notificación de lectura enviada para notificación: {}", notificacionId);
        } catch (Exception e) {
            logger.error("Error notificando lectura a usuario {}: {}", usuarioId, e.getMessage());
        }
    }

    // Enviar evento de conexión exitosa
    public void enviarEventoConexion(int usuarioId) {
        try {
            String destination = "/user/" + usuarioId + "/status";
            logger.info("Enviando evento de conexión a usuario {}", usuarioId);

            messagingTemplate.convertAndSendToUser(String.valueOf(usuarioId), "/status",
                Map.of("connected", true, "timestamp", System.currentTimeMillis(), "message", "Conectado exitosamente"));

            logger.debug("Evento de conexión enviado a usuario: {}", usuarioId);
        } catch (Exception e) {
            logger.error("Error enviando evento de conexión a usuario {}: {}", usuarioId, e.getMessage());
        }
    }
}
```

## 3. NotificacionWebSocketController.java - Versión Completa

```java
package com.poo.miapi.controller.websocket;

import com.poo.miapi.service.core.UsuarioService;
import com.poo.miapi.service.notificacion.NotificacionService;
import com.poo.miapi.service.websocket.NotificacionWebSocketService;
import com.poo.miapi.model.core.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;

@Controller
public class NotificacionWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionWebSocketController.class);

    private final NotificacionService notificacionService;
    private final NotificacionWebSocketService webSocketService;
    private final UsuarioService usuarioService;

    public NotificacionWebSocketController(NotificacionService notificacionService,
                                         NotificacionWebSocketService webSocketService,
                                         UsuarioService usuarioService) {
        this.notificacionService = notificacionService;
        this.webSocketService = webSocketService;
        this.usuarioService = usuarioService;
    }

    // Endpoint para que el cliente solicite el estado de sus notificaciones
    @MessageMapping("/notifications/status")
    public void obtenerEstadoNotificaciones(Principal principal) {
        try {
            if (principal != null) {
                Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
                if (usuario != null) {
                    long cantidadNoLeidas = notificacionService.contarNoLeidas(usuario.getId());
                    webSocketService.enviarContadorNotificaciones(usuario.getId(), cantidadNoLeidas);

                    logger.info("Estado de notificaciones enviado a usuario: {} (No leídas: {})",
                              usuario.getId(), cantidadNoLeidas);
                }
            }
        } catch (Exception e) {
            logger.error("Error obteniendo estado de notificaciones: {}", e.getMessage());
        }
    }

    // Endpoint para marcar notificación como leída via WebSocket
    @MessageMapping("/notifications/read")
    public void marcarComoLeida(Map<String, Object> payload, Principal principal) {
        try {
            if (principal != null && payload.containsKey("notificacionId")) {
                Usuario usuario = usuarioService.buscarPorEmail(principal.getName());
                if (usuario != null) {
                    int notificacionId = (Integer) payload.get("notificacionId");

                    // Marcar como leída
                    notificacionService.marcarComoLeida(notificacionId, usuario.getId());

                    // Notificar al cliente que se actualizó
                    webSocketService.notificarNotificacionLeida(usuario.getId(), notificacionId);

                    // Enviar nuevo contador
                    long cantidadNoLeidas = notificacionService.contarNoLeidas(usuario.getId());
                    webSocketService.enviarContadorNotificaciones(usuario.getId(), cantidadNoLeidas);

                    logger.info("Notificación {} marcada como leída por usuario {} via WebSocket",
                              notificacionId, usuario.getId());
                }
            }
        } catch (Exception e) {
            logger.error("Error marcando notificación como leída via WebSocket: {}", e.getMessage());
        }
    }

    // Listener para conexiones WebSocket
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent connectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(connectEvent.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal user = headerAccessor.getUser();

        logger.info("Nueva conexión WebSocket establecida. SessionId: {}", sessionId);

        if (user != null) {
            Usuario usuario = usuarioService.buscarPorEmail(user.getName());
            if (usuario != null) {
                webSocketService.enviarEventoConexion(usuario.getId());
                logger.info("Usuario {} conectado via WebSocket", usuario.getId());
            }
        }
    }

    // Listener para desconexiones WebSocket
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent disconnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(disconnectEvent.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Principal user = headerAccessor.getUser();

        logger.info("Conexión WebSocket desconectada. SessionId: {}", sessionId);

        if (user != null) {
            Usuario usuario = usuarioService.buscarPorEmail(user.getName());
            if (usuario != null) {
                logger.info("Usuario {} desconectado de WebSocket", usuario.getId());
            }
        }
    }
}
```

## Instrucciones para Activar WebSocket

1. **Recargar Maven**: Ejecutar `mvn clean install` o recargar dependencias en IDE
2. **Descomentar imports**: Actualizar los imports en cada archivo
3. **Descomentar anotaciones**: Habilitar `@MessageMapping`, `@EventListener`, etc.
4. **Actualizar constructor**: Agregar `SimpMessagingTemplate` al service
5. **Probar conexión**: Usar el código JavaScript de la documentación

## Endpoints WebSocket Finales

- **Conexión**: `ws://localhost:8080/ws` (con SockJS)
- **Conexión nativa**: `ws://localhost:8080/ws-native`
- **Solicitar estado**: `/app/notifications/status`
- **Marcar como leída**: `/app/notifications/read`

## Canales de Suscripción

- **Notificaciones**: `/user/{usuarioId}/notifications`
- **Contador**: `/user/{usuarioId}/counter`
- **Lectura**: `/user/{usuarioId}/read`
- **Estado**: `/user/{usuarioId}/status`
- **Broadcast**: `/topic/notifications`
