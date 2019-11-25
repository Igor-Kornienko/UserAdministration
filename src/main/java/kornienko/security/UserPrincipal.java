package kornienko.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import kornienko.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@AllArgsConstructor
@Getter
public class UserPrincipal implements UserDetails {
    private String name;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String passHash;
    private boolean googleAuth;
    @JsonIgnore
    private GoogleTokenResponse googleTokenResponse;
    private Collection<? extends GrantedAuthority> roles;

    public static UserPrincipal create(User user){
        List<GrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return new UserPrincipal(
                user.getName(),
                user.getEmail(),
                user.getPassHash(),
                user.getGoogleAuth(),
                user.getGoogleTokenResponse(),
                roles
        );
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public String getPassword() {
        return passHash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
