package com.poo.miapi.service.historial;

import com.poo.miapi.dto.historial.TecnicoPorTicketResponseDto;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.historial.TecnicoPorTicket;
import com.poo.miapi.repository.historial.TecnicoPorTicketRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TecnicoPorTicketService {

    @Autowired
    private TecnicoPorTicketRepository tecnicoPorTicketRepository;

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