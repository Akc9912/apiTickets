package com.poo.miapi.module.auth.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.poo.miapi.module.users.enums.UserRole;
import com.poo.miapi.module.users.enums.UserStatus;
import com.poo.miapi.module.users.model.User;

/**
 * Principal de Spring Security. Envuelve un {@link User} en lugar de que la entidad
 * implemente UserDetails: así el modelo JPA no queda acoplado a Spring Security y la
 * decisión vive en module/auth, que es de quien es el problema.
 *
 * El authority se emite como ROLE_ + el nombre del rol global. El prefijo no es
 * decorativo: hasAnyRole/hasRole lo agregan por su cuenta, así que un authority sin él
 * hace que toda regla de rol devuelva 403 sin explicación visible.
 */
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final UserRole globalRole;
    private final UserStatus status;
    private final boolean deleted;

    private UserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.globalRole = user.getGlobalRole();
        this.status = user.getStatus();
        this.deleted = user.getDeletedAt() != null;
    }

    public static UserPrincipal of(User user) {
        return new UserPrincipal(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (globalRole == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + globalRole.name()));
    }

    /** El hash, no la contraseña: es lo que Spring compara vía PasswordEncoder. */
    @Override
    public String getPassword() {
        return passwordHash;
    }

    /** El username del sistema es el email; de ahí sale Authentication.getName(). */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.SUSPENDED;
    }

    /**
     * Sólo una cuenta ACTIVE está habilitada. Una PENDING_VERIFICATION todavía no validó
     * su email, y una INACTIVE o DELETED no debe operar.
     */
    @Override
    public boolean isEnabled() {
        return !deleted && status == UserStatus.ACTIVE;
    }

    public UUID getId() {
        return id;
    }

    public UserRole getGlobalRole() {
        return globalRole;
    }

    public UserStatus getStatus() {
        return status;
    }
}
