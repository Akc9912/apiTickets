package com.poo.miapi.module.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poo.miapi.module.users.enums.UserRole;
import com.poo.miapi.module.users.enums.UserStatus;
import com.poo.miapi.module.users.model.User;

import java.util.List;
import java.util.UUID;
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByIdAndDeletedAtIsNull(UUID id);

    List<User> findByDeletedAtIsNull();

    List<User> findByStatus(UserStatus status);

    List<User> findByRoleAndDeletedAtIsNull(UserRole role);

    List<User> findByFirstNameContainingIgnoreCase(String firstName);

    List<User> findByLastNameContainingIgnoreCase(String lastName);

    User findByEmailAndDeletedAtIsNull(String email);
}
