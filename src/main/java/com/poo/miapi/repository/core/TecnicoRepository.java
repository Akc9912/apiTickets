package com.poo.miapi.repository.core;

import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Integer> {

    Optional<Tecnico> findByEmail(String email);

    List<Tecnico> findByBloqueadoTrue();

    int countByBloqueadoTrue();

    List<Tecnico> findByActivoTrue();

    List<Tecnico> findByNombreContainingIgnoreCase(String nombre);

    List<Tecnico> findByApellidoContainingIgnoreCase(String apellido);

    List<Tecnico> findByRol(Rol rol);

    Optional<Tecnico> findById(int id);
}