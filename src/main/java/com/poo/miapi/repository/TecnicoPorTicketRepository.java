package com.poo.miapi.repository;

import com.poo.miapi.model.historial.TecnicoPorTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TecnicoPorTicketRepository extends JpaRepository<TecnicoPorTicket, Integer> {
}