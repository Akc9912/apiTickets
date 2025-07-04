package com.poo.miapi.repository;

import com.poo.miapi.model.historial.HistorialValidacionTrabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialValidacionRepository extends JpaRepository<HistorialValidacionTrabajador, Long> {
}