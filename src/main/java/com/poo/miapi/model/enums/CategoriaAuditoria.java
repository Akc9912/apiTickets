package com.poo.miapi.model.enums;

public enum CategoriaAuditoria {
    SECURITY("Seguridad"),
    DATA("Datos"),
    SYSTEM("Sistema"),
    BUSINESS("Negocio");

    private final String displayName;

    CategoriaAuditoria(String displayName) {
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
