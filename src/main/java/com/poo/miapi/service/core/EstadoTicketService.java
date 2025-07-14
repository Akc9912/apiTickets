package com.poo.miapi.service.core;

import com.poo.miapi.dto.ticket.EstadoTicketResponseDto;
import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.repository.core.EstadoTicketRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoTicketService {

    @Autowired
    private EstadoTicketRepository estadoTicketRepository;

    // Listar todos los estados como DTOs
    public List<EstadoTicketResponseDto> listarEstados() {
        return estadoTicketRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    // Verificar existencia de estado
    public boolean existeEstado(String idEstado) {
        return estadoTicketRepository.existsById(idEstado);
    }

    // Obtener estado por ID como DTO
    public EstadoTicketResponseDto obtenerPorId(String idEstado) {
        EstadoTicket estado = estadoTicketRepository.findById(idEstado)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
        return mapToDto(estado);
    }

    // Método auxiliar para mapear entidad a DTO
    private EstadoTicketResponseDto mapToDto(EstadoTicket estado) {
        return new EstadoTicketResponseDto(
                estado.getId(),
                estado.getNombre(),
                estado.getDescripcion());
    }
}