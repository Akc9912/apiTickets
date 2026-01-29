package com.poo.miapi.dto.usuarios;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ADMIN")
public class AdminResponseDto extends UsuarioResponseDto {

	public AdminResponseDto() {
		super();
	}

	public AdminResponseDto(int id, String nombre, String apellido, String email, com.poo.miapi.model.enums.Rol rol, boolean cambiarPass, boolean activo, boolean bloqueado) {
		super(id, nombre, apellido, email, rol, cambiarPass, activo, bloqueado);
	}
}
