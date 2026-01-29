package com.poo.miapi.model.enums;

public enum TipoIncidente {
    MARCA("Marca"),
    FALLA("Falla");

    private final String displayName;

    TipoIncidente(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return name();
    }

    @Override
    public String toString() {
        return name();
    }
}
