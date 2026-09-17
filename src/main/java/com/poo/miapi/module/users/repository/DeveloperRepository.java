package com.poo.miapi.module.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.users.enums.UserRole;
import com.poo.miapi.module.users.model.Developer;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Integer> {

    Optional<Developer> findByEmail(String email);

    List<Developer> findByBlockedTrue();

    long countByBlockedTrue();

    List<Developer> findByActiveTrue();

    List<Developer> findByNameContainingIgnoreCase(String name);

    List<Developer> findByLastNameContainingIgnoreCase(String lastName);

    List<Developer> findByRole(UserRole role);

    Developer findById(int id);
}