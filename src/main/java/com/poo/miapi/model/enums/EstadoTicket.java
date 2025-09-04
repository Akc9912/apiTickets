package com.poo.miapi.model.enums;

/**
 * Enum que define los estados de un ticket en el sistema
 */
public enum EstadoTicket {
    NO_ATENDIDO("No atendido"),
    ATENDIDO("Atendido"),
    RESUELTO("Resuelto"),
    FINALIZADO("Finalizado"),
    REABIERTO("Reabierto");

    private final String displayName;

    EstadoTicket(String displayName) {
        this.displayName = displayName;
    }

    // Nombre estándar para el front (mayúsculas, igual al nombre del enum)
    public String getName() {
        return name();
    }

    // Nombre legible para mostrar en UI
    public String getDisplayName() {
        return displayName;
    }

    // Valor original del enum (para compatibilidad)
    public String getValue() {
        return name();
    }

    @Override
    public String toString() {
        return name();
    }

    // Verifica si el estado permite asignación de técnico
    public boolean canBeAssigned() {
        return this == NO_ATENDIDO || this == REABIERTO;
    }

    // Verifica si el ticket está en proceso activo
    public boolean isActive() {
        return this == NO_ATENDIDO || this == ATENDIDO || this == RESUELTO || this == REABIERTO;
    }

    // Verifica si el ticket está cerrado
    public boolean isClosed() {
        return this == FINALIZADO;
    }

    // Verifica si el estado permite evaluación por parte del trabajador
    public boolean canBeEvaluated() {
        return this == RESUELTO;
    }
}
