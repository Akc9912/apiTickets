package com.poo.miapi.module.user.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.poo.miapi.module.user.model.Rol;

@JsonTypeName("TRABAJADOR")
public class TrabajadorResponseDto extends UsuarioResponseDto {

    public TrabajadorResponseDto() {
        super();
    }

    public TrabajadorResponseDto(int id, String nombre, String apellido, String email, Rol rol,
            boolean cambiarPass, boolean activo, boolean bloqueado) {
        super(id, nombre, apellido, email, rol, cambiarPass, activo, bloqueado);
    }
}
