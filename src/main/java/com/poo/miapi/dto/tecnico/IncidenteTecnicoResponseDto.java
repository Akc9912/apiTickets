package com.poo.miapi.dto.tecnico;

import java.time.LocalDateTime;
import com.poo.miapi.model.historial.IncidenteTecnico.TipoIncidente;

public class IncidenteTecnicoResponseDto {
    private int id;
    private int idTecnico;
    private int idTicket;
    private TipoIncidente tipo;
    private String motivo;
    private LocalDateTime fechaRegistro;

    public IncidenteTecnicoResponseDto() {
    }

    public IncidenteTecnicoResponseDto(int id, int idTecnico, int idTicket, TipoIncidente tipo, String motivo,
            LocalDateTime fechaRegistro) {
        this.id = id;
        this.idTecnico = idTecnico;
        this.idTicket = idTicket;
        this.tipo = tipo;
        this.motivo = motivo;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(int idTecnico) {
        this.idTecnico = idTecnico;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
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
}
