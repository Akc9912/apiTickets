package com.poo.miapi.model.historial;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Trabajador;

@Entity
public class HistorialValidacionTrabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Trabajador trabajador;

    @ManyToOne(optional = false)
    private Ticket ticket;

    private boolean fueResuelto;

    private String comentario;

    private LocalDateTime fechaValidacion;

    public HistorialValidacionTrabajador() {
    }

    public HistorialValidacionTrabajador(Trabajador trabajador, Ticket ticket, boolean fueResuelto, String comentario) {
        this.trabajador = trabajador;
        this.ticket = ticket;
        this.fueResuelto = fueResuelto;
        this.comentario = comentario;
        this.fechaValidacion = LocalDateTime.now();
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public boolean isFueResuelto() {
        return fueResuelto;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void setFueResuelto(boolean fueResuelto) {
        this.fueResuelto = fueResuelto;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }
}