package com.poo.miapi.repository;

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

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
}
