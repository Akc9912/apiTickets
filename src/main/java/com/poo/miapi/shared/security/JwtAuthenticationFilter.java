package com.poo.miapi.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.poo.miapi.module.auth.service.JwtService;

import java.io.IOException;

/**
 * Autentica el request a partir del access token del header Authorization.
 *
 * No escribe el token en el log. La versión anterior lo hacía en INFO, así que cualquiera con
 * acceso a los logs podía tomar una sesión ajena; además el resto del log de este filtro era
 * INFO por request, lo que lo volvía inútilmente ruidoso. Ahora todo va en DEBUG y nunca
 * incluye el token.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Las rutas públicas no llevan token: la lista es la misma que usa SecurityConfig.
        if (PublicEndpoints.isPublic(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Si ya hay autenticación en el contexto, no se vuelve a resolver.
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length());
        String email = jwtService.extractEmail(token);
        if (email == null) {
            // Firma inválida o token vencido: se sigue sin autenticar y decide SecurityConfig.
            logger.debug("Token inválido o vencido en {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Una cuenta suspendida, inactiva o sin verificar no se autentica aunque su token
            // siga vigente: el access token no es revocable, así que el estado se chequea acá.
            if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
                logger.debug("Cuenta no habilitada para {}", email);
                filterChain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Autenticado {} con authorities {}", email, userDetails.getAuthorities());
        } catch (UsernameNotFoundException e) {
            logger.debug("El usuario del token ya no existe: {}", email);
        }

        filterChain.doFilter(request, response);
    }
}
