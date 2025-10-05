package com.poo.miapi.service.notificacion;

import org.springframework.stereotype.Service;

import com.poo.miapi.dto.notificacion.NotificacionRequestDto;
import com.poo.miapi.dto.notificacion.NotificacionResponseDto;
import com.poo.miapi.model.notificacion.Notificacion;

@Service
public class NotificacionService {

    // CRUD básico

    // crearNotificacion(NotificacionDTO notificacionDTO)
    public void crearNotificacion(NotificacionRequestDto dto) {
        Notificacion n = new Notificacion(dto.getTitulo(),);
    }

    // obtenerNotificacionPorId(Long id)

    // actualizarNotificacion(Long id, NotificacionDTO notificacionDTO)

    // eliminarNotificacion(Long id)

    // Gestión por usuario

    // listarNotificacionesPorUsuario(Long usuarioId)

    // listarNoLeidasPorUsuario(Long usuarioId)

    // marcarComoLeida(Long id)

    // marcarTodasComoLeidas(Long usuarioId)

    // eliminarTodasPorUsuario(Long usuarioId)

    // Lógica avanzada

    // contarNoLeidas(Long usuarioId)

    // buscarPorFiltros(Long usuarioId, String tipo, Boolean leida, LocalDateTime
    // desde, LocalDateTime hasta)

    // enviarNotificacion(Notificacion notificacion) → (para integrar con WebSocket
    // o colas)

    // procesarEventoDeSistema(EventoSistema evento) → (para emitir notificaciones
    // automáticas, ej. ticket resuelto)

    // Integración en tiempo real (WebSocket / Event Bus)

    // notificarUsuario(Long usuarioId, Notificacion notificacion)

    // notificarGrupo(List<Long> usuariosIds, Notificacion notificacion)

    // notificarSistema(Notificacion notificacion)

    // Utilidades

    NotificacionResponseDto mapToDto(Notificacion n) {
        NotificacionResponseDto dto = new NotificacionResponseDto();
        dto.setId(n.getId());
        dto.setTitulo(n.getTitulo());
        dto.setMensaje(n.getMensaje());
        dto.setOrigenTipo(n.getOrigenTipo());
        dto.setOrigenId(n.getOrigenId());
        dto.setMetadata(n.getMetadate());
        dto.setFechaCreacion(n.getFechaCreacion());
        return dto;
    }
}
