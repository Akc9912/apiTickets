package com.poo.miapi.dto.usuarios;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TECNICO")
public class TecnicoResponseDto extends UsuarioResponseDto {
    private int fallas;
    private int marcas;

    public TecnicoResponseDto() {
        super();
    }

    public TecnicoResponseDto(int id, String nombre, String apellido, String email, com.poo.miapi.model.enums.Rol rol, boolean cambiarPass, boolean activo, boolean bloqueado, int fallas, int marcas) {
        super(id, nombre, apellido, email, rol, cambiarPass, activo, bloqueado);
        this.fallas = fallas;
        this.marcas = marcas;
    }

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

}
