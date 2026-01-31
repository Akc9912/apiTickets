package com.poo.miapi.shared.events.enums;

public enum SeveridadAuditoria {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta"),
    CRITICAL("Crítica");

    private final String displayName;

    SeveridadAuditoria(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
