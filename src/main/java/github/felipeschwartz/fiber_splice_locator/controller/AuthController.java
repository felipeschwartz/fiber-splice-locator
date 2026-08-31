package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.config.CustomUserDetails;
import github.felipeschwartz.fiber_splice_locator.config.JwtService;
import github.felipeschwartz.fiber_splice_locator.model.dto.LoginRequestDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.LoginResponseDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.UserSummaryDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/v1")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );


        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(principal.getEmail());

        UserSummaryDTO userSummary = new UserSummaryDTO(
                principal.getId(),
                principal.getName(),
                principal.getEmail(),
                principal.getRoles()
        );

        return ResponseEntity.ok(new LoginResponseDTO(token, userSummary));
    }
}
