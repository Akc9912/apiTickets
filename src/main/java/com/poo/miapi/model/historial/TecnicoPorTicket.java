package com.poo.miapi.model.historial;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;

@Entity
public class TecnicoPorTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_ticket")
    private Ticket ticket;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tecnico")
    private Tecnico tecnico;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_inicial")
    private EstadoTicket estadoInicial;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_final")
    private EstadoTicket estadoFinal;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_desasignacion")
    private LocalDateTime fechaDesasignacion;

    public TecnicoPorTicket() {
        this.fechaAsignacion = LocalDateTime.now();
    }

    public TecnicoPorTicket(Ticket ticket, Tecnico tecnico, EstadoTicket estadoInicial, EstadoTicket estadoFinal,
            String comentario) {
        this.ticket = ticket;
        this.tecnico = tecnico;
        this.estadoInicial = estadoInicial;
        this.estadoFinal = estadoFinal;
        this.comentario = comentario;
        this.fechaAsignacion = LocalDateTime.now();
        this.fechaDesasignacion = null;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public EstadoTicket getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(EstadoTicket estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public EstadoTicket getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(EstadoTicket estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public LocalDateTime getFechaDesasignacion() {
        return fechaDesasignacion;
    }

    public void setFechaDesasignacion(LocalDateTime fechaDesasignacion) {
        this.fechaDesasignacion = fechaDesasignacion;
    }

    @Override
    public String toString() {
        return "TecnicoPorTicket{id=" + id +
                ", ticket=" + (ticket != null ? ticket.getId() : null) +
                ", tecnico=" + (tecnico != null ? tecnico.getId() : null) +
                ", estadoInicial=" + estadoInicial +
                ", estadoFinal=" + estadoFinal +
                ", fechaAsignacion=" + fechaAsignacion +
                ", fechaDesasignacion=" + fechaDesasignacion +
                '}';
    }
}
