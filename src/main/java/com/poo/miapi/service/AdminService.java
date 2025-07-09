package com.poo.miapi.service;

import com.poo.miapi.dto.CrearUsuarioDto;
import com.poo.miapi.dto.EditarUsuarioDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.historial.TecnicoPorTicket;
import com.poo.miapi.repository.TecnicoPorTicketRepository;
import com.poo.miapi.repository.TecnicoRepository;
import com.poo.miapi.repository.TicketRepository;
import com.poo.miapi.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TecnicoRepository tecnicoRepository;
    @Autowired
    private TecnicoPorTicketRepository tecnicoPorTicketRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TecnicoService tecnicoService;
    @Autowired
    private TecnicoPorTicketService tecnicoPorTicketService;

    // =========================
    // USUARIOS
    // =========================

    // Crear usuario
    public String crearUsuario(CrearUsuarioDto usuarioDto) {
        validarDatosUsuario(usuarioDto);

        if (usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        Usuario nuevoUsuario = crearUsuarioPorRol(usuarioDto);
        nuevoUsuario.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
        nuevoUsuario.setRol(usuarioDto.getRol().toUpperCase());
        usuarioRepository.save(nuevoUsuario);

        return "Usuario creado con éxito: " + nuevoUsuario.getEmail();
    }

    // Editar usuario
    public String editarUsuario(Long id, EditarUsuarioDto usuarioDto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        validarDatosUsuario(usuarioDto);

        if (usuarioRepository.existsByEmail(usuarioDto.getEmail()) &&
                !usuario.getEmail().equals(usuarioDto.getEmail())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        usuario.setNombre(usuarioDto.getNombre());
        usuario.setApellido(usuarioDto.getApellido());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setPassword(passwordEncoder.encode((CharSequence) usuarioDto.getPassword()));
        usuario.setRol(((String) usuarioDto.getRol()).toUpperCase());
        usuarioRepository.save(usuario);

        return "Usuario editado con éxito: " + usuario.getEmail();
    }

    // Cambiar rol de usuario (crea nuevo usuario y da de baja lógica al anterior)
    public String cambiarRolUsuario(int id, EditarUsuarioDto cambiarRolDto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        validarRol(cambiarRolDto.getRol());

        Usuario nuevoUsuario = crearUsuarioPorRol(cambiarRolDto);
        nuevoUsuario.setPassword(passwordEncoder.encode(cambiarRolDto.getPassword()));
        nuevoUsuario.setRol(cambiarRolDto.getRol().toUpperCase());
        usuarioRepository.save(nuevoUsuario);

        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        return "Rol de usuario cambiado con éxito: " + nuevoUsuario.getEmail();
    }

    // Activar usuario
    public String activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        return "Usuario activado con éxito: " + usuario.getEmail();
    }

    // Desactivar usuario
    public String desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        return "Usuario desactivado con éxito: " + usuario.getEmail();
    }

    // Bloquear usuario
    public String bloquearUsuario(long idUsuario) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        u.setBloqueado(true);
        usuarioRepository.save(u);
        return "Usuario bloqueado con éxito: " + u.getEmail();
    }

    // Desbloquear usuario
    public String desbloquearUsuario(int idUsuario) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        u.setBloqueado(false);
        usuarioRepository.save(u);
        if (u instanceof Tecnico t) {
            tecnicoService.reiniciarFallasYMarcas(t);
        }
        return "Usuario desbloqueado con éxito: " + u.getEmail();
    }

    // Blanquear password
    public String blanquearPassword(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(String.valueOf(usuario.getId())));
        usuario.setCambiarPass(true);
        usuarioRepository.save(usuario);
        return "Contraseña reiniciada con éxito para el usuario: " + usuario.getEmail();
    }

    // =========================
    // CONSULTAS DE USUARIOS
    // =========================

    public List<Usuario> listarTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario verUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + id));
    }

    public List<Usuario> listarUsuariosPorRol(String rol) {
        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException("El rol no puede ser nulo o vacío");
        }
        return usuarioRepository.findByRol(rol.toUpperCase());
    }

    // =========================
    // TICKETS
    // =========================

    public List<Ticket> listarTodosLosTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> filtrarTicketsPorEstado(EstadoTicket estado) {
        return ticketRepository.findByEstado(estado);
    }

    // Reabrir ticket
    public void reabrirTicket(Integer idTicket, String comentario) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado con ID: " + idTicket));

        if (ticket.getEstado() != EstadoTicket.FINALIZADO) {
            throw new IllegalArgumentException("El ticket no está cerrado, no se puede reabrir");
        }

        Tecnico tecnicoActual = ticket.getTecnicoActual();
        if (tecnicoActual == null) {
            throw new IllegalArgumentException("No hay técnico asignado al ticket, no se puede reabrir");
        }

        TecnicoPorTicket entradaHistorial = tecnicoPorTicketService.buscarEntradaHistorialPorTicket(tecnicoActual,
                ticket);
        if (entradaHistorial == null) {
            throw new IllegalArgumentException("No se encontró historial para este ticket en el técnico");
        }

        entradaHistorial.setEstadoFinal(EstadoTicket.REABIERTO);
        entradaHistorial.setFechaDesasignacion(LocalDateTime.now());
        entradaHistorial.setComentario(comentario);
        tecnicoPorTicketRepository.save(entradaHistorial);

        tecnicoActual.setFallas(tecnicoActual.getFallas() + 1);
        tecnicoRepository.save(tecnicoActual);

        ticket.setEstado(EstadoTicket.REABIERTO);
        ticket.setFechaUltimaActualizacion(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    // =========================
    // MÉTODOS AUXILIARES PRIVADOS
    // =========================

    private void validarDatosUsuario(CrearUsuarioDto usuarioDto) {
        if (usuarioDto.getNombre() == null || usuarioDto.getApellido() == null ||
                usuarioDto.getEmail() == null || usuarioDto.getPassword() == null ||
                usuarioDto.getRol() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
        validarRol(usuarioDto.getRol());
    }

    private void validarDatosUsuario(EditarUsuarioDto usuarioDto) {
        if (usuarioDto.getNombre() == null || usuarioDto.getApellido() == null ||
                usuarioDto.getEmail() == null || usuarioDto.getPassword() == null ||
                usuarioDto.getRol() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
        validarRol((String) usuarioDto.getRol());
    }

    private void validarRol(String rol) {
        if (!rol.equalsIgnoreCase("ADMIN") &&
                !rol.equalsIgnoreCase("TECNICO") &&
                !rol.equalsIgnoreCase("TRABAJADOR")) {
            throw new IllegalArgumentException("Rol no válido: " + rol);
        }
    }

    private Usuario crearUsuarioPorRol(CrearUsuarioDto usuarioDto) {
        switch (usuarioDto.getRol().toUpperCase()) {
            case "ADMIN":
                return crearAdmin(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            case "TECNICO":
                return crearTecnico(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            case "TRABAJADOR":
                return crearTrabajador(usuarioDto.getNombre(), usuarioDto.getApellido(), usuarioDto.getEmail());
            default:
                throw new IllegalArgumentException("Rol no válido: " + usuarioDto.getRol());
        }
    }

    public Admin crearAdmin(String nombre, String apellido, String email) {
        return new Admin(nombre, apellido, email);
    }

    public Tecnico crearTecnico(String nombre, String apellido, String email) {
        return new Tecnico(nombre, apellido, email);
    }

    public Trabajador crearTrabajador(String nombre, String apellido, String email) {
        return new Trabajador(nombre, apellido, email);
    }
}
