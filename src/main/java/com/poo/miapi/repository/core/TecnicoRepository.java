package com.poo.miapi.repository.core;

import com.poo.miapi.model.core.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Integer> {

    Optional<Tecnico> findByEmail(String email);

    List<Tecnico> findByBloqueadoTrue();

    long countByBloqueadoTrue();

    List<Tecnico> findByActivoTrue();

    List<Tecnico> findByNombreContainingIgnoreCase(String nombre);

    List<Tecnico> findByApellidoContainingIgnoreCase(String apellido);

    List<Tecnico> findByRol(String rol);

    List<Tecnico> findByTipoUsuario(String tipoUsuario);

    Optional<Tecnico> findById(Long id);
}