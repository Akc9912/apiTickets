package com.poo.miapi.controller;

import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/crear-trabajador")
    public ResponseEntity<?> crearTrabajador(@RequestParam String nombre) {
        return ResponseEntity.ok(adminService.crearTrabajador(nombre));
    }

    @PostMapping("/crear-tecnico")
    public ResponseEntity<?> crearTecnico(@RequestParam String nombre) {
        return ResponseEntity.ok(adminService.crearTecnico(nombre));
    }

    @PostMapping("/bloquear-tecnico/{id}")
    public ResponseEntity<?> bloquearTecnico(@PathVariable int id) {
        adminService.bloquearTecnico(id);
        return ResponseEntity.ok("Técnico bloqueado");
    }

    @PostMapping("/desbloquear-tecnico/{id}")
    public ResponseEntity<?> desbloquearTecnico(@PathVariable int id) {
        adminService.desbloquearTecnico(id);
        return ResponseEntity.ok("Técnico desbloqueado");
    }

    @PostMapping("/blanquear-password/{idUsuario}")
    public ResponseEntity<?> blanquearPassword(@PathVariable int idUsuario) {
        adminService.blanquearPassword(idUsuario);
        return ResponseEntity.ok("Contraseña blanqueada");
    }

    @PostMapping("/reabrir-ticket")
    public ResponseEntity<?> reabrirTicket(@RequestParam int idTicket, @RequestParam int idTecnico) {
        adminService.reabrirTicket(idTicket, idTecnico);
        return ResponseEntity.ok("Ticket reabierto correctamente");
    }

    @GetMapping("/tickets/estado")
    public ResponseEntity<List<Ticket>> filtrarPorEstado(@RequestParam EstadoTicket estado) {
        return ResponseEntity.ok(adminService.filtrarPorEstado(estado));
    }

    @GetMapping("/tecnicos-bloqueados")
    public ResponseEntity<List<Tecnico>> obtenerTecnicosBloqueados() {
        return ResponseEntity.ok(adminService.obtenerTecnicosBloqueados());
    }
}
