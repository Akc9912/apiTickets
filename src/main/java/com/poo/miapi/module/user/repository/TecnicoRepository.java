package com.poo.miapi.module.user.repository;

import com.poo.miapi.module.user.model.Rol;
import com.poo.miapi.module.user.model.Tecnico;
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

    List<Tecnico> findByRol(Rol rol);

    Optional<Tecnico> findById(Long id);
}