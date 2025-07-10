package com.poo.miapi.repository;

import com.poo.miapi.model.notificacion.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioId(Long idUsuario);

    // Método para eliminar todas las notificaciones de un usuario
    void deleteAllByUsuarioId(Long idUsuario);
}