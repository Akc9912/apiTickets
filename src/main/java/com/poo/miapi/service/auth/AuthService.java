package com.poo.miapi.service.auth;

import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.auth.LoginRequestDto;
import com.poo.miapi.dto.auth.LoginResponseDto;
import com.poo.miapi.dto.auth.ResetPasswordDto;
import com.poo.miapi.dto.usuarios.UsuarioResponseDto;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.repository.core.TrabajadorRepository;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.AdminRepository;
import com.poo.miapi.repository.core.SuperAdminRepository;
import com.poo.miapi.service.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TrabajadorRepository trabajadorRepository;
    private final TecnicoRepository tecnicoRepository;
    private final AdminRepository adminRepository;
    private final SuperAdminRepository superAdminRepository;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TrabajadorRepository trabajadorRepository,
            TecnicoRepository tecnicoRepository,
            AdminRepository adminRepository,
            SuperAdminRepository superAdminRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.trabajadorRepository = trabajadorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.adminRepository = adminRepository;
        this.superAdminRepository = superAdminRepository;
    }

    /**
     * Autenticación de usuario con medidas de seguridad mejoradas
     * 
     * Medidas de seguridad implementadas:
     * 1. Los usuarios INACTIVOS se tratan como si no existieran (baja lógica)
     * 2. Los usuarios BLOQUEADOS pueden iniciar sesión pero no realizar acciones (ponerse de acuerdo si logea o no)
     * 3. Se ejecuta verificación de contraseña incluso para usuarios inexistentes 
     *    para prevenir timing attacks
     * 4. Mismo mensaje de error para usuarios inexistentes e inactivos
     * 
     * @param loginRequest Datos de login (email y contraseña)
     * @return Token JWT y datos del usuario si la autenticación es exitosa
     * @throws EntityNotFoundException Si el usuario no existe o está inactivo
     * @throws IllegalArgumentException Si la contraseña es incorrecta
     */
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElse(null);
        // Instancia hija según rol
        if (usuario != null && usuario.getRol() != null) {
            switch (usuario.getRol().name()) {
                case "TRABAJADOR":
                    com.poo.miapi.model.core.Trabajador trabajador = null;
                    try {
                        trabajador = (com.poo.miapi.model.core.Trabajador) usuario;
                    } catch (ClassCastException e) {
                        // Si no es instancia, buscar por email
                        if (trabajadorRepository != null) {
                            trabajador = trabajadorRepository.findByEmail(loginRequest.getEmail()).orElse(null);
                        }
                    }
                    if (trabajador != null) usuario = trabajador;
                    break;
                case "TECNICO":
                    com.poo.miapi.model.core.Tecnico tecnico = null;
                    try {
                        tecnico = (com.poo.miapi.model.core.Tecnico) usuario;
                    } catch (ClassCastException e) {
                        if (tecnicoRepository != null) {
                            tecnico = tecnicoRepository.findByEmail(loginRequest.getEmail()).orElse(null);
                        }
                    }
                    if (tecnico != null) usuario = tecnico;
                    break;
                case "ADMIN":
                    com.poo.miapi.model.core.Admin admin = null;
                    try {
                        admin = (com.poo.miapi.model.core.Admin) usuario;
                    } catch (ClassCastException e) {
                        if (adminRepository != null) {
                            admin = adminRepository.findByEmail(loginRequest.getEmail()).orElse(null);
                        }
                    }
                    if (admin != null) usuario = admin;
                    break;
                case "SUPERADMIN":
                    com.poo.miapi.model.core.SuperAdmin superAdmin = null;
                    try {
                        superAdmin = (com.poo.miapi.model.core.SuperAdmin) usuario;
                    } catch (ClassCastException e) {
                        if (superAdminRepository != null) {
                            superAdmin = superAdminRepository.findByEmail(loginRequest.getEmail()).orElse(null);
                        }
                    }
                    if (superAdmin != null) usuario = superAdmin;
                    break;
            }
        }

        // Siempre verificar la contraseña para evitar timing attacks
        boolean passwordMatches = false;
        if (usuario != null) {
            passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword());
        } else {
            // Ejecutar una verificación dummy para mantener el mismo tiempo de respuesta
            passwordEncoder.matches(loginRequest.getPassword(), "$2a$10$dummyHashToPreventTimingAttacks");
        }

        // Verificación de seguridad: usuarios inexistentes o inactivos se tratan igual
        // Los usuarios bloqueados SÍ pueden iniciar sesión pero no realizar acciones
        if (usuario == null || !usuario.isActivo()) {
            throw new EntityNotFoundException("Usuario no encontrado");
        }

        if (!passwordMatches) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(usuario);
        UsuarioResponseDto usuarioDto = new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isCambiarPass(),
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
