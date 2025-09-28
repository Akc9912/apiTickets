package com.poo.miapi.model.events;

import com.poo.miapi.model.core.Usuario;
import org.springframework.context.ApplicationEvent;
import java.time.LocalDateTime;

/**
 * Evento que se dispara cuando un usuario inicia sesión
 * Permite actualizar estadísticas de actividad y sesiones
 */
public class UsuarioLoginEvent extends ApplicationEvent {

    private final Usuario usuario;
    private final LocalDateTime fechaLogin;
    private final String ipAddress;
    private final String userAgent;
    private final boolean esPrimerLoginDelDia;

    public UsuarioLoginEvent(Object source, Usuario usuario) {
        super(source);
        this.usuario = usuario;
        this.fechaLogin = LocalDateTime.now();
        this.ipAddress = "unknown";
        this.userAgent = "unknown";
        this.esPrimerLoginDelDia = false;
    }

    public UsuarioLoginEvent(Object source, Usuario usuario, String ipAddress, String userAgent, boolean esPrimerLoginDelDia) {
        super(source);
        this.usuario = usuario;
        this.fechaLogin = LocalDateTime.now();
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.esPrimerLoginDelDia = esPrimerLoginDelDia;
    }

    // Getters
    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getFechaLogin() {
        return fechaLogin;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public boolean isEsPrimerLoginDelDia() {
        return esPrimerLoginDelDia;
    }

    // Métodos de utilidad
    public String getHoraFormateada() {
        return fechaLogin.getHour() + ":" + 
               String.format("%02d", fechaLogin.getMinute());
    }

    public boolean esLoginFueraDeHorario() {
        int hora = fechaLogin.getHour();
        return hora < 8 || hora > 18; // Fuera del horario 8:00-18:00
    }

    public boolean esFinDeSemana() {
        int dayOfWeek = fechaLogin.getDayOfWeek().getValue();
        return dayOfWeek >= 6; // Sábado o Domingo
    }

    @Override
    public String toString() {
        return String.format("UsuarioLoginEvent{usuario=%s, fecha=%s, ip=%s}", 
            usuario.getEmail(), fechaLogin, ipAddress);
    }
}