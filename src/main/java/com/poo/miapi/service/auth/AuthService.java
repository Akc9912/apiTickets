package com.poo.miapi.service.auth;

import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.auth.LoginRequestDto;
import com.poo.miapi.dto.auth.LoginResponseDto;
import com.poo.miapi.dto.auth.ResetPasswordDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.service.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Login: recibe LoginRequestDto, devuelve LoginResponseDto
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!usuario.isActivo() || usuario.isBloqueado()) {
            throw new IllegalStateException("Usuario inactivo o bloqueado");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(usuario);
        UsuarioResponseDto usuarioDto = new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol() != null ? usuario.getRol().name() : null,
                usuario.isActivo(),
                usuario.isBloqueado());

        return new LoginResponseDto(token, usuarioDto);
    }

    // Cambiar contraseña: recibe ChangePasswordDto
    public void cambiarPassword(ChangePasswordDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior");
        }

        usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuario.setCambiarPass(false);
        usuarioRepository.save(usuario);
    }

    // Reiniciar contraseña: recibe ResetPasswordDto
    public void reiniciarPassword(ResetPasswordDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
    }
}
