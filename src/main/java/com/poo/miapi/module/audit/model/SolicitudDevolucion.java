package com.poo.miapi.model.historial;

import jakarta.persistence.*;
import java.io.Serializable;

import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.enums.EstadoSolicitud;
import com.poo.miapi.model.core.Admin;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud_devolucion")
public class SolicitudDevolucion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tecnico")
    private Tecnico tecnico;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_ticket")
    private Ticket ticket;
    private String motivo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;
    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;
    @ManyToOne
    @JoinColumn(name = "id_admin_resolutor")
    private Admin adminResolutor;
    @Column(name = "comentario_resolucion", columnDefinition = "TEXT")
    private String comentarioResolucion;

    

    public SolicitudDevolucion() {
        this.estado = EstadoSolicitud.PENDIENTE;
        this.fechaSolicitud = LocalDateTime.now();
    }

    public SolicitudDevolucion(Tecnico tecnico, Ticket ticket, String motivo) {
        this();
        this.tecnico = tecnico;
        this.ticket = ticket;
        this.motivo = motivo;
        this.estado = EstadoSolicitud.PENDIENTE;
        this.fechaSolicitud = LocalDateTime.now();
    }

    // Getters y Setters

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Tecnico getTecnico() {
        return this.tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getMotivo() {
        return this.motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public Admin getAdminResolutor() {
        return adminResolutor;
    }

    public void setAdminResolutor(Admin adminResolutor) {
        this.adminResolutor = adminResolutor;
    }

    public String getComentarioResolucion() {
        return comentarioResolucion;
    }

    public void setComentarioResolucion(String comentarioResolucion) {
        this.comentarioResolucion = comentarioResolucion;
    }

    // Métodos útiles

    public boolean estaPendiente() {
        return estado == EstadoSolicitud.PENDIENTE;
    }

    public boolean fueAprobada() {
        return estado == EstadoSolicitud.APROBADO;
    }

    public boolean fueRechazada() {
        return estado == EstadoSolicitud.RECHAZADO;
    }

    public void aprobar(Admin admin, String comentario) {
        this.estado = EstadoSolicitud.APROBADO;
        this.fechaResolucion = LocalDateTime.now();
        this.adminResolutor = admin;
        this.comentarioResolucion = comentario;
    }

    public void rechazar(Admin admin, String comentario) {
        this.estado = EstadoSolicitud.RECHAZADO;
        this.fechaResolucion = LocalDateTime.now();
        this.adminResolutor = admin;
        this.comentarioResolucion = comentario;
    }
}