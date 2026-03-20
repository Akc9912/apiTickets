package com.poo.miapi.module.user.service;

import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.ticket.service.TicketService;
import com.poo.miapi.module.user.dto.DeveloperResponseDto;
import com.poo.miapi.module.user.dto.UserRequestDto;
import com.poo.miapi.module.user.model.Developer;
import com.poo.miapi.module.user.repository.DeveloperRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.poo.miapi.shared.util.PasswordHelper;

import java.util.List;

@Service
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TicketService ticketService;

    @Value("${app.default-password}")
    private String defaultPassword;

    public DeveloperService(
            DeveloperRepository developerRepository,
            PasswordEncoder passwordEncoder, TicketService ticketService) {
        this.developerRepository = developerRepository;
        this.passwordEncoder = passwordEncoder;
        this.ticketService = ticketService;
    }

    // MÉTODOS PÚBLICOS
    // Buscar developer por ID
    public Developer findById(int developerId) {
        return developerRepository.findById(developerId)
                .orElseThrow(() -> new EntityNotFoundException("Developer no encontrado"));
    }

    // Buscar developer por email
    public Developer findByEmail(String email) {
        return developerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Developer no encontrado"));
    }

    // Incrementar failures del developer
    public void incrementFailure(Developer developer) {
        developer.setFailures(developer.getFailures() + 1);
        if (developer.getFailures() >= 2) {
            developer.setBlocked(true);
        }
        developerRepository.save(developer);
    }

    // Incrementar warnings del developer
    public void incrementWarning(Developer developer) {
        developer.setWarnings(developer.getWarnings() + 1);
        if (developer.getWarnings() >= 2) {
            developer.setWarnings(0);
            incrementFailure(developer);
        }
        developerRepository.save(developer);
    }

    // Reiniciar failures y warnings del developer
    public void resetFailuresAndWarnings(Developer developer) {
        developer.setFailures(0);
        developer.setWarnings(0);
        developer.setBlocked(false);
        developerRepository.save(developer);
    }

    // Reiniciar failures y warnings por ID de developer
    public void resetFailuresAndWarnings(int developerId) {
        Developer developer = findById(developerId);
        resetFailuresAndWarnings(developer);
    }

    // Listar todos los developers
    public List<DeveloperResponseDto> findAll() {
        return developerRepository.findAll().stream()
                .map(this::mapToDeveloperDto)
                .toList();
    }

    // Listar developers activos
    public List<DeveloperResponseDto> findActive() {
        return developerRepository.findByActiveTrue().stream()
                .map(this::mapToDeveloperDto)
                .toList();
    }

    // Listar developers bloqueados
    public List<DeveloperResponseDto> findBlocked() {
        return developerRepository.findByBlockedTrue().stream()
                .map(this::mapToDeveloperDto)
                .toList();
    }

    // Contar developers bloqueados
    public long countBlocked() {
        return developerRepository.countByBlockedTrue();
    }

    // Obtener datos del developer
    public DeveloperResponseDto getDetails(int developerId) {
        Developer developer = findById(developerId);
        return mapToDeveloperDto(developer);
    }

    // Editar datos del developer
    public DeveloperResponseDto updateData(int developerId, UserRequestDto userDto) {
        Developer developer = findById(developerId);
        developer.setName(userDto.getName());
        developer.setLastName(userDto.getLastName());
        developer.setEmail(userDto.getEmail());
        developerRepository.save(developer);

        return mapToDeveloperDto(developer);
    }

    // Resetear contraseña a la por defecto
    public DeveloperResponseDto resetPassword(int developerId) {
        Developer developer = findById(developerId);
        String rawPassword = PasswordHelper.generarPasswordPorDefecto(developer.getLastName());
        developer.setPassword(passwordEncoder.encode(rawPassword));
        developer.setChangePassword(true);
        developerRepository.save(developer);
        return mapToDeveloperDto(developer);
    }

    // Desbloquear developer
    public DeveloperResponseDto unblockDeveloper(int developerId) {
        Developer developer = findById(developerId);
        if (developer.isBlocked()) {
            developer.setBlocked(false);
            developer.setActive(true);
            resetFailuresAndWarnings(developer);
        }
        return mapToDeveloperDto(developer);
    }

    // Buscar developers por nombre
    public List<DeveloperResponseDto> findByName(String name) {
        return developerRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToDeveloperDto)
                .toList();
    }

    // Buscar developers por apellido
    public List<DeveloperResponseDto> findByLastName(String lastName) {
        return developerRepository.findByLastNameContainingIgnoreCase(lastName).stream()
                .map(this::mapToDeveloperDto)
                .toList();
    }

    // Operaciones sobre tickets (pendiente de implementación de negocio)
    public void takeTicket(int developerId, int ticketId) {
        Ticket t = ticketService.findById(ticketId);
        Developer d = developerRepository.findById(developerId);

        


    }

    public void resolveTicket(int developerId, int ticketId, String comment) {
        throw new UnsupportedOperationException("Resolver ticket no implementado en DeveloperService");
    }

    public void requestTicketReturn(int developerId, int ticketId, String reason) {
        throw new UnsupportedOperationException("Devolver ticket no implementado en DeveloperService");
    }

    // MÉTODOS PRIVADOS/UTILIDADES
    private DeveloperResponseDto mapToDeveloperDto(Developer developer) {
        return new DeveloperResponseDto(
                developer.getId(),
                developer.getName(),
                developer.getLastName(),
                developer.getEmail(),
                developer.getRole(),
                developer.isChangePassword(),
                developer.isActive(),
                developer.isBlocked(),
                developer.getFailures(),
                developer.getWarnings(),
                null); // No lista de incidentes
    }
}
