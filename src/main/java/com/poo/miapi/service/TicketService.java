package com.poo.miapi.service;

import com.poo.miapi.model.*;
import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Trabajador;
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

    // Métodos funcionales
    public boolean puedeSerTomado() {
        return this.estado == EstadoTicket.NO_ATENDIDO;
    }

    public void asignarTecnico(Tecnico tecnico) {
        if (!puedeSerTomado()) {
            throw new IllegalStateException("El ticket ya está siendo atendido.");
        }
        this.tecnicoActual = tecnico;
        this.estado = EstadoTicket.ATENDIDO;
    }

    public void desasignarTecnico() {
        this.tecnicoAnterior = this.tecnicoActual;
        this.tecnicoActual = null;
        this.estado = EstadoTicket.NO_ATENDIDO;
    }

    public void marcarResuelto() {
        if (this.estado != EstadoTicket.ATENDIDO) {
            throw new IllegalStateException("Solo se pueden resolver tickets en estado 'ATENDIDO'.");
        }
        this.estado = EstadoTicket.RESUELTO;
    }

    public void marcarFinalizado() {
        if (this.estado != EstadoTicket.RESUELTO) {
            throw new IllegalStateException("Solo se pueden finalizar tickets en estado 'RESUELTO'.");
        }
        this.estado = EstadoTicket.FINALIZADO;
    }

    public void marcarReabierto() {
        if (this.estado != EstadoTicket.RESUELTO) {
            throw new IllegalStateException("Solo se pueden reabrir tickets en estado 'RESUELTO'.");
        }
        this.tecnicoAnterior = this.tecnicoActual;
        this.tecnicoActual = null;
        this.estado = EstadoTicket.REABIERTO;
    }

    public void reabrir(Tecnico tecnicoQueLoPide) {
        if (!tecnicoQueLoPide.equals(this.tecnicoActual)) {
            throw new IllegalArgumentException("Solo el técnico actual puede pedir la reapertura.");
        }
        this.tecnicoAnterior = this.tecnicoActual;
        this.tecnicoActual = null;
        this.estado = EstadoTicket.REABIERTO;
    }
}
