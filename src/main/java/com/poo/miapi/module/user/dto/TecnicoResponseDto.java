package com.poo.miapi.module.user.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.poo.miapi.module.user.model.Rol;

@JsonTypeName("TECNICO")
public class TecnicoResponseDto extends UsuarioResponseDto {
    private int fallas;
    private int marcas;
    private List<IncidenteTecnicoResponseDto> incidentes; // Usar DTO, no entidad

    // Constructor
    public TecnicoResponseDto() {
        super();
    }

    public TecnicoResponseDto(int id, String nombre, String apellido, String email, Rol rol,
                    boolean cambiarPass, boolean activo, boolean bloqueado, int fallas, int marcas) {
        super(id, nombre, apellido, email, rol, cambiarPass, activo, bloqueado);
        this.fallas = fallas;
        this.marcas = marcas;
    }

    public TecnicoResponseDto(int id, String nombre, String apellido, String email, Rol rol,
            boolean cambiarPass, boolean activo, boolean bloqueado, int fallas, int marcas,
            List<IncidenteTecnicoResponseDto> incidentes) {
        super(id, nombre, apellido, email, rol, cambiarPass, activo, bloqueado);
        this.fallas = fallas;
        this.marcas = marcas;
        this.incidentes = incidentes;
    }

    // Getters y setters solo para campos adicionales

    public int getFallas() {
        return fallas;
    }

    public void setFallas(int fallas) {
        this.fallas = fallas;
    }

    public int getMarcas() {
        return marcas;
    }

    public void setMarcas(int marcas) {
        this.marcas = marcas;
    }

    public List<IncidenteTecnicoResponseDto> getIncidentes() {
        return incidentes;
    }

    public void setIncidentes(List<IncidenteTecnicoResponseDto> incidentes) {
        this.incidentes = incidentes;
    }
}