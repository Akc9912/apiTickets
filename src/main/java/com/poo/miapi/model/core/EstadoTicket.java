package com.poo.miapi.model.core;

public enum EstadoTicket {
    NO_ATENDIDO("No atendido"),
    ATENDIDO("Atendido"),
    FINALIZADO("Finalizado"),
    RESUELTO("Resuelto"),
    REABIERTO("Reabierto");

    private final String label;

    EstadoTicket(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    // Método auxiliar para convertir desde texto legible
    public static EstadoTicket fromLabel(String label) {
        for (EstadoTicket estado : values()) {
            if (estado.label.equalsIgnoreCase(label)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado desconocido: " + label);
    }
}
