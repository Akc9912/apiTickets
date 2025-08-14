package com.poo.miapi.security;

import com.poo.miapi.model.core.Usuario;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import com.poo.miapi.service.security.JwtService;

import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Debes tener un JwtService para validar y extraer el usuario del token
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final String[] PUBLIC_PATHS = {
        "/v3/api-docs",
        "/v3/api-docs/",
        "/v3/api-docs/*",
        "/swagger-ui.html",
        "/swagger-ui/*",
        "/swagger-ui/index.html",
        "/swagger-resources/*",
        "/webjars/*"
    };

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
                logger.info("[JWT Filter] Método: {} Endpoint: {}", request.getMethod(), request.getRequestURI());
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No se encontró el header Authorization o no es Bearer. Request: {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);
        logger.info("JWT extraído: {}", jwt);
        logger.info("Email extraído del token: {}", username);
        logger.info("Authentication actual en SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    String rol = null;
                    if (userDetails instanceof Usuario usuario) {
                        rol = usuario.getRol() != null ? usuario.getRol().name() : "NO_ROLE";
                    }
                    logger.info("[JWT Filter] Usuario autenticado: {} Rol: {}", username, rol);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                logger.info("Token válido para usuario: {}", username);
                UsernamePasswordAuthenticationToken authToken = jwtService.getAuthentication(jwt, userDetails);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                logger.warn("Token inválido para usuario: {}", username);
            }
        } else {
            logger.warn("No se pudo extraer el usuario del token o ya existe autenticación en el contexto.");
        }
        filterChain.doFilter(request, response);
    }

}