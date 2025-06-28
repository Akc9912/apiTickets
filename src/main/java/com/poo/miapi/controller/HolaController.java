package com.poo.miapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaController {

    @GetMapping("/hola")
    public String saludar() {
        return "¡Hola desde tu API Spring Boot!";
    }
}
