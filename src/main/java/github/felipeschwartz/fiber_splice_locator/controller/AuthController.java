package github.felipeschwartz.fiber_splice_locator.controller;

import github.felipeschwartz.fiber_splice_locator.config.CustomUserDetails;
import github.felipeschwartz.fiber_splice_locator.config.JwtService;
import github.felipeschwartz.fiber_splice_locator.controller.docs.AuthControllerDocs;
import github.felipeschwartz.fiber_splice_locator.model.dto.*;
import github.felipeschwartz.fiber_splice_locator.service.PasswordResetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/v1")
@Tag(name = "Auth", description = "Endpoints for user authentication and password recovery")
public class AuthController implements AuthControllerDocs {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder, PasswordResetService passwordResetService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    @Override
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

    @PostMapping("/forgot-password")
    @Override
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO request) {
        passwordResetService.requestReset(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Override
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request){
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
