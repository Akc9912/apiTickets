package com.poo.miapi.controller;

import com.poo.miapi.model.*;
import com.poo.miapi.service.AdminService;
import com.poo.miapi.service.GestorDeUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private GestorDeUsuarios gestorUsuarios;

    @PostMapping("/crear-trabajador")
    public Trabajador crearTrabajador(@RequestParam String nombre) {
        return adminService.crearTrabajador(nombre);
    }

    @PostMapping("/crear-tecnico")
    public Tecnico crearTecnico(@RequestParam String nombre) {
        return adminService.crearTecnico(nombre);
    }

    @PutMapping("/blanquear-password/{id}")
    public String blanquearPassword(@PathVariable int id) {
        Usuario u = gestorUsuarios.buscarPorId(id);
        if (u == null)
            return "Usuario no encontrado.";
        adminService.blanquearPassword(u);
        return "Contraseña blanqueada.";
    }

    @PutMapping("/bloquear-tecnico/{id}")
    public String bloquearTecnico(@PathVariable int id) {
        adminService.bloquearTecnico(id);
        return "Técnico bloqueado.";
    }

    @PutMapping("/desbloquear-tecnico/{id}")
    public String desbloquearTecnico(@PathVariable int id) {
        adminService.desbloquearTecnico(id);
        return "Técnico desbloqueado.";
    }

    @PutMapping("/reabrir-ticket")
    public String reabrirTicket(@RequestParam int idTicket, @RequestParam int idTecnico) {
        adminService.reabrirTicket(idTicket, idTecnico);
        return "Ticket reabierto.";
    }
}
