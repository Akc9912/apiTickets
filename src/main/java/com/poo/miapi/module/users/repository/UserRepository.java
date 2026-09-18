package com.poo.miapi.module.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poo.miapi.module.users.enums.UserRole;
import com.poo.miapi.module.users.enums.UserStatus;
import com.poo.miapi.module.users.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Ojo: NO filtra por deletedAt a propósito. El UNIQUE de email aplica a la tabla
    // completa, así que un usuario dado de baja sigue ocupando su email; si filtráramos
    // los borrados, el alta pasaría la validación y reventaría contra la constraint.
    boolean existsByEmail(String email);

    Optional<User> findByIdAndDeletedAtIsNull(UUID id);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    List<User> findByDeletedAtIsNull();

    List<User> findByStatusAndDeletedAtIsNull(UserStatus status);

    List<User> findByGlobalRoleAndDeletedAtIsNull(UserRole globalRole);

    @Query("""
            select u from User u
            where u.deletedAt is null
              and (lower(u.firstName) like lower(concat('%', :term, '%'))
                or lower(u.lastName) like lower(concat('%', :term, '%')))
            """)
    List<User> searchActiveByName(@Param("term") String term);
}
