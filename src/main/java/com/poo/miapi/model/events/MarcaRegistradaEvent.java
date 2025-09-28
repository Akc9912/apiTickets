package com.poo.miapi.model.events;

import com.poo.miapi.model.core.Usuario;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que se dispara cuando se registra una marca para un usuario
 */
public class MarcaRegistradaEvent extends ApplicationEvent {

    private final Usuario usuario;
    private final Usuario registradoPor;
    private final String motivo;
    private final String tipoMarca;
    private final Object entidadRelacionada; // Puede ser Ticket u otra entidad

    public MarcaRegistradaEvent(Object source, Usuario usuario, Usuario registradoPor,
            String motivo, String tipoMarca, Object entidadRelacionada) {
        super(source);
        this.usuario = usuario;
        this.registradoPor = registradoPor;
        this.motivo = motivo;
        this.tipoMarca = tipoMarca;
        this.entidadRelacionada = entidadRelacionada;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Usuario getRegistradoPor() {
        return registradoPor;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getTipoMarca() {
        return tipoMarca;
    }

    public Object getEntidadRelacionada() {
        return entidadRelacionada;
    }
}
