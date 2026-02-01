package com.poo.miapi.module.user.repository;

import com.poo.miapi.module.user.model.Superadmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SuperadminRepository extends JpaRepository<Superadmin, Integer> {
    Optional<Superadmin> findByEmail(String email);
}
