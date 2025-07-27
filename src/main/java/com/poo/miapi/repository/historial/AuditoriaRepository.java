package com.poo.miapi.repository.historial;

import com.poo.miapi.model.historial.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {

    List<Auditoria> findByUsuario(String usuario);

    List<Auditoria> findByEntidad(String entidad);

    List<Auditoria> findByAccion(String accion);

    List<Auditoria> findByFechaBetween(LocalDateTime startDate, LocalDateTime endDate);
}
