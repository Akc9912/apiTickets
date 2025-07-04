package com.poo.miapi.repository;

import com.poo.miapi.model.core.EstadoTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoTicketRepository extends JpaRepository<EstadoTicket, String> {
}