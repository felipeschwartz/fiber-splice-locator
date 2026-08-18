package github.felipeschwartz.fiber_splice_locator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@Profile("dev")
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfigDev {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfigDev(
            UserDetailsService userDetailsService,
            JwtFilter jwtFilter
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                new HttpStatusEntryPoint(
                                        HttpStatus.UNAUTHORIZED
                                )
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/v1/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error",
                                "/api/test/v1"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/user/v1/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/user/v1"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/user/v1/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/user/v1/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/ceo/v1/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "FIELD_TECHNICIAN"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/ceo/v1"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/ceo/v1/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/ceo/v1/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/service_orders/v1/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "FIELD_TECHNICIAN"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/service_orders/v1"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/service_orders/v1/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "FIELD_TECHNICIAN"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/service_orders/v1/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/service_order_photos/v1/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "FIELD_TECHNICIAN"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/service_order_photos/v1/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "FIELD_TECHNICIAN"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/service_order_photos/v1/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "FIELD_TECHNICIAN"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/service_order_photos/v1/**"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(
            DaoAuthenticationProvider authenticationProvider
    ) {
        return new ProviderManager(
                List.of(authenticationProvider)
        );
    }
}