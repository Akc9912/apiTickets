package com.poo.miapi.dto.historial;

import java.time.LocalDateTime;

public class HistorialValidacionResponseDto {
    private Long id;
    private Long idTrabajador;
    private Long idTicket;
    private boolean fueResuelto;
    private String comentario;
    private LocalDateTime fechaRegistro;

    public HistorialValidacionResponseDto() {
    }

    public HistorialValidacionResponseDto(Long id, Long idTrabajador, Long idTicket, boolean fueResuelto,
            String comentario, LocalDateTime fechaRegistro) {
        this.id = id;
        this.idTrabajador = idTrabajador;
        this.idTicket = idTicket;
        this.fueResuelto = fueResuelto;
        this.comentario = comentario;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(Long idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public Long getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Long idTicket) {
        this.idTicket = idTicket;
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}