package com.poo.miapi.module.user.repository;

import com.poo.miapi.module.user.model.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportRepository extends JpaRepository<Support, Integer> {

    Optional<Support> findByEmail(String email);

    List<Support> findByActiveTrue();

    List<Support> findByNameContainingIgnoreCase(String name);

    List<Support> findByLastNameContainingIgnoreCase(String lastName);

    List<Support> findByActive(boolean active);
}