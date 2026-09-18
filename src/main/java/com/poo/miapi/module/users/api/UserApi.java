package com.poo.miapi.module.users.api;

import java.util.List;
import java.util.UUID;

import com.poo.miapi.module.users.api.dto.request.CreateUserRequest;
import com.poo.miapi.module.users.api.dto.request.UpdateUserRequest;
import com.poo.miapi.module.users.api.dto.response.UserResponse;
import com.poo.miapi.module.users.enums.UserRole;
import com.poo.miapi.module.users.enums.UserStatus;
import com.poo.miapi.module.users.model.User;

/**
 * Contrato de entrada al módulo users. Todo consumidor externo (module/auth,
 * module/ticket, shared/config) depende de esta interfaz, nunca de UserService.
 *
 * <h2>Excepciones documentadas a la regla de DTOs</h2>
 *
 * architecture.md pide que los contratos entre módulos hablen sólo en DTOs. Tres métodos
 * la rompen a propósito porque hoy no hay forma de evitarlo:
 *
 * <ul>
 * <li>{@link #findById(UUID)} y {@link #findByEmail(String)} devuelven la entidad porque
 * module/ticket la necesita como entidad JPA real: Ticket la referencia con @ManyToOne y
 * un DTO no sirve para armar la relación. auth también la necesita para firmar el JWT.</li>
 * <li>{@link #save(User)} recibe la entidad porque AuthService modifica el hash de
 * password sobre el User y lo persiste.</li>
 * </ul>
 *
 * Son el punto de acoplamiento a cerrar: cuando ticket referencie al usuario por UUID en
 * vez de por relación JPA, y auth tenga sus propias operaciones de password en este
 * contrato, estos tres métodos se pueden sacar y la interfaz queda 100% DTO.
 *
 * <h2>Errores</h2>
 *
 * Los métodos que resuelven un usuario tiran {@link jakarta.persistence.EntityNotFoundException}
 * si no existe o está dado de baja, y {@link IllegalArgumentException} si los datos de
 * entrada son inválidos. GlobalExceptionHandler los mapea a 404 y 400.
 */
public interface UserApi {

    /* ---------- CREATE ---------- */

    /**
     * Da de alta un usuario. Sin endpoint: lo llama únicamente AuthService.register().
     * Nace siempre con rol USER y estado PENDING_VERIFICATION; el llamador no los elige.
     * La confirmación es la ausencia de excepción.
     *
     * @throws IllegalArgumentException si falta un campo obligatorio o el email ya existe
     */
    void create(CreateUserRequest request);

    /* ---------- READ (entidad — ver excepciones arriba) ---------- */

    /** Usuario activo por id, como entidad. Para uso entre módulos. */
    User findById(UUID id);

    /** Usuario activo por email, como entidad. Para uso entre módulos. */
    User findByEmail(String email);

    /* ---------- READ (DTO) ---------- */

    /** Usuario activo por id. */
    UserResponse getById(UUID id);

    /** Usuario activo por email. */
    UserResponse getByEmail(String email);

    /** Usuarios no dados de baja. */
    List<UserResponse> findAllActive();

    /** Todos los usuarios, incluidos los dados de baja. */
    List<UserResponse> findAllIncludingDeleted();

    /** Usuarios activos con el rol global indicado. */
    List<UserResponse> findByRole(UserRole role);

    /** Usuarios activos en el estado indicado. */
    List<UserResponse> findByStatus(UserStatus status);

    /**
     * Búsqueda parcial sobre nombre y apellido, sin distinguir mayúsculas.
     * Un término vacío devuelve lista vacía, no la tabla entera.
     */
    List<UserResponse> searchByName(String term);

    /* ---------- UPDATE ---------- */

    /** Update parcial por id (uso administrativo). Los campos null no se tocan. */
    UserResponse updateUserData(UUID id, UpdateUserRequest request);

    /** Update parcial del perfil propio, resuelto por el email del autenticado. */
    UserResponse updateOwnProfile(String email, UpdateUserRequest request);

    /**
     * Cambia el estado del usuario.
     *
     * @throws IllegalArgumentException si se pide DELETED: eso implica baja, va por
     *                                  {@link #softDelete(UUID)}
     */
    UserResponse changeStatus(UUID id, UserStatus status);

    /** Persiste cambios hechos sobre la entidad. Ver excepciones arriba. */
    User save(User user);

    /* ---------- DELETE ---------- */

    /**
     * Baja lógica: marca deletedAt y deja el estado en DELETED. La fila queda, así que el
     * email sigue ocupado.
     */
    void softDelete(UUID id);
}
