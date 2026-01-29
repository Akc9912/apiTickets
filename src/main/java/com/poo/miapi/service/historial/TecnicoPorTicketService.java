package com.poo.miapi.service.historial;

import com.poo.miapi.dto.historial.TecnicoPorTicketResponseDto;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.historial.TecnicoPorTicket;
import com.poo.miapi.repository.historial.TecnicoPorTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.poo.miapi.service.auditoria.AuditoriaService;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.SeveridadAuditoria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TecnicoPorTicketService {

    @Autowired
    private final TecnicoPorTicketRepository tecnicoPorTicketRepository;
    private final AuditoriaService auditoriaService;

    public TecnicoPorTicketService(TecnicoPorTicketRepository tecnicoPorTicketRepository,
            AuditoriaService auditoriaService) {
        this.tecnicoPorTicketRepository = tecnicoPorTicketRepository;
        this.auditoriaService = auditoriaService;
    }

    // MÉTODOS PÚBLICOS
    // Registrar toma de ticket
    public TecnicoPorTicket registrarToma(Ticket ticket, Tecnico tecnico) {
        // El estado inicial será ATENDIDO, el estado final queda null
        TecnicoPorTicket historial = new TecnicoPorTicket(ticket, tecnico, EstadoTicket.ATENDIDO, null, null);
        TecnicoPorTicket savedHistorial = tecnicoPorTicketRepository.save(historial);

        // Auditar registro de toma de ticket
        auditoriaService.registrarAccion(
                tecnico,
                AccionAuditoria.ASSIGN_TICKET,
                "HISTORIAL_TECNICO",
                savedHistorial.getId(),
                "Técnico " + tecnico.getNombre() + " tomó ticket: " + ticket.getTitulo(),
                null,
                savedHistorial,
                CategoriaAuditoria.BUSINESS,
                SeveridadAuditoria.MEDIUM);

        return savedHistorial;
    }

    // Registrar resolución de ticket
    public void registrarResolucion(int idTecnico, int idTicket) {
        Optional<TecnicoPorTicket> optHistorial = tecnicoPorTicketRepository
                .findByTecnicoIdAndTicketIdAndFechaDesasignacionIsNull(idTecnico, idTicket);
        if (optHistorial.isPresent()) {
            TecnicoPorTicket historial = optHistorial.get();
            historial.setFechaDesasignacion(LocalDateTime.now());
            historial.setEstadoFinal(EstadoTicket.RESUELTO);
            TecnicoPorTicket savedHistorial = tecnicoPorTicketRepository.save(historial);

            // Auditar registro de resolución
            auditoriaService.registrarAccion(
                    historial.getTecnico(),
                    AccionAuditoria.RESOLVE_TICKET,
                    "HISTORIAL_TECNICO",
                    savedHistorial.getId(),
                    "Técnico " + historial.getTecnico().getNombre() + " resolvió ticket: "
                            + historial.getTicket().getTitulo(),
                    EstadoTicket.ATENDIDO,
                    EstadoTicket.RESUELTO,
                    CategoriaAuditoria.BUSINESS,
                    SeveridadAuditoria.LOW);
        }
    }

    // Registrar devolución de ticket
    public void registrarDevolucion(Tecnico tecnico, Ticket ticket) {
        Optional<TecnicoPorTicket> optHistorial = tecnicoPorTicketRepository
                .findByTecnicoAndTicketAndFechaDesasignacionIsNull(tecnico, ticket);
        if (optHistorial.isPresent()) {
            TecnicoPorTicket historial = optHistorial.get();
            historial.setFechaDesasignacion(LocalDateTime.now());
            historial.setEstadoFinal(EstadoTicket.REABIERTO);
            tecnicoPorTicketRepository.save(historial);
        }
    }

    // Buscar historial por técnico y ticket (devuelve DTO)
    public Optional<TecnicoPorTicketResponseDto> buscarEntradaHistorialPorTicket(Tecnico tecnico, Ticket ticket) {
        return tecnicoPorTicketRepository.findByTecnicoAndTicket(tecnico, ticket)
                .map(this::mapToDto);
    }

    // Guardar historial (devuelve DTO)
    public TecnicoPorTicketResponseDto guardar(TecnicoPorTicket historial) {
        TecnicoPorTicket saved = tecnicoPorTicketRepository.save(historial);
        return mapToDto(saved);
    }

    // Listar historial por técnico (devuelve DTOs)
    public List<TecnicoPorTicketResponseDto> listarPorTecnico(Tecnico tecnico) {
        return tecnicoPorTicketRepository.findByTecnico(tecnico).stream()
                .map(this::mapToDto)
                .toList();
    }

    // Listar historial por ticket (devuelve DTOs)
    public List<TecnicoPorTicketResponseDto> listarPorTicket(Ticket ticket) {
        return tecnicoPorTicketRepository.findByTicket(ticket).stream()
                .map(this::mapToDto)
                .toList();
    }

    // Listar historial por idTecnico
    public List<TecnicoPorTicketResponseDto> listarPorIdTecnico(int idTecnico) {
        return tecnicoPorTicketRepository.findByTecnicoId(idTecnico).stream()
                .map(this::mapToDto)
                .toList();
    }

    // Listar historial por idTicket
    public List<TecnicoPorTicketResponseDto> listarPorIdTicket(int idTicket) {
        return tecnicoPorTicketRepository.findByTicketId(idTicket).stream()
                .map(this::mapToDto)
                .toList();
    }

    // MÉTODOS PRIVADOS/UTILIDADES
    // Método auxiliar para mapear entidad a DTO
    private TecnicoPorTicketResponseDto mapToDto(TecnicoPorTicket historial) {
        return new TecnicoPorTicketResponseDto(
                historial.getId(),
                historial.getTicket().getId(),
                historial.getTecnico().getId(),
                historial.getEstadoInicial(),
                historial.getEstadoFinal(),
                historial.getFechaAsignacion(),
                historial.getFechaDesasignacion(),
                historial.getComentario());
    }
}
