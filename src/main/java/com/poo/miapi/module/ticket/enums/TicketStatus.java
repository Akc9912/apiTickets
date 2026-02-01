package com.poo.miapi.module.ticket.enums;

public enum TicketStatus {
    NOT_ATTENDED("Not Attended"),
    ATTENDED("Attended"),
    RESOLVED("Resolved"),
    FINALIZED("Finalized"),
    REOPENED("Reopened");

    private final String displayName;

    TicketStatus(String displayName) {
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
        return this == NOT_ATTENDED || this == REOPENED;
    }

    // Verifica si el ticket está en proceso activo
    public boolean isActive() {
        return this == NOT_ATTENDED || this == ATTENDED || this == RESOLVED || this == REOPENED;
    }

    // Verifica si el ticket está cerrado
    public boolean isClosed() {
        return this == FINALIZED;
    }

    // Verifica si el estado permite evaluación por parte del trabajador
    public boolean canBeEvaluated() {
        return this == RESOLVED;
    }
}
