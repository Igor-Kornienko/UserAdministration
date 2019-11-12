package kornienko.model;

import org.springframework.security.core.GrantedAuthority;


public enum UserRole implements GrantedAuthority {
    GUEST,
    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
