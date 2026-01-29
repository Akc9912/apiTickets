package com.poo.miapi.model.events;

import com.poo.miapi.model.core.Usuario;
import org.springframework.context.ApplicationEvent;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Evento que se dispara cuando un usuario cierra sesión
 * Permite actualizar estadísticas de tiempo conectado
 */
public class UsuarioLogoutEvent extends ApplicationEvent {

    private final Usuario usuario;
    private final LocalDateTime fechaLogout;
    private final LocalDateTime fechaLogin;
    private final Duration tiempoSesion;
    private final int tiempoSesionMinutos;
    private final String motivoCierre;

    public UsuarioLogoutEvent(Object source, Usuario usuario, LocalDateTime fechaLogin) {
        super(source);
        this.usuario = usuario;
        this.fechaLogout = LocalDateTime.now();
        this.fechaLogin = fechaLogin;
        this.tiempoSesion = Duration.between(fechaLogin, fechaLogout);
        this.tiempoSesionMinutos = (int) tiempoSesion.toMinutes();
        this.motivoCierre = "Logout normal";
    }

    public UsuarioLogoutEvent(Object source, Usuario usuario, LocalDateTime fechaLogin, String motivoCierre) {
        super(source);
        this.usuario = usuario;
        this.fechaLogout = LocalDateTime.now();
        this.fechaLogin = fechaLogin;
        this.tiempoSesion = Duration.between(fechaLogin, fechaLogout);
        this.tiempoSesionMinutos = (int) tiempoSesion.toMinutes();
        this.motivoCierre = motivoCierre;
    }

    // Getters
    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getFechaLogout() {
        return fechaLogout;
    }

    public LocalDateTime getFechaLogin() {
        return fechaLogin;
    }

    public Duration getTiempoSesion() {
        return tiempoSesion;
    }

    public int getTiempoSesionMinutos() {
        return tiempoSesionMinutos;
    }

    public String getMotivoCierre() {
        return motivoCierre;
    }

    // Métodos de utilidad
    public String getTiempoSesionFormateado() {
        long horas = tiempoSesion.toHours();
        long minutos = tiempoSesion.toMinutes() % 60;

        if (horas > 0) {
            return String.format("%dh %dm", horas, minutos);
        } else {
            return String.format("%dm", minutos);
        }
    }

    public boolean esSesionCorta() {
        return tiempoSesionMinutos < 15; // Menos de 15 minutos
    }

    public boolean esSesionLarga() {
        return tiempoSesionMinutos > 480; // Más de 8 horas
    }

    public boolean esCierreAutomatico() {
        return motivoCierre != null && motivoCierre.toLowerCase().contains("timeout");
    }

    public boolean esCierreForzado() {
        return motivoCierre != null && motivoCierre.toLowerCase().contains("forzado");
    }

    @Override
    public String toString() {
        return String.format("UsuarioLogoutEvent{usuario=%s, duracion=%s, motivo=%s}",
                usuario.getEmail(), getTiempoSesionFormateado(), motivoCierre);
    }
}
