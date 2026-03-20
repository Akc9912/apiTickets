package com.poo.miapi.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Tickets Management System")
                        .description("Sistema completo de gestión de tickets con soporte multi-rol. " +
                                "Incluye autenticación JWT, gestión de usuarios (SuperAdmin, Admin, Developer, Support), "
                                +
                                "creación y seguimiento de tickets, solicitudes de devolución, historial de evaluaciones y más.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support Team")
                                .email("support@tickets.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.tickets.com")
                                .description("Production Server")));
    }
}
