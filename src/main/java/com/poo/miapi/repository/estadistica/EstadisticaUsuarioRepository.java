package com.poo.miapi.repository.estadistica;

import com.poo.miapi.model.estadistica.EstadisticaUsuario;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.PeriodoTipo;
import com.poo.miapi.model.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para estadísticas por usuario individual
 * Consultas especializadas por rol y período
 */
@Repository
public interface EstadisticaUsuarioRepository extends JpaRepository<EstadisticaUsuario, Integer> {

    // Buscar estadística específica por usuario y período
    Optional<EstadisticaUsuario> findByUsuarioAndPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
            Usuario usuario, PeriodoTipo periodoTipo, int anio,
            Integer mes, Integer semana, Integer dia, Integer trimestre);

    // Buscar por tipo de usuario (rol) y período
    List<EstadisticaUsuario> findByTipoUsuarioAndPeriodoTipoAndAnioAndMes(
            Rol tipoUsuario, PeriodoTipo periodoTipo, int anio, Integer mes);

    // Buscar estadísticas de usuario específico por período
    List<EstadisticaUsuario> findByUsuarioAndPeriodoTipoAndAnio(
            Usuario usuario, PeriodoTipo periodoTipo, int anio);

    // Estadísticas del mes actual por rol
    @Query("SELECT eu FROM EstadisticaUsuario eu WHERE eu.tipoUsuario = :rol " +
            "AND eu.periodoTipo = 'MENSUAL' AND eu.anio = :anio AND eu.mes = :mes")
    List<EstadisticaUsuario> findEstadisticasMesActualPorRol(
            @Param("rol") Rol rol, @Param("anio") int anio, @Param("mes") int mes);

    // Top usuarios por tickets creados
    @Query("SELECT eu FROM EstadisticaUsuario eu WHERE eu.periodoTipo = :periodoTipo " +
            "AND eu.anio = :anio AND (:mes IS NULL OR eu.mes = :mes) " +
            "ORDER BY eu.ticketsCreados DESC")
    List<EstadisticaUsuario> findTopPorTicketsCreados(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Top usuarios por tickets evaluados
    @Query("SELECT eu FROM EstadisticaUsuario eu WHERE eu.periodoTipo = :periodoTipo " +
            "AND eu.anio = :anio AND (:mes IS NULL OR eu.mes = :mes) " +
            "ORDER BY eu.ticketsEvaluados DESC")
    List<EstadisticaUsuario> findTopPorTicketsEvaluados(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Usuarios con más incidentes reportados
    @Query("SELECT eu FROM EstadisticaUsuario eu WHERE eu.tipoUsuario = 'TRABAJADOR' " +
            "AND eu.periodoTipo = :periodoTipo AND eu.anio = :anio " +
            "AND (:mes IS NULL OR eu.mes = :mes) " +
            "ORDER BY eu.incidentesReportados DESC")
    List<EstadisticaUsuario> findTrabajadoresConMasIncidentes(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Admins con más usuarios gestionados
    @Query("SELECT eu FROM EstadisticaUsuario eu WHERE eu.tipoUsuario IN ('ADMIN', 'SUPER_ADMIN') " +
            "AND eu.periodoTipo = :periodoTipo AND eu.anio = :anio " +
            "AND (:mes IS NULL OR eu.mes = :mes) " +
            "ORDER BY eu.usuariosGestionados DESC")
    List<EstadisticaUsuario> findAdminsConMasGestion(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Comparación entre períodos para un usuario
    @Query("SELECT eu FROM EstadisticaUsuario eu WHERE eu.usuario = :usuario " +
            "AND eu.periodoTipo = :periodoTipo AND eu.anio = :anio " +
            "ORDER BY eu.mes ASC")
    List<EstadisticaUsuario> findComparacionPeriodos(
            @Param("usuario") Usuario usuario,
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio);

    // Usuarios más activos por tiempo de conexión
    @Query("SELECT eu FROM EstadisticaUsuario eu WHERE eu.periodoTipo = :periodoTipo " +
            "AND eu.anio = :anio AND (:mes IS NULL OR eu.mes = :mes) " +
            "ORDER BY eu.tiempoConectadoMinutos DESC")
    List<EstadisticaUsuario> findUsuariosMasActivos(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Promedios por rol en un período
    @Query("SELECT eu.tipoUsuario, " +
            "AVG(eu.ticketsCreados), AVG(eu.ticketsEvaluados), " +
            "AVG(eu.porcentajeAprobacion), AVG(eu.tiempoConectadoMinutos) " +
            "FROM EstadisticaUsuario eu WHERE eu.periodoTipo = :periodoTipo " +
            "AND eu.anio = :anio AND (:mes IS NULL OR eu.mes = :mes) " +
            "GROUP BY eu.tipoUsuario")
    List<Object[]> findPromediosPorRol(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Resumen ejecutivo por rol
    @Query("SELECT eu.tipoUsuario, " +
            "COUNT(eu.id), " +
            "SUM(eu.ticketsCreados), SUM(eu.ticketsEvaluados), " +
            "SUM(eu.incidentesReportados), SUM(eu.tiempoConectadoMinutos) " +
            "FROM EstadisticaUsuario eu WHERE eu.periodoTipo = :periodoTipo " +
            "AND eu.anio = :anio AND (:mes IS NULL OR eu.mes = :mes) " +
            "GROUP BY eu.tipoUsuario")
    List<Object[]> findResumenEjecutivoPorRol(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Usuarios con bajo rendimiento (pocas evaluaciones, mucho tiempo conectado)
    @Query("SELECT eu FROM EstadisticaUsuario eu WHERE eu.periodoTipo = 'MENSUAL' " +
            "AND eu.anio = :anio AND eu.mes = :mes " +
            "AND eu.tiempoConectadoMinutos > 0 " +
            "AND (eu.ticketsEvaluados * 60.0 / eu.tiempoConectadoMinutos) < 0.1 " +
            "ORDER BY (eu.ticketsEvaluados * 60.0 / eu.tiempoConectadoMinutos) ASC")
    List<EstadisticaUsuario> findUsuariosBajoRendimiento(
            @Param("anio") int anio, @Param("mes") int mes);

    // Buscar por rango de fechas
    @Query("SELECT eu FROM EstadisticaUsuario eu WHERE eu.anio = :anio " +
            "AND (:mesInicio IS NULL OR eu.mes >= :mesInicio) " +
            "AND (:mesFin IS NULL OR eu.mes <= :mesFin) " +
            "AND eu.periodoTipo = :periodoTipo " +
            "ORDER BY eu.mes ASC")
    List<EstadisticaUsuario> findPorRangoFechas(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio,
            @Param("mesInicio") Integer mesInicio,
            @Param("mesFin") Integer mesFin);
}
