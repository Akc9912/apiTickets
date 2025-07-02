package com.poo.miapi.model;

public enum EstadoTicket {
    NO_ATENDIDO("No atendido"),
    ATENDIDO("Atendido"),
    RESUELTO("Resuelto"),
    FINALIZADO("Finalizado"),
    REABIERTO("Reabierto");

    private final String label;

    EstadoTicket(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
