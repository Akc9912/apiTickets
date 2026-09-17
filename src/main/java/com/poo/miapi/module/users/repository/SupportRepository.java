package com.poo.miapi.module.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.users.model.Support;

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