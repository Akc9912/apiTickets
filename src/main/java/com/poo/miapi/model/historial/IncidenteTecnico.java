package com.poo.miapi.model.historial;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;

@Entity
public class IncidenteTecnico {

    public enum TipoIncidente {
        MARCA,
        FALLA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tecnico")
    private Tecnico tecnico;

    @ManyToOne(optional = true) // Cambia a false si el ticket siempre es obligatorio
    @JoinColumn(name = "id_ticket")
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoIncidente tipo;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    public IncidenteTecnico() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public IncidenteTecnico(Tecnico tecnico, Ticket ticket, TipoIncidente tipo, String motivo) {
        this.tecnico = tecnico;
        this.ticket = ticket;
        this.tipo = tipo;
        this.motivo = motivo;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public TipoIncidente getTipo() {
        return tipo;
    }

    public void setTipo(TipoIncidente tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "IncidenteTecnico{id=" + id +
                ", tecnico=" + (tecnico != null ? tecnico.getId() : null) +
                ", ticket=" + (ticket != null ? ticket.getId() : null) +
                ", tipo=" + tipo +
                ", motivo='" + motivo + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
