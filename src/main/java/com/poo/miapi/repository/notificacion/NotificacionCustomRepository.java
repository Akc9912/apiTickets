package com.poo.miapi.repository.notificacion;

import com.poo.miapi.model.notificacion.Notificacion;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionCustomRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> buscarPorFiltros(int usuarioId, String tipo, Boolean leida, LocalDateTime desde,
            LocalDateTime hasta);
}
