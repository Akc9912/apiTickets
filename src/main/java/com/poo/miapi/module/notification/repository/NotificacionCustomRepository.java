package com.poo.miapi.module.notification.repository;

import com.poo.miapi.module.notification.model.Notificacion;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionCustomRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> buscarPorFiltros(int usuarioId, String tipo, Boolean leida, LocalDateTime desde,
            LocalDateTime hasta);
}
