package com.poo.miapi.dto.usuarios;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("TRABAJADOR")
public class TrabajadorResponseDto extends UsuarioResponseDto {
	public TrabajadorResponseDto() {
		super();
	}

	public TrabajadorResponseDto(int id, String nombre, String apellido, String email, com.poo.miapi.model.enums.Rol rol, boolean cambiarPass, boolean activo, boolean bloqueado) {
		super(id, nombre, apellido, email, rol, cambiarPass, activo, bloqueado);
	}
}
