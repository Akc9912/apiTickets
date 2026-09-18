package com.poo.miapi.module.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.poo.miapi.module.auth.security.UserPrincipal;
import com.poo.miapi.module.users.api.UserApi;
import com.poo.miapi.module.users.model.User;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Se depende del contrato del módulo users, no de UserService.
    private final UserApi userApi;

    public CustomUserDetailsService(UserApi userApi) {
        this.userApi = userApi;
    }

    /**
     * El username es el email. Los usuarios dados de baja ya quedan afuera: UserApi sólo
     * resuelve usuarios con deletedAt nulo y tira EntityNotFoundException si no hay.
     *
     * Acá NO se filtra por estado de la cuenta. Una PENDING_VERIFICATION o SUSPENDED se
     * carga igual, y de eso se encargan isEnabled()/isAccountNonLocked() del principal:
     * así el login puede distinguir "no existe" de "existe pero no está habilitada" en
     * lugar de responder lo mismo a los dos casos.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userApi.findByEmail(username);
            return UserPrincipal.of(user);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
    }
}
