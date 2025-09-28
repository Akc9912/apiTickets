package com.poo.miapi.repository.notificacion;

import com.poo.miapi.model.notificacion.Notificacion;
import com.poo.miapi.model.enums.TipoNotificacion;
import com.poo.miapi.model.enums.CategoriaNotificacion;
import com.poo.miapi.model.enums.PrioridadNotificacion;
import com.poo.miapi.model.enums.SeveridadNotificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    // Consultas básicas por usuario
    List<Notificacion> findByUsuarioIdAndEliminadaFalseOrderByFechaCreacionDesc(Integer usuarioId);

    Page<Notificacion> findByUsuarioIdAndEliminadaFalseOrderByFechaCreacionDesc(Integer usuarioId, Pageable pageable);

    // Notificaciones no leídas
    List<Notificacion> findByUsuarioIdAndLeidaFalseAndEliminadaFalseOrderByFechaCreacionDesc(Integer usuarioId);

    long countByUsuarioIdAndLeidaFalseAndEliminadaFalse(Integer usuarioId);

    // Notificaciones archivadas
    List<Notificacion> findByUsuarioIdAndArchivadaTrueAndEliminadaFalseOrderByFechaCreacionDesc(Integer usuarioId);

    // Por tipo y categoría
    List<Notificacion> findByUsuarioIdAndTipoAndEliminadaFalseOrderByFechaCreacionDesc(Integer usuarioId,
            TipoNotificacion tipo);

    List<Notificacion> findByUsuarioIdAndCategoriaAndEliminadaFalseOrderByFechaCreacionDesc(Integer usuarioId,
            CategoriaNotificacion categoria);

    // Por prioridad y severidad
    List<Notificacion> findByUsuarioIdAndPrioridadAndEliminadaFalseOrderByFechaCreacionDesc(Integer usuarioId,
            PrioridadNotificacion prioridad);

    List<Notificacion> findByUsuarioIdAndSeveridadAndEliminadaFalseOrderByFechaCreacionDesc(Integer usuarioId,
            SeveridadNotificacion severidad);

    // Por entidad relacionada
    List<Notificacion> findByUsuarioIdAndEntidadTipoAndEntidadIdAndEliminadaFalseOrderByFechaCreacionDesc(
            Integer usuarioId, String entidadTipo, Integer entidadId);

    // Notificaciones críticas
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.prioridad = 'CRITICA' " +
            "AND n.eliminada = false ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findNotificacionesCriticas(@Param("usuarioId") Integer usuarioId);

    // Notificaciones expiradas
    @Query("SELECT n FROM Notificacion n WHERE n.fechaExpiracion IS NOT NULL AND n.fechaExpiracion < CURRENT_TIMESTAMP "
            +
            "AND n.eliminada = false")
    List<Notificacion> findNotificacionesExpiradas();

    // Notificaciones recientes (últimas 24 horas)
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.fechaCreacion >= :fecha " +
            "AND n.eliminada = false ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findNotificacionesRecientes(@Param("usuarioId") Integer usuarioId,
            @Param("fecha") LocalDateTime fecha);

    // Estadísticas por usuario
    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.eliminada = false")
    long countTotalByUsuario(@Param("usuarioId") Integer usuarioId);

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.leida = false AND n.eliminada = false")
    long countNoLeidasByUsuario(@Param("usuarioId") Integer usuarioId);

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.archivada = true AND n.eliminada = false")
    long countArchivadasByUsuario(@Param("usuarioId") Integer usuarioId);

    // Limpiar notificaciones expiradas (soft delete)
    @Query("UPDATE Notificacion n SET n.eliminada = true, n.fechaEliminacion = CURRENT_TIMESTAMP " +
            "WHERE n.fechaExpiracion IS NOT NULL AND n.fechaExpiracion < CURRENT_TIMESTAMP AND n.eliminada = false")
    int marcarExpiradas();

    // Buscar por texto en título o mensaje
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.id = :usuarioId " +
            "AND (LOWER(n.titulo) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "OR LOWER(n.mensaje) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
            "AND n.eliminada = false ORDER BY n.fechaCreacion DESC")
    List<Notificacion> buscarPorTexto(@Param("usuarioId") Integer usuarioId, @Param("texto") String texto);

    // Dashboard - notificaciones importantes
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.id = :usuarioId " +
            "AND (n.prioridad IN ('ALTA', 'CRITICA') OR n.severidad IN ('ERROR', 'CRITICAL')) " +
            "AND n.leida = false AND n.eliminada = false ORDER BY n.prioridad DESC, n.fechaCreacion DESC")
    List<Notificacion> findNotificacionesImportantes(@Param("usuarioId") Integer usuarioId);

    // Cleanup - notificaciones muy antiguas
    @Query("SELECT n FROM Notificacion n WHERE n.fechaCreacion < :fechaLimite AND n.archivada = true")
    List<Notificacion> findNotificacionesParaLimpieza(@Param("fechaLimite") LocalDateTime fechaLimite);
}
