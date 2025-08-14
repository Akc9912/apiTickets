package com.poo.miapi.model.historial;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;

@Entity
@Table(name = "historial_validacion")
public class HistorialValidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario_validador")
    private Usuario usuarioValidador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_ticket")
    private Ticket ticket;

    @Column(nullable = false)
    private boolean fueAprobado;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaValidacion;

    public HistorialValidacion() {
        this.fechaValidacion = LocalDateTime.now();
    }

    public HistorialValidacion(Usuario usuarioValidador, Ticket ticket, boolean fueAprobado, String comentario) {
        this.usuarioValidador = usuarioValidador;
        this.ticket = ticket;
        this.fueAprobado = fueAprobado;
        this.comentario = comentario;
        this.fechaValidacion = LocalDateTime.now();
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public Usuario getUsuarioValidador() {
        return usuarioValidador;
    }

    public void setUsuarioValidador(Usuario usuarioValidador) {
        this.usuarioValidador = usuarioValidador;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public boolean isFueAprobado() {
        return fueAprobado;
    }

    public void setFueAprobado(boolean fueAprobado) {
        this.fueAprobado = fueAprobado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaValidacion;
    }
}