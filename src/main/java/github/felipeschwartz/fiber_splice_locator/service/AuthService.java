package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.config.CustomUserDetails;
import github.felipeschwartz.fiber_splice_locator.config.JwtService;
import github.felipeschwartz.fiber_splice_locator.model.dto.LoginRequestDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.LoginResponseDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.UserSummaryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthRateLimiter rateLimiter;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, AuthRateLimiter rateLimiter) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.rateLimiter = rateLimiter;
    }

    public LoginResponseDTO login(LoginRequestDTO request, String clientIp) {
        logger.info("Authenticating user: {}", request.email());
        rateLimiter.checkLoginAllowed(request.email(), clientIp);

        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            rateLimiter.recordLoginFailure(request.email(), clientIp);
            throw e;
        }
        rateLimiter.recordLoginSuccess(request.email(), clientIp);

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(principal.getEmail());

        UserSummaryDTO userSummary = new UserSummaryDTO(
                principal.getId(),
                principal.getName(),
                principal.getEmail(),
                principal.getRoles()
        );

        return new LoginResponseDTO(token, userSummary);
    }
}
