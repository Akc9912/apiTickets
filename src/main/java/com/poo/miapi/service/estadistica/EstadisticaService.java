package com.poo.miapi.service.estadistica;

import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.core.UsuarioRepository;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TrabajadorRepository;
import com.poo.miapi.repository.historial.IncidenteTecnicoRepository;
import com.poo.miapi.repository.historial.TecnicoPorTicketRepository;
import com.poo.miapi.repository.estadistica.EstadisticaPeriodoRepository;
import com.poo.miapi.repository.estadistica.EstadisticaTecnicoRepository;
import com.poo.miapi.model.estadistica.EstadisticaPeriodo;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.model.enums.PeriodoTipo;
import com.poo.miapi.dto.estadistica.EstadisticaPeriodoDto;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class EstadisticaService {

    private static final Logger logger = LoggerFactory.getLogger(EstadisticaService.class);

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final TecnicoRepository tecnicoRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final IncidenteTecnicoRepository incidenteTecnicoRepository;
    private final TecnicoPorTicketRepository tecnicoPorTicketRepository;
    private final EstadisticaPeriodoRepository estadisticaPeriodoRepository;
    private final EstadisticaTecnicoRepository estadisticaTecnicoRepository;

    public EstadisticaService(
            TicketRepository ticketRepository,
            UsuarioRepository usuarioRepository,
            TecnicoRepository tecnicoRepository,
            TrabajadorRepository trabajadorRepository,
            IncidenteTecnicoRepository incidenteTecnicoRepository,
            TecnicoPorTicketRepository tecnicoPorTicketRepository,
            EstadisticaPeriodoRepository estadisticaPeriodoRepository,
            EstadisticaTecnicoRepository estadisticaTecnicoRepository) {
        this.ticketRepository = ticketRepository;
        this.usuarioRepository = usuarioRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.incidenteTecnicoRepository = incidenteTecnicoRepository;
        this.tecnicoPorTicketRepository = tecnicoPorTicketRepository;
        this.estadisticaPeriodoRepository = estadisticaPeriodoRepository;
        this.estadisticaTecnicoRepository = estadisticaTecnicoRepository;
    }

    public Object obtenerEstadisticasUsuarios() {
        // Lógica movida desde SuperAdminService
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", usuarioRepository.count());
        stats.put("usuariosActivos", usuarioRepository.countByActivoTrue());
        stats.put("usuariosBloqueados", usuarioRepository.countByBloqueadoTrue());
        stats.put("superAdmins", usuarioRepository.countByRol(Rol.SUPER_ADMIN));
        stats.put("admins", usuarioRepository.countByRol(Rol.ADMIN));
        stats.put("tecnicos", usuarioRepository.countByRol(Rol.TECNICO));
        stats.put("trabajadores", usuarioRepository.countByRol(Rol.TRABAJADOR));
        return stats;
    }

    public Object obtenerEstadisticasTickets() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTickets", ticketRepository.count());
        stats.put("ticketsNoAtendidos", ticketRepository.countByEstado(EstadoTicket.NO_ATENDIDO));
        stats.put("ticketsAtendidos", ticketRepository.countByEstado(EstadoTicket.ATENDIDO));
        stats.put("ticketsResueltos", ticketRepository.countByEstado(EstadoTicket.RESUELTO));
        stats.put("ticketsFinalizados", ticketRepository.countByEstado(EstadoTicket.FINALIZADO));
        stats.put("ticketsReabiertos", ticketRepository.countByEstado(EstadoTicket.REABIERTO));
        return stats;
    }

    public Object obtenerEstadisticasSistema() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("usuarios", obtenerEstadisticasUsuarios());
        stats.put("tickets", obtenerEstadisticasTickets());
        stats.put("tecnicosBloqueados", tecnicoRepository.countByBloqueadoTrue());
        return stats;
    }

    public int cantidadTotalTickets() {
        return (int) ticketRepository.count();
    }

    public int cantidadTicketsPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado).size();
    }

    public int cantidadTotalUsuarios() {
        return (int) usuarioRepository.count();
    }

    public int cantidadTotalTecnicos() {
        return (int) tecnicoRepository.count();
    }

    public int cantidadTotalTrabajadores() {
        return (int) trabajadorRepository.count();
    }

    public int cantidadIncidentesTecnicos() {
        return (int) incidenteTecnicoRepository.count();
    }

    // ========================================
    // NUEVOS MÉTODOS PARA ESTADÍSTICAS AVANZADAS
    // ========================================

    /**
     * CALCULAR ESTADÍSTICAS DIARIAS
     */
    public void calcularEstadisticasDiarias(LocalDateTime fecha) {
        try {
            logger.info("Calculando estadísticas diarias para fecha: {}", fecha.toLocalDate());

            int anio = fecha.getYear();
            int mes = fecha.getMonthValue();
            int dia = fecha.getDayOfMonth();

            // Buscar o crear estadística del día
            Optional<EstadisticaPeriodo> existente = estadisticaPeriodoRepository
                    .findByPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
                            PeriodoTipo.DIARIO, anio, mes, null, dia, null);

            EstadisticaPeriodo estadistica;
            if (existente.isPresent()) {
                estadistica = existente.get();
                logger.debug("Actualizando estadística existente del día {}/{}/{}", dia, mes, anio);
            } else {
                estadistica = new EstadisticaPeriodo(PeriodoTipo.DIARIO, anio);
                estadistica.setMes(mes);
                estadistica.setDia(dia);
                logger.debug("Creando nueva estadística para el día {}/{}/{}", dia, mes, anio);
            }

            // Calcular métricas del día
            LocalDateTime inicioDia = fecha.toLocalDate().atStartOfDay();
            LocalDateTime finDia = inicioDia.plusDays(1);

            // Tickets creados en el día
            long ticketsCreados = ticketRepository.countByFechaCreacionBetween(inicioDia, finDia);
            estadistica.setTicketsCreados((int) ticketsCreados);

            // Tickets resueltos en el día
            long ticketsResueltos = ticketRepository.countByEstadoAndFechaUltimaActualizacionBetween(
                    EstadoTicket.RESUELTO, inicioDia, finDia);
            estadistica.setTicketsResueltos((int) ticketsResueltos);

            // Tickets finalizados en el día
            long ticketsFinalizados = ticketRepository.countByEstadoAndFechaUltimaActualizacionBetween(
                    EstadoTicket.FINALIZADO, inicioDia, finDia);
            estadistica.setTicketsFinalizados((int) ticketsFinalizados);

            // Tickets reabiertos en el día
            long ticketsReabiertos = ticketRepository.countByEstadoAndFechaUltimaActualizacionBetween(
                    EstadoTicket.REABIERTO, inicioDia, finDia);
            estadistica.setTicketsReabiertos((int) ticketsReabiertos);

            // Tickets pendientes (estado actual)
            long ticketsPendientes = ticketRepository.countByEstadoIn(
                    List.of(EstadoTicket.NO_ATENDIDO, EstadoTicket.ATENDIDO));
            estadistica.setTicketsPendientes((int) ticketsPendientes);

            // Calcular porcentajes
            estadistica.calcularPorcentajeAprobacion();

            // Guardar
            estadisticaPeriodoRepository.save(estadistica);

            logger.info("Estadísticas diarias calculadas: {} tickets creados, {} resueltos",
                    ticketsCreados, ticketsResueltos);

        } catch (Exception e) {
            logger.error("Error calculando estadísticas diarias: {}", e.getMessage(), e);
            throw new RuntimeException("Error al calcular estadísticas diarias", e);
        }
    }

    /**
     * OBTENER ESTADÍSTICAS DEL MES ACTUAL
     */
    @Transactional(readOnly = true)
    public EstadisticaPeriodoDto getEstadisticasMesActual() {
        LocalDateTime ahora = LocalDateTime.now();
        int anio = ahora.getYear();
        int mes = ahora.getMonthValue();

        Optional<EstadisticaPeriodo> estadistica = estadisticaPeriodoRepository
                .findByPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
                        PeriodoTipo.MENSUAL, anio, mes, null, null, null);

        if (estadistica.isPresent()) {
            return convertirADto(estadistica.get());
        } else {
            // Crear estadística vacía si no existe
            EstadisticaPeriodoDto dto = new EstadisticaPeriodoDto();
            dto.setPeriodoTipo(PeriodoTipo.MENSUAL);
            dto.setAnio(anio);
            dto.setMes(mes);
            return dto;
        }
    }

    /**
     * OBTENER ESTADÍSTICAS POR PERÍODO
     */
    @Transactional(readOnly = true)
    public List<EstadisticaPeriodoDto> getEstadisticasPorPeriodo(PeriodoTipo tipo, int anio, Integer mes) {
        List<EstadisticaPeriodo> estadisticas;

        if (mes != null) {
            estadisticas = estadisticaPeriodoRepository.findByPeriodoTipoAndAnioAndMes(tipo, anio, mes);
        } else {
            estadisticas = estadisticaPeriodoRepository.findByPeriodoTipoAndAnio(tipo, anio);
        }

        return estadisticas.stream()
                .map(this::convertirADto)
                .toList();
    }

    /**
     * CONVERTIR ENTIDAD A DTO
     */
    private EstadisticaPeriodoDto convertirADto(EstadisticaPeriodo estadistica) {
        EstadisticaPeriodoDto dto = new EstadisticaPeriodoDto();

        dto.setId(estadistica.getId());
        dto.setPeriodoTipo(estadistica.getPeriodoTipo());
        dto.setAnio(estadistica.getAnio());
        dto.setMes(estadistica.getMes());
        dto.setSemana(estadistica.getSemana());
        dto.setDia(estadistica.getDia());
        dto.setTrimestre(estadistica.getTrimestre());

        dto.setTicketsCreados(estadistica.getTicketsCreados());
        dto.setTicketsAsignados(estadistica.getTicketsAsignados());
        dto.setTicketsResueltos(estadistica.getTicketsResueltos());
        dto.setTicketsFinalizados(estadistica.getTicketsFinalizados());
        dto.setTicketsReabiertos(estadistica.getTicketsReabiertos());
        dto.setTicketsPendientes(estadistica.getTicketsPendientes());

        dto.setTiempoPromedioAsignacion(estadistica.getTiempoPromedioAsignacion());
        dto.setTiempoPromedioResolucion(estadistica.getTiempoPromedioResolucion());
        dto.setTiempoMaximoResolucion(estadistica.getTiempoMaximoResolucion());
        dto.setTiempoMinimoResolucion(estadistica.getTiempoMinimoResolucion());

        dto.setTicketsAprobados(estadistica.getTicketsAprobados());
        dto.setTicketsRechazados(estadistica.getTicketsRechazados());
        dto.setPorcentajeAprobacion(estadistica.getPorcentajeAprobacion());

        dto.setSolicitudesDevolucion(estadistica.getSolicitudesDevolucion());
        dto.setDevolucionesAprobadas(estadistica.getDevolucionesAprobadas());
        dto.setDevolucionesRechazadas(estadistica.getDevolucionesRechazadas());

        dto.setFechaCalculo(estadistica.getFechaCalculo());
        dto.setUltimaActualizacion(estadistica.getUltimaActualizacion());

        return dto;
    }

    /**
     * FORZAR RECÁLCULO DE ESTADÍSTICAS
     */
    public void recalcularEstadisticas() {
        logger.info("Iniciando recálculo completo de estadísticas");

        LocalDateTime ahora = LocalDateTime.now();

        // Recalcular hoy
        calcularEstadisticasDiarias(ahora);

        logger.info("Recálculo de estadísticas completado");
    }
}
