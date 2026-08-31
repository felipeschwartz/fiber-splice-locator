package github.felipeschwartz.fiber_splice_locator.config;


import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    @Getter
    private Long id;
    @Getter
    private String email;
    @Getter
    private String password;
    @Getter
    private String name;

    @Getter
    private Set<String> roles;
    @Getter
    private Collection<GrantedAuthority> authorities;

    @Getter
    private boolean active;


    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.active = user.getActive();
        this.roles = user.getRoles();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(
                        role.startsWith("ROLE_") ? role : "ROLE_" + role
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return active;
    }

}
