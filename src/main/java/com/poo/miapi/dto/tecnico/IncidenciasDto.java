package com.poo.miapi.dto.tecnico;

public class IncidenciasDto {
    private int fallas;
    private int marcas;

    public IncidenciasDto() {}

    public IncidenciasDto(int fallas, int marcas) {
        this.fallas = fallas;
        this.marcas = marcas;
    }

    public int getFallas() {
        return fallas;
    }

    public void setFallas(int fallas) {
        this.fallas = fallas;
    }

    public int getMarcas() {
        return marcas;
    }

    public void setMarcas(int marcas) {
        this.marcas = marcas;
    }
}
