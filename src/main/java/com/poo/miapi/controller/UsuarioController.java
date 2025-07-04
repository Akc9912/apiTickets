package com.poo.miapi.controller;

import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.core.Admin;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Trabajador;
import com.poo.miapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // --- Autenticación ---

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestParam String email, @RequestParam String password) {
        Usuario usuario = usuarioService.login(email, password);
        return ResponseEntity.ok(usuario);
    }

    // --- Creación de usuarios ---

    @PostMapping("/admin")
    public ResponseEntity<Admin> crearAdmin(@RequestBody Usuario request) {
        Admin nuevo = usuarioService.crearAdmin(request.getNombre(), request.getApellido(), request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PostMapping("/tecnico")
    public ResponseEntity<Tecnico> crearTecnico(@RequestBody Usuario request) {
        Tecnico nuevo = usuarioService.crearTecnico(request.getNombre(), request.getApellido(), request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PostMapping("/trabajador")
    public ResponseEntity<Trabajador> crearTrabajador(@RequestBody Usuario request) {
        Trabajador nuevo = usuarioService.crearTrabajador(request.getNombre(), request.getApellido(),
                request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // --- Listado y consulta ---

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Usuario>> listarActivos() {
        return ResponseEntity.ok(usuarioService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // --- Edición ---

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable int id, @RequestBody Usuario request) {
        Usuario actualizado = usuarioService.actualizarDatos(id, request.getNombre(), request.getApellido(),
                request.getEmail());
        return ResponseEntity.ok(actualizado);
    }

    // --- Cambio de contraseña ---

    @PostMapping("/{id}/password")
    public ResponseEntity<Void> cambiarPassword(@PathVariable int id, @RequestParam String nuevaPassword) {
        usuarioService.cambiarPassword(id, nuevaPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reiniciar-password")
    public ResponseEntity<Void> reiniciarPassword(@PathVariable int id) {
        usuarioService.reiniciarPassword(id);
        return ResponseEntity.ok().build();
    }

    // --- Bloqueo / Activación ---

    @PostMapping("/{id}/bloquear")
    public ResponseEntity<Void> bloquear(@PathVariable int id) {
        usuarioService.bloquearUsuario(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/desbloquear")
    public ResponseEntity<Void> desbloquear(@PathVariable int id) {
        usuarioService.desbloquearUsuario(id);
        return ResponseEntity.ok().build();
    }

    // --- Eliminación lógica ---

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // --- Utilidad ---

    @GetMapping("/{id}/tipo")
    public ResponseEntity<String> getTipoUsuario(@PathVariable int id) {
        return ResponseEntity.ok(usuarioService.getTipoUsuario(id));
    }
}
