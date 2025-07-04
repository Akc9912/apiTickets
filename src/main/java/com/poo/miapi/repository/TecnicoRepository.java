package com.poo.miapi.repository;

import com.poo.miapi.model.core.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Integer> {
    List<Tecnico> findByBloqueadoTrue();

    long countByBloqueadoTrue();
}