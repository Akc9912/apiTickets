package com.poo.miapi.repository;

import com.poo.miapi.model.core.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrabajadorRepository extends JpaRepository<Trabajador, Integer> {

    Optional<Trabajador> findByEmail(String email);

    List<Trabajador> findByActivoTrue();

    List<Trabajador> findByNombreContainingIgnoreCase(String nombre);

    List<Trabajador> findByApellidoContainingIgnoreCase(String apellido);

    List<Trabajador> findByActivo(boolean activo);

    Optional<Trabajador> findById(Integer id);
}