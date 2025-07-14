package com.poo.miapi.repository.core;

import com.poo.miapi.model.core.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByBloqueadoTrue();

    long countByActivoTrue();

    long count();

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    List<Usuario> findByRol(String rol);

    Optional<Usuario> findById(Long id);

    List<Usuario> findByTipoUsuario(String tipoUsuario);

    List<Usuario> findByApellidoContainingIgnoreCase(String apellido);
}
