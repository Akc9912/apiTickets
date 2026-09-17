package com.poo.miapi.module.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.users.model.Superadmin;

import java.util.Optional;

@Repository
public interface SuperadminRepository extends JpaRepository<Superadmin, Integer> {
    Optional<Superadmin> findByEmail(String email);
}
