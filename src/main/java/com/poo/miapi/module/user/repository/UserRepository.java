package com.poo.miapi.module.user.repository;

import com.poo.miapi.module.user.enums.UserRole;
import com.poo.miapi.module.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT COUNT(*) FROM user WHERE email = :email", nativeQuery = true)
    long countByEmail(@Param("email") String email);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user WHERE email = :email", nativeQuery = true)
    void deleteByEmail(@Param("email") String email);

    List<User> findByActiveTrue();

    List<User> findByBlockedTrue();

    int countByActiveTrue();

    List<User> findByNameContainingIgnoreCase(String name);

    List<User> findByRole(UserRole role);

    List<User> findByLastNameContainingIgnoreCase(String lastName);

    // Métodos para SuperAdminService
    int countByRole(UserRole role);

    int countByRoleAndActiveTrue(UserRole role);

    int countByBlockedTrue();

    List<User> findByRoleIn(List<UserRole> roles);
}
