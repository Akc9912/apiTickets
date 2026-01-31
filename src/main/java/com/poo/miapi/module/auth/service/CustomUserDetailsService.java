package com.poo.miapi.service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.poo.miapi.repository.core.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario por email
        var usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        // Verificación de seguridad: solo los usuarios INACTIVOS se tratan como si no existieran
        // Los usuarios BLOQUEADOS pueden autenticarse pero no realizar acciones
        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        
        return (UserDetails) usuario;
    }
}