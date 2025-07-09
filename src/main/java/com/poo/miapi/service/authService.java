package com.poo.miapi.service;

import com.poo.miapi.dto.CambioPasswordDto;
import com.poo.miapi.dto.LoginRequestDto;
import com.poo.miapi.dto.LoginResponseDto;
import com.poo.miapi.dto.ReinicioPasswordDto;
import com.poo.miapi.dto.UsuarioResponseDto;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.UsuarioRepository;
import com.poo.miapi.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!usuario.isActivo() || usuario.isBloqueado()) {
            throw new IllegalStateException("Usuario inactivo o bloqueado");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(usuario);
        UsuarioResponseDto usuarioDto = new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol());
        usuarioDto.setActivo(usuario.isActivo());

        return new LoginResponseDto(token, usuarioDto);
    }

    public void cambiarPassword(CambioPasswordDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (dto.getNuevaPassword() == null || dto.getNuevaPassword().isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }

        if (passwordEncoder.matches(dto.getNuevaPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a la anterior");
        }

        usuario.setPassword(passwordEncoder.encode(dto.getNuevaPassword()));
        usuario.setCambiarPass(false);
        usuarioRepository.save(usuario);
    }

    public void reiniciarPassword(ReinicioPasswordDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
    }
}
