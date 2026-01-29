package com.poo.miapi.model.enums;

public enum PeriodoTipo {
    DIARIO("Diario"),
    SEMANAL("Semanal"),
    MENSUAL("Mensual"),
    TRIMESTRAL("Trimestral"),
    ANUAL("Anual");

    private final String descripcion;

    PeriodoTipo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}