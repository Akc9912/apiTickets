package com.poo.miapi.service.auth;

import com.poo.miapi.dto.auth.ChangePasswordDto;
import com.poo.miapi.dto.auth.LoginRequestDto;
import com.poo.miapi.dto.auth.LoginResponseDto;
import com.poo.miapi.dto.auth.ResetPasswordDto;
import com.poo.miapi.dto.usuarios.TecnicoResponseDto;
import com.poo.miapi.dto.usuarios.UsuarioResponseDto;
import com.poo.miapi.model.core.Admin;
import com.poo.miapi.model.core.SuperAdmin;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Trabajador;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.repository.core.TrabajadorRepository;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.AdminRepository;
import com.poo.miapi.repository.core.SuperAdminRepository;
import com.poo.miapi.service.security.JwtService;
import com.poo.miapi.service.auditoria.AuditoriaService;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.SeveridadAuditoria;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TrabajadorRepository trabajadorRepository;
    private final TecnicoRepository tecnicoRepository;
    private final AdminRepository adminRepository;
    private final SuperAdminRepository superAdminRepository;
    private final AuditoriaService auditoriaService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TrabajadorRepository trabajadorRepository,
            TecnicoRepository tecnicoRepository,
            AdminRepository adminRepository,
            SuperAdminRepository superAdminRepository,
            AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.trabajadorRepository = trabajadorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.adminRepository = adminRepository;
        this.superAdminRepository = superAdminRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * @param loginRequest Datos de login (email y contraseña)
     * @return Token JWT y datos del usuario si la autenticación es exitosa
     * @throws EntityNotFoundException  Si el usuario no existe o está inactivo
     * @throws IllegalArgumentException Si la contraseña es incorrecta
     */

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElse(null);
        // Instancia hija según rol
        if (usuario != null && usuario.getRol() != null) {
            switch (usuario.getRol().name()) {
                case "TRABAJADOR":
                    Trabajador trabajador = null;
                    try {
                        trabajador = (Trabajador) usuario;
                    } catch (ClassCastException e) {
                        // Si no es instancia, buscar por email
                        if (trabajadorRepository != null) {
                            trabajador = trabajadorRepository.findByEmail(loginRequest.getEmail()).orElse(null);
                        }
                    }
                    if (trabajador != null)
                        usuario = trabajador;
                    break;
                case "TECNICO":
                    Tecnico tecnico = null;
                    try {
                        tecnico = (Tecnico) usuario;
                    } catch (ClassCastException e) {
                        if (tecnicoRepository != null) {
                            tecnico = tecnicoRepository.findByEmail(loginRequest.getEmail()).orElse(null);
                        }
                    }
                    if (tecnico != null)
                        usuario = tecnico;
                    break;
                case "ADMIN":
                    Admin admin = null;
                    try {
                        admin = (Admin) usuario;
                    } catch (ClassCastException e) {
                        if (adminRepository != null) {
                            admin = adminRepository.findByEmail(loginRequest.getEmail()).orElse(null);
                        }
                    }
                    if (admin != null)
                        usuario = admin;
                    break;
                case "SUPERADMIN":
                    SuperAdmin superAdmin = null;
                    try {
                        superAdmin = (SuperAdmin) usuario;
                    } catch (ClassCastException e) {
                        if (superAdminRepository != null) {
                            superAdmin = superAdminRepository.findByEmail(loginRequest.getEmail()).orElse(null);
                        }
                    }
                    if (superAdmin != null)
                        usuario = superAdmin;
                    break;
            }
        }

        // Siempre verificar la contraseña
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
            // Registrar intento de login fallido
            String clientIp = getClientIpAddress();
            auditoriaService.registrarLoginFallido(loginRequest.getEmail(), clientIp);
            throw new EntityNotFoundException("Usuario no encontrado");
        }

        if (!passwordMatches) {
            // Registrar intento de login fallido
            String clientIp = getClientIpAddress();
            auditoriaService.registrarLoginFallido(loginRequest.getEmail(), clientIp);
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        // Registrar login exitoso
        auditoriaService.registrarLogin(usuario);

        String token = jwtService.generateToken(usuario);
        UsuarioResponseDto usuarioDto;
        switch (usuario.getRol().name()) {
            case "TECNICO" -> {
                Tecnico tecnico = (Tecnico) usuario;
                usuarioDto = new TecnicoResponseDto(
                        tecnico.getId(),
                        tecnico.getNombre(),
                        tecnico.getApellido(),
                        tecnico.getEmail(),
                        tecnico.getRol(),
                        tecnico.isCambiarPass(),
                        tecnico.isActivo(),
                        tecnico.isBloqueado(),
                        tecnico.getFallas(),
                        tecnico.getMarcas());
            }
            case "TRABAJADOR" -> {
                Trabajador trabajador = (Trabajador) usuario;
                usuarioDto = new UsuarioResponseDto(
                        trabajador.getId(),
                        trabajador.getNombre(),
                        trabajador.getApellido(),
                        trabajador.getEmail(),
                        trabajador.getRol(),
                        trabajador.isCambiarPass(),
                        trabajador.isActivo(),
                        trabajador.isBloqueado());
            }
            case "ADMIN" -> {
                Admin admin = (Admin) usuario;
                usuarioDto = new UsuarioResponseDto(
                        admin.getId(),
                        admin.getNombre(),
                        admin.getApellido(),
                        admin.getEmail(),
                        admin.getRol(),
                        admin.isCambiarPass(),
                        admin.isActivo(),
                        admin.isBloqueado());
            }
            case "SUPERADMIN" -> {
                SuperAdmin superAdmin = (SuperAdmin) usuario;
                usuarioDto = new UsuarioResponseDto(
                        superAdmin.getId(),
                        superAdmin.getNombre(),
                        superAdmin.getApellido(),
                        superAdmin.getEmail(),
                        superAdmin.getRol(),
                        superAdmin.isCambiarPass(),
                        superAdmin.isActivo(),
                        superAdmin.isBloqueado());
            }
            default -> {
                usuarioDto = new UsuarioResponseDto(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getApellido(),
                        usuario.getEmail(),
                        usuario.getRol(),
                        usuario.isCambiarPass(),
                        usuario.isActivo(),
                        usuario.isBloqueado());
            }
        }
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

        // Registrar cambio de contraseña en auditoría
        auditoriaService.registrarAccion(usuario, AccionAuditoria.CHANGE_PASSWORD,
                "USUARIO", usuario.getId(), "Cambio de contraseña exitoso",
                null, null, CategoriaAuditoria.SECURITY, SeveridadAuditoria.MEDIUM);
    }

    // Reiniciar contraseña: recibe ResetPasswordDto
    public void reiniciarPassword(ResetPasswordDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);

        // Registrar reset de contraseña en auditoría
        auditoriaService.registrarAccion(usuario, AccionAuditoria.RESET_PASSWORD,
                "USUARIO", usuario.getId(), "Reset de contraseña por administrador",
                null, null, CategoriaAuditoria.SECURITY, SeveridadAuditoria.HIGH);
    }

    // Método auxiliar para obtener la IP del cliente
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xfHeader = request.getHeader("X-Forwarded-For");
                if (xfHeader == null) {
                    return request.getRemoteAddr();
                }
                return xfHeader.split(",")[0].trim();
            }
        } catch (Exception e) {
            // Ignorar si no hay contexto de request
        }
        return "UNKNOWN";
    }
}
