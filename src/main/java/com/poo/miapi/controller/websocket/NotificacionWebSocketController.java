package com.poo.miapi.controller.websocket;

import com.poo.miapi.service.core.UsuarioService;
import com.poo.miapi.service.notificacion.NotificacionService;
import com.poo.miapi.service.websocket.NotificacionWebSocketService;
import com.poo.miapi.model.core.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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
    // @MessageMapping("/notifications/status")
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
    // @MessageMapping("/notifications/read")
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
    // @EventListener
    public void handleWebSocketConnectListener(Object connectEvent) {
        // TODO: Implementar cuando Maven cargue las dependencias
        // StompHeaderAccessor headerAccessor =
        // StompHeaderAccessor.wrap(connectEvent.getMessage());
        // String sessionId = headerAccessor.getSessionId();
        // Principal user = headerAccessor.getUser();

        logger.info("Nueva conexión WebSocket establecida");

        // if (user != null) {
        // Usuario usuario = usuarioService.buscarPorEmail(user.getName());
        // if (usuario != null) {
        // webSocketService.enviarEventoConexion(usuario.getId());
        // logger.info("Usuario {} conectado via WebSocket", usuario.getId());
        // }
        // }
    }

    // Listener para desconexiones WebSocket
    // @EventListener
    public void handleWebSocketDisconnectListener(Object disconnectEvent) {
        // TODO: Implementar cuando Maven cargue las dependencias
        // StompHeaderAccessor headerAccessor =
        // StompHeaderAccessor.wrap(disconnectEvent.getMessage());
        // String sessionId = headerAccessor.getSessionId();
        // Principal user = headerAccessor.getUser();

        logger.info("Conexión WebSocket desconectada");

        // if (user != null) {
        // Usuario usuario = usuarioService.buscarPorEmail(user.getName());
        // if (usuario != null) {
        // logger.info("Usuario {} desconectado de WebSocket", usuario.getId());
        // }
        // }
    }
}
