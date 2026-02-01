package com.poo.miapi.module.user.enums;

public enum DeveloperIncidentType {
    WARNING("Warning"),
    FAILURE("Failure");

    private final String displayName;

    DeveloperIncidentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}