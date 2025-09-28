package com.poo.miapi.service.notificacion;

import com.poo.miapi.dto.notificacion.NotificacionResponseDto;
import com.poo.miapi.model.notificacion.Notificacion;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.TipoNotificacion;
import com.poo.miapi.model.enums.CategoriaNotificacion;
import com.poo.miapi.model.enums.PrioridadNotificacion;
import com.poo.miapi.model.enums.SeveridadNotificacion;
import com.poo.miapi.repository.notificacion.NotificacionRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.service.auditoria.AuditoriaService;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.SeveridadAuditoria;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public NotificacionService(NotificacionRepository notificacionRepository,
            UsuarioRepository usuarioRepository,
            AuditoriaService auditoriaService) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    // MÉTODOS PÚBLICOS
    // Crear notificación básica
    public NotificacionResponseDto crearNotificacion(int usuarioId, String titulo, String mensaje,
            TipoNotificacion tipo, CategoriaNotificacion categoria) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Notificacion notificacion = new Notificacion(usuario, titulo, mensaje, tipo, categoria);
        establecerMetadatos(notificacion);

        Notificacion saved = notificacionRepository.save(notificacion);

        // Auditar creación
        auditoriaService.registrarAccion(usuario, AccionAuditoria.CREATE, "NOTIFICACION",
                saved.getId(), "Notificación creada: " + titulo, null, saved,
                CategoriaAuditoria.BUSINESS, SeveridadAuditoria.LOW);

        return mapToDto(saved);
    }

    // Crear notificación completa
    public NotificacionResponseDto crearNotificacionCompleta(int usuarioId, String titulo, String mensaje,
            TipoNotificacion tipo, CategoriaNotificacion categoria,
            PrioridadNotificacion prioridad, SeveridadNotificacion severidad,
            String entidadTipo, int entidadId,
            Map<String, Object> datosAdicionales,
            LocalDateTime fechaExpiracion, Usuario creadoPor) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Notificacion notificacion = new Notificacion(usuario, titulo, mensaje, tipo, categoria, prioridad, severidad);
        notificacion.setEntidadTipo(entidadTipo);
        notificacion.setEntidadId(entidadId);
        notificacion.setDatosAdicionales(datosAdicionales);
        notificacion.setFechaExpiracion(fechaExpiracion);
        notificacion.setCreadoPor(creadoPor);

        establecerMetadatos(notificacion);
        configurarVisualizacion(notificacion);

        Notificacion saved = notificacionRepository.save(notificacion);

        // Auditar creación
        auditoriaService.registrarAccion(creadoPor != null ? creadoPor : usuario, AccionAuditoria.CREATE,
                "NOTIFICACION", saved.getId(), "Notificación completa creada: " + titulo,
                null, saved, CategoriaAuditoria.BUSINESS, SeveridadAuditoria.LOW);

        return mapToDto(saved);
    }

    // Crear notificación para ticket asignado
    @Async
    public void notificarTicketAsignado(int tecnicoId, int ticketId, String tituloTicket) {
        String titulo = "Nuevo ticket asignado";
        String mensaje = String.format("Se te ha asignado el ticket #%d: %s", ticketId, tituloTicket);

        crearNotificacionCompleta(tecnicoId, titulo, mensaje, TipoNotificacion.TICKET_ASIGNADO,
                CategoriaNotificacion.TICKETS, PrioridadNotificacion.ALTA, SeveridadNotificacion.INFO,
                "TICKET", ticketId, Map.of("ticketId", ticketId, "titulo", tituloTicket),
                null, null);
    }

    // Crear notificación para marca asignada
    @Async
    public void notificarMarcaAsignada(int tecnicoId, int ticketId, String motivo) {
        String titulo = "Marca registrada";
        String mensaje = String.format("Se ha registrado una marca. Motivo: %s", motivo);

        crearNotificacionCompleta(tecnicoId, titulo, mensaje, TipoNotificacion.MARCA_ASIGNADA,
                CategoriaNotificacion.USUARIOS, PrioridadNotificacion.MEDIA, SeveridadNotificacion.WARNING,
                "TICKET", ticketId, Map.of("motivo", motivo), null, null);
    }

    // Obtener notificaciones del usuario
    public List<NotificacionResponseDto> obtenerNotificacionesUsuario(int usuarioId) {
        return notificacionRepository.findByUsuarioIdAndEliminadaFalseOrderByFechaCreacionDesc(usuarioId)
                .stream().map(this::mapToDto).toList();
    }

    // Obtener notificaciones paginadas
    public Page<NotificacionResponseDto> obtenerNotificacionesPaginadas(int usuarioId, Pageable pageable) {
        return notificacionRepository.findByUsuarioIdAndEliminadaFalseOrderByFechaCreacionDesc(usuarioId, pageable)
                .map(this::mapToDto);
    }

    // Obtener notificaciones no leídas
    public List<NotificacionResponseDto> obtenerNotificacionesNoLeidas(int usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeidaFalseAndEliminadaFalseOrderByFechaCreacionDesc(usuarioId)
                .stream().map(this::mapToDto).toList();
    }

    // Contar notificaciones no leídas
    public long contarNoLeidas(int usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalseAndEliminadaFalse(usuarioId);
    }

    // Marcar como leída
    @Transactional
    public NotificacionResponseDto marcarComoLeida(int notificacionId, int usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));

        if (notificacion.getUsuario().getId() != usuarioId) {
            throw new IllegalArgumentException("No tienes permisos para modificar esta notificación");
        }

        if (!notificacion.getLeida()) {
            notificacion.marcarComoLeida();
            notificacionRepository.save(notificacion);

            // Auditar lectura
            auditoriaService.registrarAccion(notificacion.getUsuario(), AccionAuditoria.UPDATE,
                    "NOTIFICACION", notificacionId, "Notificación marcada como leída",
                    false, true, CategoriaAuditoria.BUSINESS, SeveridadAuditoria.LOW);
        }

        return mapToDto(notificacion);
    }

    // Archivar notificación
    @Transactional
    public NotificacionResponseDto archivarNotificacion(int notificacionId, int usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));

        if (notificacion.getUsuario().getId() != usuarioId) {
            throw new IllegalArgumentException("No tienes permisos para modificar esta notificación");
        }

        notificacion.archivar();
        notificacionRepository.save(notificacion);

        return mapToDto(notificacion);
    }

    // Eliminar notificación (soft delete)
    @Transactional
    public void eliminarNotificacion(int notificacionId, int usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));

        if (notificacion.getUsuario().getId() != usuarioId) {
            throw new IllegalArgumentException("No tienes permisos para eliminar esta notificación");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        notificacion.eliminar(usuario);
        notificacionRepository.save(notificacion);

        // Auditar eliminación
        auditoriaService.registrarAccion(usuario, AccionAuditoria.DELETE, "NOTIFICACION",
                notificacionId, "Notificación eliminada", notificacion, null,
                CategoriaAuditoria.BUSINESS, SeveridadAuditoria.LOW);
    }

    // Obtener notificaciones críticas
    public List<NotificacionResponseDto> obtenerNotificacionesCriticas(int usuarioId) {
        return notificacionRepository.findNotificacionesCriticas(usuarioId)
                .stream().map(this::mapToDto).toList();
    }

    // Obtener notificaciones importantes para dashboard
    public List<NotificacionResponseDto> obtenerNotificacionesImportantes(int usuarioId) {
        return notificacionRepository.findNotificacionesImportantes(usuarioId)
                .stream().map(this::mapToDto).toList();
    }

    // Obtener notificaciones recientes (24 horas)
    public List<NotificacionResponseDto> obtenerNotificacionesRecientes(int usuarioId) {
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);
        return notificacionRepository.findNotificacionesRecientes(usuarioId, hace24Horas)
                .stream().map(this::mapToDto).toList();
    }

    // Buscar notificaciones por texto
    public List<NotificacionResponseDto> buscarNotificaciones(int usuarioId, String texto) {
        return notificacionRepository.buscarPorTexto(usuarioId, texto)
                .stream().map(this::mapToDto).toList();
    }

    // Obtener estadísticas del usuario
    public Map<String, Long> obtenerEstadisticasUsuario(int usuarioId) {
        return Map.of(
                "total", notificacionRepository.countTotalByUsuario(usuarioId),
                "noLeidas", notificacionRepository.countNoLeidasByUsuario(usuarioId),
                "archivadas", notificacionRepository.countArchivadasByUsuario(usuarioId));
    }

    // Limpiar notificaciones expiradas (tarea programada)
    @Transactional
    public int limpiarNotificacionesExpiradas() {
        return notificacionRepository.marcarExpiradas();
    }

    // Marcar todas como leídas
    @Transactional
    public void marcarTodasComoLeidas(int usuarioId) {
        List<Notificacion> noLeidas = notificacionRepository
                .findByUsuarioIdAndLeidaFalseAndEliminadaFalseOrderByFechaCreacionDesc(usuarioId);

        for (Notificacion notificacion : noLeidas) {
            notificacion.marcarComoLeida();
        }

        if (!noLeidas.isEmpty()) {
            notificacionRepository.saveAll(noLeidas);

            // Auditar acción masiva
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            auditoriaService.registrarAccion(usuario, AccionAuditoria.UPDATE, "NOTIFICACION",
                    null, String.format("Marcadas %d notificaciones como leídas", noLeidas.size()),
                    null, null, CategoriaAuditoria.BUSINESS, SeveridadAuditoria.LOW);
        }
    } // MÉTODOS PRIVADOS/UTILIDADES
      // Establecer metadatos de la notificación

    private void establecerMetadatos(Notificacion notificacion) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                notificacion.setIpOrigen(getClientIpAddress(request));
                notificacion.setUserAgent(request.getHeader("User-Agent"));
            }
        } catch (Exception e) {
            // Ignorar si no hay contexto de request
        }
    }

    // Configurar visualización según tipo y prioridad
    private void configurarVisualizacion(Notificacion notificacion) {
        switch (notificacion.getTipo()) {
            case TICKET_ASIGNADO -> {
                notificacion.setIcono("assignment");
                notificacion.setColor("blue");
            }
            case MARCA_ASIGNADA -> {
                notificacion.setIcono("warning");
                notificacion.setColor("orange");
            }
            case FALLA_REGISTRADA -> {
                notificacion.setIcono("error");
                notificacion.setColor("red");
            }
            case PASSWORD_RESET -> {
                notificacion.setIcono("security");
                notificacion.setColor("purple");
            }
            case ALERTA_SEGURIDAD -> {
                notificacion.setIcono("shield");
                notificacion.setColor("red");
            }
            default -> {
                notificacion.setIcono("info");
                notificacion.setColor("blue");
            }
        }

        // Ajustar según prioridad
        if (notificacion.getPrioridad() == PrioridadNotificacion.CRITICA) {
            notificacion.setColor("red");
            notificacion.setMostrarBadge(true);
        }
    }

    // Obtener IP del cliente
    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    // Método auxiliar para mapear entidad a DTO
    private NotificacionResponseDto mapToDto(Notificacion n) {
        return new NotificacionResponseDto(
                n.getId(),
                n.getUsuario().getId(),
                n.getTitulo(),
                n.getMensaje(),
                n.getTipo(),
                n.getCategoria(),
                n.getPrioridad(),
                n.getSeveridad(),
                n.getLeida(),
                n.getArchivada(),
                n.getEntidadTipo(),
                n.getEntidadId(),
                n.getIcono(),
                n.getColor(),
                n.getMostrarBadge(),
                n.getFechaCreacion(),
                n.getFechaLectura(),
                n.isExpirada());
    }
}
