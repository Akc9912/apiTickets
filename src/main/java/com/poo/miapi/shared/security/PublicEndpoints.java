package com.poo.miapi.shared.security;

import java.util.List;
import java.util.Set;

/**
 * Única fuente de verdad de qué rutas son públicas.
 *
 * Antes la lista estaba duplicada entre SecurityConfig y JwtAuthenticationFilter, y las dos
 * copias decían `/api/auth/**` completo. Eso dejaba change-password accesible sin
 * autenticarse. Con una sola lista, agregar un endpoint público es una decisión explícita en
 * un solo lugar, y lo que no esté acá requiere token.
 */
public final class PublicEndpoints {

    /**
     * Endpoints de auth abiertos. Coincidencia EXACTA, no por prefijo: /api/auth/v1/logout y
     * /api/auth/v1/change-password quedan afuera a propósito y necesitan token.
     */
    public static final Set<String> AUTH_PUBLIC = Set.of(
            "/api/auth/v1/register",
            "/api/auth/v1/verify-email",
            "/api/auth/v1/resend-code",
            "/api/auth/v1/login",
            "/api/auth/v1/refresh",
            "/api/auth/v1/forgot-password",
            "/api/auth/v1/reset-password");

    /** Infraestructura y documentación. Acá sí por prefijo. */
    public static final List<String> INFRA_PREFIXES = List.of(
            "/swagger-ui",
            "/v3/api-docs",
            "/api-docs",
            "/swagger-resources",
            "/webjars/",
            "/actuator/");

    public static final Set<String> STATIC_EXACT = Set.of("/", "/index.html");

    private PublicEndpoints() {
    }

    /** Patrones para los requestMatchers de SecurityConfig. */
    public static String[] authPublicPatterns() {
        return AUTH_PUBLIC.toArray(String[]::new);
    }

    public static String[] infraPatterns() {
        return INFRA_PREFIXES.stream().map(p -> p.endsWith("/") ? p + "**" : p + "/**")
                .toArray(String[]::new);
    }

    /** Lo que usa el filtro para decidir si ni siquiera mira el header Authorization. */
    public static boolean isPublic(String path) {
        if (path == null) {
            return false;
        }
        if (AUTH_PUBLIC.contains(path) || STATIC_EXACT.contains(path)) {
            return true;
        }
        return INFRA_PREFIXES.stream().anyMatch(path::startsWith);
    }
}
