package github.felipeschwartz.fiber_splice_locator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@Profile("dev")
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfigDev {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfigDev(UserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/auth/**", "/error", "/api/test/v1").permitAll() // TestLogController pode ser acessado por todos
                        // Proteção de endpoints por URL
                        .requestMatchers(HttpMethod.POST, "/user/individual", "/user/entity").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/user/individuals/{id}", "/user/entities/{id}").hasAnyRole("ADMIN", "USER", "PARKING", "PARKING_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/user/id/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/user/**").hasAnyRole("ADMIN", "PARKING_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/vehicle/**").hasAnyRole("ADMIN", "PARKING_MANAGER", "PARKING", "USER")
                        .requestMatchers(HttpMethod.POST, "/vehicle").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/vehicle/{id}").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/vehicle/{id}").hasRole("ADMIN")
                        .requestMatchers("/parking_lot/**", "/parking_space/**", "/hourly_rate/**", "/plan/**", "/plan_rate/**").hasAnyRole("ADMIN", "PARKING_MANAGER")
                        .requestMatchers("/contracts/**").hasAnyRole("ADMIN", "PARKING_MANAGER", "USER")
                        .requestMatchers("/reservation/**").hasAnyRole("ADMIN", "PARKING_MANAGER", "USER")
                        .requestMatchers("/payment/**").hasAnyRole("ADMIN", "PARKING_MANAGER")
                        .requestMatchers("/parking_sessions/open/**").hasAnyRole("ADMIN", "PARKING", "PARKING_MANAGER")
                        .requestMatchers("/parking_sessions/close/**").hasAnyRole("ADMIN", "PARKING", "PARKING_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/parking_sessions/**").hasAnyRole("ADMIN", "PARKING", "PARKING_MANAGER", "USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider()));
    }
}