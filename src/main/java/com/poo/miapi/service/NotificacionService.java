package com.poo.miapi.service;

import com.poo.miapi.dto.notificacion.NotificacionResponseDto;
import com.poo.miapi.model.notificacion.Notificacion;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.NotificacionRepository;
import com.poo.miapi.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Enviar una notificación a un usuario (devuelve DTO)
    public NotificacionResponseDto enviarNotificacion(int idUsuario, String mensaje) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Notificacion n = new Notificacion(usuario, mensaje);
        Notificacion saved = notificacionRepository.save(n);
        return mapToDto(saved);
    }

    // Obtener todas las notificaciones de un usuario (devuelve DTOs)
    public List<NotificacionResponseDto> obtenerNotificaciones(Long idUsuario) {
        return notificacionRepository.findAllByUsuarioId(idUsuario).stream()
                .map(this::mapToDto)
                .toList();
    }

    // Eliminar todas las notificaciones de un usuario
    public void eliminarTodasDelUsuario(Long idUsuario) {
        notificacionRepository.deleteAllByUsuarioId(idUsuario);
    }

    // Método auxiliar para mapear entidad a DTO
    private NotificacionResponseDto mapToDto(Notificacion n) {
        return new NotificacionResponseDto(
                n.getId(),
                n.getUsuario().getId(),
                n.getMensaje(),
                n.getFechaCreacion());
    }
}
