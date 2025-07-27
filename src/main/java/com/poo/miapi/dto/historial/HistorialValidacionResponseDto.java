package com.poo.miapi.dto.historial;

import java.time.LocalDateTime;

public class HistorialValidacionResponseDto {
    private int id;
    private int idTrabajador;
    private int idTicket;
    private boolean fueResuelto;
    private String comentario;
    private LocalDateTime fechaRegistro;

    public HistorialValidacionResponseDto() {
    }

    public HistorialValidacionResponseDto(int id, int idTrabajador, int idTicket, boolean fueResuelto,
            String comentario, LocalDateTime fechaRegistro) {
        this.id = id;
        this.idTrabajador = idTrabajador;
        this.idTicket = idTicket;
        this.fueResuelto = fueResuelto;
        this.comentario = comentario;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
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