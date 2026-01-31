package com.poo.miapi.module.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poo.miapi.module.notification.dto.NotificacionRequestDto;
import com.poo.miapi.module.notification.dto.NotificacionResponseDto;
import com.poo.miapi.module.notification.model.Notificacion;
import com.poo.miapi.module.notification.repository.NotificacionRepository;
import com.poo.miapi.shared.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    // CRUD básico

    @Transactional
    public NotificacionResponseDto crearNotificacion(NotificacionRequestDto dto) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(dto.getUsuarioId());
        notificacion.setTitulo(dto.getTitulo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setTipo(dto.getTipo());
        notificacion.setOrigenTipo(dto.getOrigenTipo());
        notificacion.setOrigenId(dto.getOrigenId());
        notificacion.setMetadate(dto.getMetadata());
        notificacion.setLeida(false);

        Notificacion saved = notificacionRepository.save(notificacion);
        return mapToDto(saved);
    }

    public NotificacionResponseDto obtenerNotificacionPorId(Integer id) {
        @SuppressWarnings("null")
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + id));
        return mapToDto(notificacion);
    }

    @SuppressWarnings("null")
    @Transactional
    public NotificacionResponseDto actualizarNotificacion(Integer id, NotificacionRequestDto dto) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + id));

        if (dto.getTitulo() != null) {
            notificacion.setTitulo(dto.getTitulo());
        }
        if (dto.getMensaje() != null) {
            notificacion.setMensaje(dto.getMensaje());
        }
        if (dto.getTipo() != null) {
            notificacion.setTipo(dto.getTipo());
        }
        if (dto.getMetadata() != null) {
            notificacion.setMetadate(dto.getMetadata());
        }

        Notificacion updated = notificacionRepository.save(notificacion);
        return mapToDto(updated);
    }

    @SuppressWarnings("null")
    @Transactional
    public void eliminarNotificacion(Integer id) {
        if (!notificacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notificación no encontrada con id: " + id);
        }
        notificacionRepository.deleteById(id);
    }

    // Gestión por usuario

    public List<NotificacionResponseDto> listarNotificacionesPorUsuario(Integer usuarioId) {
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
        return notificaciones.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<NotificacionResponseDto> listarNoLeidasPorUsuario(Integer usuarioId) {
        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(usuarioId);
        return notificaciones.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @Transactional
    public NotificacionResponseDto marcarComoLeida(Integer id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + id));

        notificacion.setLeida(true);
        Notificacion updated = notificacionRepository.save(notificacion);
        return mapToDto(updated);
    }

    @Transactional
    public void marcarTodasComoLeidas(Integer usuarioId) {
        List<Notificacion> noLeidas = notificacionRepository
                .findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(usuarioId);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(noLeidas);
    }

    @Transactional
    public void eliminarTodasPorUsuario(Long usuarioId) {
        notificacionRepository.deleteByUsuarioId(usuarioId);
    }

    // Lógica avanzada

    public long contarNoLeidas(Integer usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    public List<NotificacionResponseDto> buscarPorFiltros(Integer usuarioId, String tipo, Boolean leida,
            LocalDateTime desde, LocalDateTime hasta) {
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);

        return notificaciones.stream()
                .filter(n -> tipo == null || tipo.equals(n.getTipo()))
                .filter(n -> leida == null || leida.equals(n.isLeida()))
                .filter(n -> desde == null || !n.getFechaCreacion().isBefore(desde))
                .filter(n -> hasta == null || !n.getFechaCreacion().isAfter(hasta))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @Transactional
    public void enviarNotificacion(Notificacion notificacion) {
        Notificacion saved = notificacionRepository.save(notificacion);

        // Enviar por WebSocket si está disponible
        if (messagingTemplate != null && saved.getUsuarioId() > 0) {
            notificarUsuario(saved.getUsuarioId(), saved);
        }
    }

    @Transactional
    public void enviarNotificacionAUsuario(Integer usuarioId, String titulo, String mensaje, String tipo) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(usuarioId);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setLeida(false);

        enviarNotificacion(notificacion);
    }

    // Integración en tiempo real (WebSocket)

    @SuppressWarnings("null")
    public void notificarUsuario(Integer usuarioId, Notificacion notificacion) {
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend(
                    "/topic/notificaciones/" + usuarioId,
                    mapToDto(notificacion));
        }
    }

    public void notificarGrupo(List<Integer> usuariosIds, Notificacion notificacion) {
        if (messagingTemplate != null) {
            usuariosIds.forEach(usuarioId -> {
                Notificacion copia = new Notificacion();
                copia.setUsuarioId(usuarioId);
                copia.setTitulo(notificacion.getTitulo());
                copia.setMensaje(notificacion.getMensaje());
                copia.setTipo(notificacion.getTipo());
                copia.setOrigenTipo(notificacion.getOrigenTipo());
                copia.setOrigenId(notificacion.getOrigenId());
                copia.setMetadate(notificacion.getMetadate());

                enviarNotificacion(copia);
            });
        }
    }

    public void notificarSistema(String titulo, String mensaje) {
        if (messagingTemplate != null) {
            NotificacionResponseDto dto = new NotificacionResponseDto();
            dto.setTitulo(titulo);
            dto.setMensaje(mensaje);
            dto.setTipo("SISTEMA");
            dto.setFechaCreacion(LocalDateTime.now());

            messagingTemplate.convertAndSend("/topic/sistema", dto);
        }
    }

    // Utilidades

    private NotificacionResponseDto mapToDto(Notificacion n) {
        NotificacionResponseDto dto = new NotificacionResponseDto();
        dto.setId(n.getId());
        dto.setUsuarioId(n.getUsuarioId());
        dto.setTitulo(n.getTitulo());
        dto.setMensaje(n.getMensaje());
        dto.setTipo(n.getTipo());
        dto.setLeida(n.isLeida());
        dto.setOrigenTipo(n.getOrigenTipo());
        dto.setOrigenId(n.getOrigenId());
        dto.setMetadata(n.getMetadate());
        dto.setFechaCreacion(n.getFechaCreacion());
        return dto;
    }
}
