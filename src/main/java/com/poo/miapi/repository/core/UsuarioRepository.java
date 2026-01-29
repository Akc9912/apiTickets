package com.poo.miapi.repository.core;

import com.poo.miapi.model.core.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import com.poo.miapi.model.enums.Rol;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM usuario WHERE email = :email", nativeQuery = true)
    long countByEmail(@Param("email") String email);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM usuario WHERE email = :email", nativeQuery = true)
    void deleteByEmail(@Param("email") String email);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByBloqueadoTrue();

    int countByActivoTrue();

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    List<Usuario> findByRol(Rol rol);

    List<Usuario> findByApellidoContainingIgnoreCase(String apellido);

    // Métodos para SuperAdminService
    int countByRol(Rol rol);

    int countByRolAndActivoTrue(Rol rol);

    int countByBloqueadoTrue();

    List<Usuario> findByRolIn(List<Rol> roles);
}
