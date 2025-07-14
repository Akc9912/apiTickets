package com.poo.miapi.repository.notificacion;

import com.poo.miapi.model.notificacion.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findAllByUsuarioId(Long idUsuario);

    void deleteAllByUsuarioId(Long idUsuario);

    long countByUsuarioId(Long idUsuario);
}