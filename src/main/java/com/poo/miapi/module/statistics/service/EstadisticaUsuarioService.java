package com.poo.miapi.service.estadistica;

import com.poo.miapi.dto.estadistica.EstadisticaUsuarioDto;
import com.poo.miapi.model.enums.PeriodoTipo;
import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.model.estadistica.EstadisticaUsuario;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.estadistica.EstadisticaUsuarioRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio especializado en estadísticas de usuarios
 * Maneja consultas y cálculos específicos para métricas de usuarios
 * individuales
 */
@Service
@Transactional
public class EstadisticaUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(EstadisticaUsuarioService.class);

    private final EstadisticaUsuarioRepository estadisticaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    public EstadisticaUsuarioService(
            EstadisticaUsuarioRepository estadisticaUsuarioRepository,
            UsuarioRepository usuarioRepository) {
        this.estadisticaUsuarioRepository = estadisticaUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ========================================
    // ESTADÍSTICAS INDIVIDUALES
    // ========================================

    /**
     * Obtener estadísticas por email del usuario
     */
    public List<EstadisticaUsuarioDto> obtenerEstadisticasPorEmail(String email, PeriodoTipo periodo, Integer anio,
            Integer mes) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
            if (usuario.isEmpty()) {
                logger.warn("Usuario no encontrado con email: {}", email);
                return List.of();
            }

            return obtenerEstadisticasPorUsuario(usuario.get().getId(), periodo, anio, mes);
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas por email {}: {}", email, e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtener estadísticas por ID de usuario (simplificado)
     */
    public List<EstadisticaUsuarioDto> obtenerEstadisticasPorUsuario(int usuarioId, PeriodoTipo periodo, Integer anio,
            Integer mes) {
        try {
            // Por ahora usar findAll y filtrar manualmente
            List<EstadisticaUsuario> todasEstadisticas = estadisticaUsuarioRepository.findAll();

            List<EstadisticaUsuario> estadisticas = todasEstadisticas.stream()
                    .filter(e -> e.getUsuario().getId() == usuarioId)
                    .filter(e -> e.getPeriodoTipo() == periodo)
                    .filter(e -> anio == null || e.getAnio() == anio)
                    .filter(e -> mes == null || (e.getMes() != null && e.getMes().equals(mes)))
                    .collect(Collectors.toList());

            return estadisticas.stream()
                    .map(this::convertirADto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas del usuario {}: {}", usuarioId, e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtener estadísticas por rol de usuario (simplificado)
     */
    public List<EstadisticaUsuarioDto> obtenerEstadisticasPorRol(Rol rol, PeriodoTipo periodo, Integer anio,
            Integer mes) {
        try {
            List<EstadisticaUsuario> todasEstadisticas = estadisticaUsuarioRepository.findAll();

            List<EstadisticaUsuario> estadisticas = todasEstadisticas.stream()
                    .filter(e -> e.getTipoUsuario() == rol)
                    .filter(e -> e.getPeriodoTipo() == periodo)
                    .filter(e -> anio == null || e.getAnio() == anio)
                    .filter(e -> mes == null || (e.getMes() != null && e.getMes().equals(mes)))
                    .collect(Collectors.toList());

            return estadisticas.stream()
                    .map(this::convertirADto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas por rol {}: {}", rol, e.getMessage());
            return List.of();
        }
    }

    // ========================================
    // RANKINGS Y COMPARATIVAS
    // ========================================

    /**
     * Obtener usuarios más activos (simplificado)
     */
    public List<Map<String, Object>> obtenerUsuariosMasActivos(int limite, Rol rol) {
        try {
            List<EstadisticaUsuario> todasEstadisticas = estadisticaUsuarioRepository.findAll();

            List<EstadisticaUsuario> estadisticas = todasEstadisticas.stream()
                    .filter(e -> rol == null || e.getTipoUsuario() == rol)
                    .sorted((e1, e2) -> Integer.compare(
                            e2.getTicketsCreados() + e2.getTicketsEvaluados(),
                            e1.getTicketsCreados() + e1.getTicketsEvaluados()))
                    .limit(limite)
                    .collect(Collectors.toList());

            return estadisticas.stream()
                    .map(this::convertirAMapaRanking)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error obteniendo usuarios más activos: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtener estadísticas de sesiones (simplificado)
     */
    public Map<String, Object> obtenerEstadisticasSesiones(Rol rol, int dias) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            LocalDateTime fechaInicio = LocalDateTime.now().minusDays(dias);
            List<EstadisticaUsuario> estadisticas = estadisticaUsuarioRepository.findAll();

            if (rol != null) {
                estadisticas = estadisticas.stream()
                        .filter(e -> e.getTipoUsuario() == rol)
                        .collect(Collectors.toList());
            }

            int totalSesiones = estadisticas.stream()
                    .mapToInt(EstadisticaUsuario::getSesionesActivas)
                    .sum();

            int tiempoTotal = estadisticas.stream()
                    .mapToInt(EstadisticaUsuario::getTiempoConectadoMinutos)
                    .sum();

            resultado.put("totalSesiones", totalSesiones);
            resultado.put("tiempoTotalMinutos", tiempoTotal);
            resultado.put("promedioTiempoPorSesion",
                    totalSesiones > 0 ? tiempoTotal / totalSesiones : 0);
            resultado.put("rangoAnalisis", dias + " días");
            resultado.put("desde", fechaInicio);
            resultado.put("hasta", LocalDateTime.now());

        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas de sesiones: {}", e.getMessage());
            resultado.put("error", "Error al obtener estadísticas de sesiones");
        }

        return resultado;
    }

    /**
     * Obtener métricas de productividad (simplificado)
     */
    public List<Map<String, Object>> obtenerMetricasProductividad(Rol rol, int meses) {
        try {
            List<EstadisticaUsuario> estadisticas = estadisticaUsuarioRepository.findAll();

            return estadisticas.stream()
                    .filter(e -> rol == null || e.getTipoUsuario() == rol)
                    .map(this::convertirAMetricasProductividad)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error obteniendo métricas de productividad: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtener comparativa entre equipos
     */
    public Map<String, Object> obtenerComparativaEquipos(PeriodoTipo periodo, Integer anio, Integer mes) {
        Map<String, Object> comparativa = new HashMap<>();

        try {
            // Obtener estadísticas por cada rol
            for (Rol rol : Rol.values()) {
                List<EstadisticaUsuario> estadisticasRol = obtenerEstadisticasFiltradas(rol, periodo, anio, mes);

                if (!estadisticasRol.isEmpty()) {
                    Map<String, Object> metricsRol = calcularMetricasAgregadas(estadisticasRol);
                    comparativa.put(rol.name().toLowerCase(), metricsRol);
                }
            }

            comparativa.put("periodo", Map.of(
                    "tipo", periodo,
                    "anio", anio,
                    "mes", mes));

        } catch (Exception e) {
            logger.error("Error obteniendo comparativa de equipos: {}", e.getMessage());
            comparativa.put("error", "Error al generar comparativa");
        }

        return comparativa;
    }

    /**
     * Obtener resumen general de usuarios (simplificado)
     */
    public Map<String, Object> obtenerResumenGeneral() {
        Map<String, Object> resumen = new HashMap<>();

        try {
            // Estadísticas básicas
            resumen.put("totalUsuarios", usuarioRepository.count());
            resumen.put("usuariosActivos", usuarioRepository.countByActivoTrue());
            resumen.put("usuariosBloqueados", usuarioRepository.countByBloqueadoTrue());

            // Distribución por roles
            Map<String, Integer> porRoles = new HashMap<>();
            for (Rol rol : Rol.values()) {
                porRoles.put(rol.name(), usuarioRepository.countByRol(rol));
            }
            resumen.put("distribucionPorRoles", porRoles);

            // Métricas de actividad reciente (simplificado)
            List<EstadisticaUsuario> estadisticas = estadisticaUsuarioRepository.findAll();
            LocalDateTime ultimaSemana = LocalDateTime.now().minusDays(7);

            // Cálculos aproximados basados en datos disponibles
            int sesionesActivas = estadisticas.stream()
                    .mapToInt(EstadisticaUsuario::getSesionesActivas)
                    .sum();

            int tiempoTotal = estadisticas.stream()
                    .mapToInt(EstadisticaUsuario::getTiempoConectadoMinutos)
                    .sum();

            resumen.put("actividadReciente", Map.of(
                    "sesionesActivasTotal", sesionesActivas,
                    "tiempoTotalConectadoMinutos", tiempoTotal,
                    "promedioTiempoConectado", estadisticas.isEmpty() ? 0 : tiempoTotal / estadisticas.size()));

        } catch (Exception e) {
            logger.error("Error obteniendo resumen general: {}", e.getMessage());
            resumen.put("error", "Error al obtener resumen general");
        }

        return resumen;
    }

    // ========================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ========================================

    private EstadisticaUsuarioDto convertirADto(EstadisticaUsuario estadistica) {
        EstadisticaUsuarioDto dto = new EstadisticaUsuarioDto();

        // Información básica
        dto.setId(estadistica.getId());
        dto.setUsuarioId(estadistica.getUsuario().getId());
        dto.setNombreUsuario(estadistica.getUsuario().getNombre());
        dto.setApellidoUsuario(estadistica.getUsuario().getApellido());
        dto.setEmailUsuario(estadistica.getUsuario().getEmail());
        dto.setTipoUsuario(estadistica.getTipoUsuario());

        // Período
        dto.setPeriodoTipo(estadistica.getPeriodoTipo());
        dto.setAnio(estadistica.getAnio());
        dto.setMes(estadistica.getMes());
        dto.setDia(estadistica.getDia());
        dto.setSemana(estadistica.getSemana());
        dto.setTrimestre(estadistica.getTrimestre());

        // Métricas generales
        dto.setTicketsCreados(estadistica.getTicketsCreados());
        dto.setTicketsEvaluados(estadistica.getTicketsEvaluados());
        dto.setSolicitudesDevolucionCreadas(estadistica.getSolicitudesDevolucionCreadas());
        dto.setSolicitudesDevolucionProcesadas(estadistica.getSolicitudesDevolucionProcesadas());

        // Métricas específicas para Admins/SuperAdmins
        dto.setTicketsAsignados(estadistica.getTicketsAsignados());
        dto.setUsuariosGestionados(estadistica.getUsuariosGestionados());
        dto.setReportesGenerados(estadistica.getReportesGenerados());

        // Métricas de sesión
        dto.setSesionesActivas(estadistica.getSesionesActivas());
        dto.setTiempoConectadoMinutos(estadistica.getTiempoConectadoMinutos());

        // Métricas específicas para Trabajadores
        dto.setIncidentesReportados(estadistica.getIncidentesReportados());
        dto.setEvaluacionesRealizadas(estadistica.getEvaluacionesRealizadas());

        // Fechas
        dto.setFechaCreacion(estadistica.getFechaCreacion());
        dto.setFechaActualizacion(estadistica.getFechaActualizacion());

        return dto;
    }

    private Map<String, Object> convertirAMapaRanking(EstadisticaUsuario estadistica) {
        Map<String, Object> item = new HashMap<>();

        item.put("usuarioId", estadistica.getUsuario().getId());
        item.put("nombre", estadistica.getUsuario().getNombre());
        item.put("apellido", estadistica.getUsuario().getApellido());
        item.put("email", estadistica.getUsuario().getEmail());
        item.put("rol", estadistica.getTipoUsuario());

        // Métricas de actividad
        item.put("ticketsCreados", estadistica.getTicketsCreados());
        item.put("ticketsEvaluados", estadistica.getTicketsEvaluados());
        item.put("tiempoConectado", estadistica.getTiempoConectadoMinutos());
        item.put("sesionesActivas", estadistica.getSesionesActivas());

        // Calcular puntuación de actividad
        int puntuacionActividad = calcularPuntuacionActividad(estadistica);
        item.put("puntuacionActividad", puntuacionActividad);

        return item;
    }

    private int calcularPuntuacionActividad(EstadisticaUsuario estadistica) {
        // Fórmula simple de puntuación basada en varias métricas
        return estadistica.getTicketsCreados() * 2 +
                estadistica.getTicketsEvaluados() * 3 +
                estadistica.getTicketsAsignados() * 2 +
                (estadistica.getTiempoConectadoMinutos() / 60); // Horas convertidas a puntos
    }

    private List<EstadisticaUsuario> obtenerEstadisticasFiltradas(Rol rol, PeriodoTipo periodo, Integer anio,
            Integer mes) {
        List<EstadisticaUsuario> todasEstadisticas = estadisticaUsuarioRepository.findAll();

        return todasEstadisticas.stream()
                .filter(e -> e.getTipoUsuario() == rol)
                .filter(e -> e.getPeriodoTipo() == periodo)
                .filter(e -> anio == null || e.getAnio() == anio)
                .filter(e -> mes == null || (e.getMes() != null && e.getMes().equals(mes)))
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertirAMetricasProductividad(EstadisticaUsuario estadistica) {
        Map<String, Object> metricas = new HashMap<>();

        metricas.put("usuarioId", estadistica.getUsuario().getId());
        metricas.put("nombre", estadistica.getUsuario().getNombre());
        metricas.put("email", estadistica.getUsuario().getEmail());
        metricas.put("rol", estadistica.getTipoUsuario());

        // Métricas de productividad
        metricas.put("ticketsCreados", estadistica.getTicketsCreados());
        metricas.put("ticketsEvaluados", estadistica.getTicketsEvaluados());
        metricas.put("tiempoConectadoHoras", estadistica.getTiempoConectadoMinutos() / 60.0);

        // Calcular eficiencia (tickets por hora)
        double horasConectadas = estadistica.getTiempoConectadoMinutos() / 60.0;
        double eficiencia = horasConectadas > 0
                ? (estadistica.getTicketsCreados() + estadistica.getTicketsEvaluados()) / horasConectadas
                : 0.0;

        metricas.put("eficienciaTicketsPorHora", Math.round(eficiencia * 100.0) / 100.0);

        return metricas;
    }

    private Map<String, Object> calcularMetricasAgregadas(List<EstadisticaUsuario> estadisticas) {
        Map<String, Object> metricas = new HashMap<>();

        if (estadisticas.isEmpty()) {
            return metricas;
        }

        int totalUsuarios = estadisticas.size();
        int ticketsCreados = estadisticas.stream().mapToInt(EstadisticaUsuario::getTicketsCreados).sum();
        int ticketsEvaluados = estadisticas.stream().mapToInt(EstadisticaUsuario::getTicketsEvaluados).sum();
        int tiempoTotal = estadisticas.stream().mapToInt(EstadisticaUsuario::getTiempoConectadoMinutos).sum();

        metricas.put("usuarios", totalUsuarios);
        metricas.put("ticketsCreados", ticketsCreados);
        metricas.put("ticketsEvaluados", ticketsEvaluados);
        metricas.put("tiempoTotalMinutos", tiempoTotal);
        metricas.put("promedioTicketsPorUsuario", totalUsuarios > 0 ? (double) ticketsCreados / totalUsuarios : 0.0);
        metricas.put("promedioTiempoPorUsuario", totalUsuarios > 0 ? (double) tiempoTotal / totalUsuarios : 0.0);

        return metricas;
    }
}
