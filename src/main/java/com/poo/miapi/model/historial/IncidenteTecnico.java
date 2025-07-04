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
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tecnico")
    private Tecnico tecnico;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_ticket")
    private Ticket ticket; // puede ser null si es una falla general

    @Enumerated(EnumType.STRING)
    private TipoIncidente tipo;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    private LocalDateTime fecha;

    public IncidenteTecnico() {
        this.fecha = LocalDateTime.now();
    }

    public IncidenteTecnico(Tecnico tecnico, Ticket ticket, TipoIncidente tipo, String motivo) {
        this.tecnico = tecnico;
        this.ticket = ticket;
        this.tipo = tipo;
        this.motivo = motivo;
        this.fecha = LocalDateTime.now();
    }

    // Getters y setters

    public Long getId() {
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
