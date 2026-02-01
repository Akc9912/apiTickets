package com.poo.miapi.module.user.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.poo.miapi.module.user.enums.UserRole;

@JsonTypeName("DEVELOPER")
public class DeveloperResponseDto extends UserResponseDto {
    private int failures;
    private int warnings;
    private List<DeveloperIncidentResponseDto> incidentes; // Usar DTO, no entidad

    // Constructor
    public DeveloperResponseDto() {
        super();
    }

    public DeveloperResponseDto(int id, String name, String lastName, String email, UserRole role,
            boolean changePassword, boolean active, boolean blocked, int failures, int warnings) {
        super(id, name, lastName, email, role, changePassword, active, blocked);
        this.failures = failures;
        this.warnings = warnings;
    }

    public DeveloperResponseDto(int id, String name, String lastName, String email, UserRole role,
            boolean changePassword, boolean active, boolean blocked, int failures, int warnings,
            List<DeveloperIncidentResponseDto> incidentes) {
        super(id, name, lastName, email, role, changePassword, active, blocked);
        this.failures = failures;
        this.warnings = warnings;
        this.incidentes = incidentes;
    }

    // Getters y setters solo para campos adicionales

    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public int getWarnings() {
        return warnings;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }

    public List<DeveloperIncidentResponseDto> getIncidentes() {
        return incidentes;
    }

    public void setIncidentes(List<DeveloperIncidentResponseDto> incidentes) {
        this.incidentes = incidentes;
    }
}