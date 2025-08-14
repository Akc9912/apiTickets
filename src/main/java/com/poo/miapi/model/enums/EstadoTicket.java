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

    public String getDisplayName() {
        return displayName;
    }

    public String getLabel() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    //Verifica si el estado permite asignación de técnico
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

    //  Verifica si el estado permite evaluación por parte del trabajador
    
    public boolean canBeEvaluated() {
        return this == RESUELTO;
    }

    // Obtiene el estado por su nombre (case-insensitive)   
    public static EstadoTicket fromString(String estadoName) {
        if (estadoName == null || estadoName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estado no puede estar vacío");
        }
        
        try {
            return EstadoTicket.valueOf(estadoName.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + estadoName + ". Estados válidos: " + java.util.Arrays.toString(values()));
        }
    }

    // Método auxiliar para convertir desde texto legible
    public static EstadoTicket fromLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("El label del estado no puede estar vacío");
        }
        
        for (EstadoTicket estado : values()) {
            if (estado.displayName.equalsIgnoreCase(label.trim())) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado desconocido: " + label + ". Estados disponibles: " + java.util.Arrays.toString(getAllDisplayNames()));
    }

    // Obtiene todos los nombres para mostrar
    public static String[] getAllDisplayNames() {
        return java.util.Arrays.stream(values())
                .map(EstadoTicket::getDisplayName)
                .toArray(String[]::new);
    }

    // Método auxiliar para obtener el nombre del enum
    public String getName() {
        return name();
    }
}
