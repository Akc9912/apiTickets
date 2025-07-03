package com.poo.miapi.service;

import com.poo.miapi.model.*;
import com.poo.miapi.repository.TicketRepository;
import com.poo.miapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todos los tickets
    public List<Ticket> obtenerTodos() {
        return ticketRepository.findAll();
    }

    // Buscar ticket por ID
    public Ticket buscarPorId(int id) {
        return ticketRepository.findById(id).orElse(null);
    }

    // Crear y guardar un nuevo ticket
    public Ticket crearTicket(String titulo, String descripcion, int idTrabajador) {
        Trabajador trabajador = (Trabajador) usuarioRepository.findById(idTrabajador)
                .filter(u -> u instanceof Trabajador)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado."));

        Ticket nuevo = new Ticket(titulo, descripcion, trabajador);
        return ticketRepository.save(nuevo);
    }

    // Tickets por estado
    public List<Ticket> obtenerPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado);
    }

    // Tickets por técnico
    public List<Ticket> obtenerPorTecnico(int idTecnico) {
        Tecnico tecnico = (Tecnico) usuarioRepository.findById(idTecnico)
                .filter(u -> u instanceof Tecnico)
                .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado."));

        return ticketRepository.findByTecnicoActual(tecnico);
    }

    // Tickets por trabajador
    public List<Ticket> obtenerPorTrabajador(int idTrabajador) {
        Trabajador trabajador = (Trabajador) usuarioRepository.findById(idTrabajador)
                .filter(u -> u instanceof Trabajador)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado."));

        return ticketRepository.findByCreador(trabajador);
    }

    // Buscar por palabra en título
    public List<Ticket> buscarPorTitulo(String texto) {
        return ticketRepository.findByTituloContainingIgnoreCase(texto);
    }

    // Tickets sin técnico asignado
    public List<Ticket> obtenerDisponibles() {
        return ticketRepository.findByTecnicoActualIsNull();
    }

    // Guardar cambios (resolver, reabrir, etc.)
    public Ticket actualizar(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    // Eliminar ticket (opcional)
    public void eliminarTicket(int id) {
        ticketRepository.deleteById(id);
    }
}
