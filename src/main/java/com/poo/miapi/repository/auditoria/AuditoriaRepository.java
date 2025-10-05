package com.poo.miapi.repository.auditoria;

import com.poo.miapi.model.auditoria.Auditoria;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    // Buscar por usuario
    List<Auditoria> findByUsuarioIdOrderByFechaAccionDesc(Integer usuarioId);

    // Buscar por entidad
    List<Auditoria> findByEntidadTipoAndEntidadIdOrderByFechaAccionDesc(String entidadTipo, Integer entidadId);

    // Buscar por acción
    List<Auditoria> findByAccionOrderByFechaAccionDesc(AccionAuditoria accion);

    // Buscar por categoría
    Page<Auditoria> findByCategoriaOrderByFechaAccionDesc(CategoriaAuditoria categoria, Pageable pageable);

    // Buscar en rango de fechas
    @Query("SELECT a FROM Auditoria a WHERE a.fechaAccion BETWEEN :inicio AND :fin ORDER BY a.fechaAccion DESC")
    List<Auditoria> findByFechaAccionBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Buscar eventos de seguridad críticos
    @Query("SELECT a FROM Auditoria a WHERE a.categoria = 'SECURITY' AND a.severidad IN ('HIGH', 'CRITICAL') ORDER BY a.fechaAccion DESC")
    List<Auditoria> findEventosSeguridadCriticos();

    // Buscar por IP para detectar actividad sospechosa
    List<Auditoria> findByDireccionIpOrderByFechaAccionDesc(String direccionIp);

    // Contar acciones por usuario en un período
    @Query("SELECT COUNT(a) FROM Auditoria a WHERE a.usuario.id = :usuarioId AND a.fechaAccion BETWEEN :inicio AND :fin")
    Long countAccionesByUsuarioAndFecha(@Param("usuarioId") Integer usuarioId, @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    // Actividad reciente (últimas 24 horas)
    @Query("SELECT a FROM Auditoria a WHERE a.fechaAccion >= :fecha ORDER BY a.fechaAccion DESC")
    List<Auditoria> findActividadReciente(@Param("fecha") LocalDateTime fecha);

    // Estadísticas por usuario
    @Query("SELECT a.nombreUsuario, a.emailUsuario, COUNT(a), MAX(a.fechaAccion) FROM Auditoria a GROUP BY a.nombreUsuario, a.emailUsuario ORDER BY COUNT(a) DESC")
    List<Object[]> getEstadisticasPorUsuario();
}
