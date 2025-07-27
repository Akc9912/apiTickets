package com.poo.miapi.dto.tecnico;

import com.poo.miapi.model.historial.IncidenteTecnico.TipoIncidente;

public class IncidenteTecnicoRequestDto {
    private int idTecnico;
    private int idTicket;
    private TipoIncidente tipo;
    private String motivo;

    public IncidenteTecnicoRequestDto() {
    }

    public IncidenteTecnicoRequestDto(int idTecnico, int idTicket, TipoIncidente tipo, String motivo) {
        this.idTecnico = idTecnico;
        this.idTicket = idTicket;
        this.tipo = tipo;
        this.motivo = motivo;
    }

    // Getters y setters
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
}
