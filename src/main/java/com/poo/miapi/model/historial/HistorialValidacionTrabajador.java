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
    @JoinColumn(name = "id_trabajador")
    private Trabajador trabajador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_ticket")
    private Ticket ticket;

    @Column(nullable = false)
    private boolean fueResuelto;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaValidacion;

    public HistorialValidacionTrabajador() {
        this.fechaValidacion = LocalDateTime.now();
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

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public boolean isFueResuelto() {
        return fueResuelto;
    }

    public void setFueResuelto(boolean fueResuelto) {
        this.fueResuelto = fueResuelto;
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
}