package com.poo.miapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiapiApplication {
    public static void main(String[] args) {
        // Cargar variables del archivo .env
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .ignoreIfMissing()
                .load();
        
        // Establecer las variables de entorno del sistema
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        
        SpringApplication.run(MiapiApplication.class, args);
        System.out.println("API Tickets is running...");
        System.out.println("JWT Secret loaded from .env: " + (System.getProperty("JWT_SECRET") != null ? "✓" : "✗"));
        System.out.println("🔑 Contraseña por defecto: [Apellido]123 (usuarios deben cambiar en primer inicio)");
    }
}
