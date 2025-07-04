package com.poo.miapi.service;

import com.poo.miapi.model.notificacion.Notificacion;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.NotificacionRepository;
import com.poo.miapi.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Notificacion enviarNotificacion(int idUsuario, String mensaje) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Notificacion n = new Notificacion(usuario, mensaje);
        return notificacionRepository.save(n);
    }

    public List<Notificacion> obtenerNotificaciones(int idUsuario) {
        return notificacionRepository.findByUsuarioId(idUsuario);
    }

    public void eliminarTodasDelUsuario(int idUsuario) {
        List<Notificacion> notificaciones = obtenerNotificaciones(idUsuario);
        notificacionRepository.deleteAll(notificaciones);
    }
}
