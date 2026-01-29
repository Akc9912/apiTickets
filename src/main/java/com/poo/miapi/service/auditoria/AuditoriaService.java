package com.poo.miapi.service.auditoria;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poo.miapi.model.auditoria.*;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.SeveridadAuditoria;
import com.poo.miapi.repository.auditoria.AuditoriaRepository;
import com.poo.miapi.dto.auditoria.AuditoriaResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final ObjectMapper objectMapper;

    public AuditoriaService(AuditoriaRepository auditoriaRepository, ObjectMapper objectMapper) {
        this.auditoriaRepository = auditoriaRepository;
        this.objectMapper = objectMapper;
    }

    // MÉTODOS PÚBLICOS
    // Registra una acción de auditoría básica de forma asíncrona
    @Async
    public void registrarAccion(Usuario usuario, AccionAuditoria accion, String entidadTipo,
            Integer entidadId, String detalleAccion) {
        registrarAccion(usuario, accion, entidadTipo, entidadId, detalleAccion,
                null, null, CategoriaAuditoria.BUSINESS, SeveridadAuditoria.MEDIUM);
    }

    // Registra una acción de auditoría completa de forma asíncrona
    @Async
    public void registrarAccion(Usuario usuario, AccionAuditoria accion, String entidadTipo,
            Integer entidadId, String detalleAccion, Object valoresAnteriores,
            Object valoresNuevos, CategoriaAuditoria categoria, SeveridadAuditoria severidad) {
        try {
            Auditoria auditoria = new Auditoria(usuario, accion, entidadTipo, entidadId, detalleAccion);

            // Configurar categoría y severidad
            auditoria.setCategoria(categoria);
            auditoria.setSeveridad(severidad);

            // Serializar objetos a JSON si existen
            if (valoresAnteriores != null) {
                auditoria.setValoresAnteriores(objectMapper.writeValueAsString(valoresAnteriores));
            }
            if (valoresNuevos != null) {
                auditoria.setValoresNuevos(objectMapper.writeValueAsString(valoresNuevos));
            }

            // Obtener información de la petición HTTP si está disponible
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                        .getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    auditoria.setDireccionIp(getClientIpAddress(request));
                    auditoria.setUserAgent(request.getHeader("User-Agent"));
                    auditoria.setSesionId(request.getSession().getId());
                }
            } catch (Exception e) {
                // Ignora errores de contexto de request (ej: tareas programadas)
            }

            // Generar hash de integridad
            auditoria.setHashIntegridad(generarHashIntegridad(auditoria));

            // Configurar retención (7 años para seguridad, 5 años para negocio)
            int diasRetencion = categoria == CategoriaAuditoria.SECURITY ? 2555 : 1825;
            auditoria.setRetencionHasta(LocalDateTime.now().plusDays(diasRetencion));

            auditoriaRepository.save(auditoria);

        } catch (Exception e) {
            // Log del error pero no fallar la operación principal
            System.err.println("Error al registrar auditoría: " + e.getMessage());
        }
    }

    // Registra eventos de seguridad críticos
    public void registrarEventoSeguridad(Usuario usuario, AccionAuditoria accion, String detalle) {
        registrarAccion(usuario, accion, "SECURITY", null, detalle,
                null, null, CategoriaAuditoria.SECURITY, SeveridadAuditoria.HIGH);
    }

    // Registra login exitoso del usuario
    public void registrarLogin(Usuario usuario) {
        registrarAccion(usuario, AccionAuditoria.LOGIN, "USUARIO", usuario.getId(),
                "Login exitoso", null, null, CategoriaAuditoria.SECURITY, SeveridadAuditoria.LOW);
    }

    // Registra intento de login fallido
    public void registrarLoginFallido(String email, String direccionIp) {
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setNombreUsuario("USUARIO_NO_IDENTIFICADO");
            auditoria.setEmailUsuario(email);
            auditoria.setAccion(AccionAuditoria.LOGIN_FAILED);
            auditoria.setEntidadTipo("SECURITY");
            auditoria.setDetalleAccion("Intento de login fallido para email: " + email);
            auditoria.setDireccionIp(direccionIp);
            auditoria.setCategoria(CategoriaAuditoria.SECURITY);
            auditoria.setSeveridad(SeveridadAuditoria.HIGH);
            auditoria.setHashIntegridad(generarHashIntegridad(auditoria));
            auditoria.setRetencionHasta(LocalDateTime.now().plusDays(2555)); // 7 años

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            System.err.println("Error al registrar login fallido: " + e.getMessage());
        }
    }

    // Métodos de consulta
    // Obtener auditorías por usuario
    public List<Auditoria> obtenerPorUsuario(Integer usuarioId) {
        return auditoriaRepository.findByUsuarioIdOrderByFechaAccionDesc(usuarioId);
    }

    // Obtener auditorías por entidad
    public List<Auditoria> obtenerPorEntidad(String entidadTipo, Integer entidadId) {
        return auditoriaRepository.findByEntidadTipoAndEntidadIdOrderByFechaAccionDesc(entidadTipo, entidadId);
    }

    // Obtener auditorías por categoría
    public Page<Auditoria> obtenerPorCategoria(CategoriaAuditoria categoria, Pageable pageable) {
        return auditoriaRepository.findByCategoriaOrderByFechaAccionDesc(categoria, pageable);
    }

    // Obtener eventos de seguridad críticos
    public List<Auditoria> obtenerEventosSeguridadCriticos() {
        return auditoriaRepository.findEventosSeguridadCriticos();
    }

    // Obtener actividad reciente (24 horas)
    public List<Auditoria> obtenerActividadReciente() {
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);
        return auditoriaRepository.findActividadReciente(hace24Horas);
    }

    // Obtener auditorías en rango de fechas
    public List<Auditoria> obtenerEnRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return auditoriaRepository.findByFechaAccionBetween(inicio, fin);
    }

    // Métodos para el controlador
    // Obtener actividad reciente con DTOs
    public List<AuditoriaResponseDto> getActividadReciente(int horas) {
        LocalDateTime fechaDesde = LocalDateTime.now().minusHours(horas);
        List<Auditoria> auditorias = auditoriaRepository.findActividadReciente(fechaDesde);
        return auditorias.stream().map(this::toDto).toList();
    }

    // Obtener auditoría por usuario con DTOs
    public List<AuditoriaResponseDto> getAuditoriaPorUsuario(Integer usuarioId) {
        List<Auditoria> auditorias = auditoriaRepository.findByUsuarioIdOrderByFechaAccionDesc(usuarioId);
        return auditorias.stream().map(this::toDto).toList();
    }

    // Obtener auditoría por entidad con DTOs
    public List<AuditoriaResponseDto> getAuditoriaPorEntidad(String entidadTipo, Integer entidadId) {
        List<Auditoria> auditorias = auditoriaRepository
                .findByEntidadTipoAndEntidadIdOrderByFechaAccionDesc(entidadTipo, entidadId);
        return auditorias.stream().map(this::toDto).toList();
    }

    // Obtener eventos de seguridad críticos con DTOs
    public List<AuditoriaResponseDto> getEventosSeguridadCriticos() {
        List<Auditoria> auditorias = auditoriaRepository.findEventosSeguridadCriticos();
        return auditorias.stream().map(this::toDto).toList();
    }

    // Obtener auditoría por categoría con DTOs paginados
    public Page<AuditoriaResponseDto> getAuditoriaPorCategoria(CategoriaAuditoria categoria, Pageable pageable) {
        Page<Auditoria> auditorias = auditoriaRepository.findByCategoriaOrderByFechaAccionDesc(categoria, pageable);
        return auditorias.map(this::toDto);
    }

    // Obtener estadísticas por usuario
    public List<Object[]> getEstadisticasPorUsuario() {
        return auditoriaRepository.getEstadisticasPorUsuario();
    }

    // Buscar en auditoría con filtros
    public List<AuditoriaResponseDto> buscarEnAuditoria(LocalDateTime fechaInicio, LocalDateTime fechaFin,
            AccionAuditoria accion, String entidadTipo) {
        List<Auditoria> auditorias;

        if (fechaInicio != null && fechaFin != null) {
            auditorias = auditoriaRepository.findByFechaAccionBetween(fechaInicio, fechaFin);
        } else if (accion != null) {
            auditorias = auditoriaRepository.findByAccionOrderByFechaAccionDesc(accion);
        } else if (entidadTipo != null) {
            auditorias = auditoriaRepository.findByEntidadTipoAndEntidadIdOrderByFechaAccionDesc(entidadTipo, null);
        } else {
            // Si no hay filtros, devolver actividad reciente
            LocalDateTime hace7Dias = LocalDateTime.now().minusDays(7);
            auditorias = auditoriaRepository.findActividadReciente(hace7Dias);
        }

        return auditorias.stream().map(this::toDto).toList();
    }

    // MÉTODOS PRIVADOS/UTILIDADES
    // Método auxiliar para mapear entidad a DTO
    private AuditoriaResponseDto toDto(Auditoria auditoria) {
        AuditoriaResponseDto dto = new AuditoriaResponseDto();
        dto.setId(auditoria.getId());
        dto.setIdUsuario(auditoria.getUsuario() != null ? auditoria.getUsuario().getId() : null);
        dto.setNombreUsuario(auditoria.getNombreUsuario());
        dto.setEmailUsuario(auditoria.getEmailUsuario());
        dto.setRolUsuario(auditoria.getRolUsuario());
        dto.setAccion(auditoria.getAccion());
        dto.setEntidadTipo(auditoria.getEntidadTipo());
        dto.setEntidadId(auditoria.getEntidadId());
        dto.setDetalleAccion(auditoria.getDetalleAccion());
        dto.setValoresAnteriores(auditoria.getValoresAnteriores());
        dto.setValoresNuevos(auditoria.getValoresNuevos());
        dto.setDireccionIp(auditoria.getDireccionIp());
        dto.setUserAgent(auditoria.getUserAgent());
        dto.setFechaAccion(auditoria.getFechaAccion());
        dto.setCategoria(auditoria.getCategoria());
        dto.setSeveridad(auditoria.getSeveridad());
        return dto;
    }

    // Métodos auxiliares
    // Método auxiliar para obtener IP del cliente
    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    // Método auxiliar para generar hash de integridad
    private String generarHashIntegridad(Auditoria auditoria) {
        try {
            String data = (auditoria.getUsuario() != null ? String.valueOf(auditoria.getUsuario().getId()) : "") +
                    auditoria.getNombreUsuario() +
                    auditoria.getAccion().toString() +
                    auditoria.getEntidadTipo() +
                    (auditoria.getEntidadId() != null ? auditoria.getEntidadId().toString() : "") +
                    auditoria.getFechaAccion().toString();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
