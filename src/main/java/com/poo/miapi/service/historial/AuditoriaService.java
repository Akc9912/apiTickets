package com.poo.miapi.service.historial;

import com.poo.miapi.dto.historial.AuditoriaResponseDto;
import com.poo.miapi.model.historial.Auditoria;
import com.poo.miapi.repository.historial.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditoriaService {

    @Autowired
    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    // Registrar una acción de auditoría
    public AuditoriaResponseDto registrar(String usuario, String accion, String entidad, String detalle) {
        Auditoria auditoria = new Auditoria(usuario, accion, entidad, detalle);
        Auditoria saved = auditoriaRepository.save(auditoria);
        return mapToDto(saved);
    }

    // Listar todas las auditorías
    public List<AuditoriaResponseDto> listarTodas() {
        return auditoriaRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Buscar por usuario
    public List<AuditoriaResponseDto> listarPorUsuario(String usuario) {
        return auditoriaRepository.findByUsuario(usuario).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Buscar por entidad
    public List<AuditoriaResponseDto> listarPorEntidad(String entidad) {
        return auditoriaRepository.findByEntidad(entidad).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Buscar por acción
    public List<AuditoriaResponseDto> listarPorAccion(String accion) {
        return auditoriaRepository.findByAccion(accion).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Buscar por rango de fechas
    public List<AuditoriaResponseDto> listarPorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return auditoriaRepository.findByFechaBetween(desde, hasta).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Método auxiliar para mapear entidad a DTO
    private AuditoriaResponseDto mapToDto(Auditoria auditoria) {
        return new AuditoriaResponseDto(
                auditoria.getId(),
                auditoria.getUsuario(),
                auditoria.getAccion(),
                auditoria.getEntidad(),
                auditoria.getDetalle(),
                auditoria.getFecha());
    }
}
