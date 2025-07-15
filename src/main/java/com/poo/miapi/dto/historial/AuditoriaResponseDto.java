package com.poo.miapi.dto.historial;

import java.time.LocalDateTime;

public class AuditoriaResponseDto {
    private Long id;
    private String usuario;
    private String accion;
    private String entidad;
    private String detalle;
    private LocalDateTime fecha;

    public AuditoriaResponseDto() {
    }

    public AuditoriaResponseDto(Long id, String usuario, String accion, String entidad, String detalle,
            LocalDateTime fecha) {
        this.id = id;
        this.usuario = usuario;
        this.accion = accion;
        this.entidad = entidad;
        this.detalle = detalle;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getAccion() {
        return accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public String getDetalle() {
        return detalle;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
