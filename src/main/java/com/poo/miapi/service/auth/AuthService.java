package com.poo.miapi.service.auth;

import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.auth.LoginRequestDto;
import com.poo.miapi.dto.auth.LoginResponseDto;
import com.poo.miapi.dto.auth.ResetPasswordDto;
import com.poo.miapi.dto.usuario.UsuarioResponseDto;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.core.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import com.poo.miapi.util.PasswordHelper;

@Service
public class AuthService {

    // Set estático para guardar los tokens válidos
    private static final Set<String> validTokens = new HashSet<>();

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Login: recibe LoginRequestDto, devuelve LoginResponseDto
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!usuario.isActivo() || usuario.isBloqueado()) {
            throw new IllegalStateException("Usuario inactivo o bloqueado");
        }

        UsuarioResponseDto usuarioDto = new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol() != null ? usuario.getRol().name() : null,
                usuario.isActivo(),
                usuario.isBloqueado());

        // Genera un token aleatorio y lo guarda en el set
        String token = UUID.randomUUID().toString();
        validTokens.add(token);
        return new LoginResponseDto(token, usuarioDto);

    }

    // Método para validar el token en endpoints protegidos
    public static boolean isTokenValid(String token) {
        return validTokens.contains(token);
    }

    // Cambiar contraseña: recibe ChangePasswordDto
    public void cambiarPassword(ChangePasswordDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }

        usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuario.setCambiarPass(false);
        usuarioRepository.save(usuario);
    }

    // Reiniciar contraseña: recibe ResetPasswordDto
    public void reiniciarPassword(ResetPasswordDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        String defaultPassword = PasswordHelper.generarPasswordPorDefecto(usuario.getApellido());
        usuario.setPassword(passwordEncoder.encode(defaultPassword));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
    }
}
