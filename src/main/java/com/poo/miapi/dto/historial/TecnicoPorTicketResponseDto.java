package com.poo.miapi.dto.historial;

import java.time.LocalDateTime;
import com.poo.miapi.model.enums.EstadoTicket;

public class TecnicoPorTicketResponseDto {
    private int id;
    private int idTicket;
    private int idTecnico;
    private EstadoTicket estadoInicial;
    private EstadoTicket estadoFinal;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaDesasignacion;
    private String comentario;

    public TecnicoPorTicketResponseDto() {
    }

    public TecnicoPorTicketResponseDto(int id, int idTicket, int idTecnico, EstadoTicket estadoInicial,
            EstadoTicket estadoFinal, LocalDateTime fechaAsignacion,
            LocalDateTime fechaDesasignacion, String comentario) {
        this.id = id;
        this.idTicket = idTicket;
        this.idTecnico = idTecnico;
        this.estadoInicial = estadoInicial;
        this.estadoFinal = estadoFinal;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaDesasignacion = fechaDesasignacion;
        this.comentario = comentario;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public int getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(int idTecnico) {
        this.idTecnico = idTecnico;
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

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}