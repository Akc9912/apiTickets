package com.poo.miapi.repository.notificacion;

import com.poo.miapi.model.notificacion.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

        // Obtener todas las notificaciones de un usuario
        List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(int usuarioId);

        // Obtener solo las no leídas
        List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(int usuarioId);

        // Buscar por tipo (por ejemplo: “SISTEMA”, “MENSAJE”, “ALERTA”)
        List<Notificacion> findByTipoOrderByFechaCreacionDesc(String tipo);

        // Buscar por origen (ej: “TICKET”, “PEDIDO”, etc.)
        List<Notificacion> findByOrigenTipoAndOrigenId(String origenTipo, int origenId);

        // Contar no leídas
        long countByUsuarioIdAndLeidaFalse(int usuarioId);

        // Buscar por tipo y usuario
        List<Notificacion> findByUsuarioIdAndTipo(Long usuarioId, String tipo);

        // Borrar todas las notificaciones de un usuario (útil para limpieza)
        void deleteByUsuarioId(Long usuarioId);

        // Consultas más complejas
        @Query("SELECT n FROM Notificacion n WHERE n.usuarioId = :usuarioId AND n.fechaCreacion >= CURRENT_DATE ORDER BY n.fechaCreacion DESC")
        List<Notificacion> findTodayNotifications(int usuarioId);
}
