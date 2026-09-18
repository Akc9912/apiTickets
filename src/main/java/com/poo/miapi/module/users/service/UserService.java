package com.poo.miapi.module.users.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poo.miapi.module.users.api.UserApi;
import com.poo.miapi.module.users.api.dto.request.CreateUserRequest;
import com.poo.miapi.module.users.api.dto.request.UpdateUserRequest;
import com.poo.miapi.module.users.api.dto.response.UserResponse;
import com.poo.miapi.module.users.enums.UserRole;
import com.poo.miapi.module.users.enums.UserStatus;
import com.poo.miapi.module.users.model.User;
import com.poo.miapi.module.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.*;

@Service
@AllArgsConstructor
public class UserService implements UserApi {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** CREATE */

    // No tiene endpoint: el único llamador es AuthService.register(). No hay login
    // automático (la cuenta se valida después por mail), así que no hay nada que devolver.
    //
    // La confirmación es la ausencia de excepción: si este método vuelve, el alta se hizo.
    // Cualquier falla tira IllegalArgumentException (campo vacío o email duplicado).
    //
    // OJO con el commit: si register() es @Transactional, este save() se suma a esa
    // transacción y el INSERT recién se confirma cuando register() termina. Un duplicado
    // por race condition explota ahí, después de que este método volvió bien. Si register()
    // manda el mail de verificación, mandalo después del commit, no acá adentro.
    //
    // Tampoco corre @Valid en ningún lado, así que las validaciones de acá abajo son
    // las únicas que hay: no las saques asumiendo que el controller ya validó.
    // El rol y el estado no los elige el llamador: siempre nace USER / PENDING_VERIFICATION.
    @Transactional
    @Override
    public void create(CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es obligatorio");
        }
        String firstName = requireText(request.getFirstName(), "firstName");
        String password = requireText(request.getPassword(), "password");
        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con el email " + email);
        }

        User user = User.builder()
                .firstName(firstName)
                .lastName(trimToNull(request.getLastName()))
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .globalRole(UserRole.USER)
                .status(UserStatus.PENDING_VERIFICATION)
                .build();

        userRepository.save(user);
    }

    /** READ */

    // findById/findByEmail devuelven la entidad porque los otros módulos necesitan el
    // User real para asociarlo a tickets y validar roles. getById/getByEmail son la
    // versión para exponer por HTTP.
    @Transactional(readOnly = true)
    @Override
    public User findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("El id es obligatorio");
        }
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public User findByEmail(String email) {
        String normalized = normalizeEmail(email);
        return userRepository.findByEmailAndDeletedAtIsNull(normalized)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + normalized));
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getById(UUID id) {
        return toResponse(findById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getByEmail(String email) {
        return toResponse(findByEmail(email));
    }

    // Quién puede ver los dados de baja lo decide el controller según el rol del que
    // consulta: el service expone las dos listas y no mira el SecurityContext.
    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> findAllActive() {
        return toResponseList(userRepository.findByDeletedAtIsNull());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> findAllIncludingDeleted() {
        return toResponseList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> findByRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        return toResponseList(userRepository.findByGlobalRoleAndDeletedAtIsNull(role));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> findByStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        return toResponseList(userRepository.findByStatusAndDeletedAtIsNull(status));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponse> searchByName(String term) {
        String normalized = trimToNull(term);
        // Sin término no devolvemos la tabla entera.
        if (normalized == null) {
            return List.of();
        }
        return toResponseList(userRepository.searchActiveByName(normalized));
    }

    /** UPDATE */

    // Update parcial: sólo se toca lo que viene no-null.
    @Transactional
    @Override
    public UserResponse updateUserData(UUID id, UpdateUserRequest request) {
        return applyUpdate(findById(id), request);
    }

    // Para el perfil propio: el controller sólo tiene el email del autenticado, así que
    // la resolución email -> User queda acá y la entidad no se escapa del service.
    @Transactional
    @Override
    public UserResponse updateOwnProfile(String email, UpdateUserRequest request) {
        return applyUpdate(findByEmail(email), request);
    }

    private UserResponse applyUpdate(User user, UpdateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es obligatorio");
        }
        if (request.getFirstName() != null) {
            user.setFirstName(requireText(request.getFirstName(), "firstName"));
        }
        if (request.getLastName() != null) {
            user.setLastName(trimToNull(request.getLastName()));
        }
        if (request.getPhone() != null) {
            user.setPhone(trimToNull(request.getPhone()));
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponse changeStatus(UUID id, UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        // DELETED no se setea a mano: implica deletedAt, y de eso se encarga softDelete().
        if (status == UserStatus.DELETED) {
            throw new IllegalArgumentException("Para dar de baja un usuario usá softDelete()");
        }
        User user = findById(id);
        user.setStatus(status);
        return toResponse(userRepository.save(user));
    }

    // Lo necesita AuthService para cambio y reset de password: modifica el User y lo
    // persiste. El hash lo calcula el llamador.
    @Transactional
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    /** DELETE */

    // Soft delete. deletedAt es la fuente de verdad para las consultas; status DELETED
    // se mantiene en sync para poder mostrar el estado sin mirar deletedAt.
    @Transactional
    @Override
    public void softDelete(UUID id) {
        User user = findById(id);
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    /** HELPERS */

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .globalRole(user.getGlobalRole() != null ? user.getGlobalRole().name() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .build();
    }

    private List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(this::toResponse).toList();
    }

    // El email se normaliza siempre: es la clave de unicidad y de login, así que tiene
    // que entrar igual por alta y por búsqueda.
    private String normalizeEmail(String email) {
        return requireText(email, "email").toLowerCase();
    }

    private String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo " + field + " es obligatorio");
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
