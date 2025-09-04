package com.poo.miapi.dto.historial;

import java.time.LocalDateTime;

public class SolicitudDevolucionResponseDto {
    private int id;
    private int idTecnico;
    private int idTicket;
    private String motivo;
    private String estado; // EstadoSolicitud como String
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaResolucion;
    private int idAdminResolutor; // Solo el id del admin
    private String comentarioResolucion;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setIdTecnico(int idTecnico) {
        this.idTecnico = idTecnico;
    }

    public int getIdTecnico() {
        return this.idTecnico;
    }
    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public int getIdTicket() {
        return this.idTicket;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivo() {
        return this.motivo;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return this.fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaResolucion() {
        return this.fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public int getIdAdminResolutor() {
        return this.idAdminResolutor;
    }

    public void setIdAdminResolutor(int idAdminResolutor) {
        this.idAdminResolutor = idAdminResolutor;
    }

    public String getComentarioResolucion() {
        return this.comentarioResolucion;
    }

    public void setComentarioResolucion(String comentarioResolucion) {
        this.comentarioResolucion = comentarioResolucion;
    }
}