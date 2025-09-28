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
        // Habilita un broker de mensajes en memoria simple para enviar mensajes a los
        // clientes
        config.enableSimpleBroker("/topic", "/queue", "/user");

        // Define el prefijo para los mensajes destinados a métodos anotados con
        // @MessageMapping
        config.setApplicationDestinationPrefixes("/app");

        // Configura el prefijo para mensajes dirigidos a usuarios específicos
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra el endpoint WebSocket que será usado por los clientes para
        // conectarse
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // En producción, especificar dominios exactos
                .withSockJS(); // Habilita fallback SockJS para navegadores que no soportan WebSocket

        // Endpoint adicional sin SockJS para clientes que soporten WebSocket nativo
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns("*");
    }
}
