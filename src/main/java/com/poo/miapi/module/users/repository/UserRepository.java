package com.poo.miapi.module.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.users.enums.UserStatus;
import com.poo.miapi.module.users.model.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByIdAndDeletedAtIsNull(UUID id);

    List<User> findByDeletedAtIsNull();

    List<User> findByStatus(UserStatus status);
}
