package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.config.CustomUserDetails;
import github.felipeschwartz.fiber_splice_locator.config.JwtService;
import github.felipeschwartz.fiber_splice_locator.model.dto.LoginRequestDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.LoginResponseDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.model.enums.UserRole;
import github.felipeschwartz.fiber_splice_locator.service.exceptions.TooManyAttemptsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    private static final String IP = "203.0.113.10";

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthRateLimiter rateLimiter;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_WhenRateLimited_ThrowsWithoutAuthenticating() {
        doThrow(new TooManyAttemptsException(600)).when(rateLimiter).checkLoginAllowed("felipe@example.com", IP);

        assertThrows(TooManyAttemptsException.class,
                () -> authService.login(new LoginRequestDTO("felipe@example.com", "12345678"), IP));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_WithInvalidCredentials_RecordsFailure() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequestDTO("felipe@example.com", "senhaErrada"), IP));
        verify(rateLimiter).recordLoginFailure("felipe@example.com", IP);
        verify(rateLimiter, never()).recordLoginSuccess(anyString(), anyString());
    }

    @Test
    void login_WithValidCredentials_ResetsFailureCounter() {
        CustomUserDetails principal = new CustomUserDetails(new User(1L, "Felipe", "felipe@example.com", "encodedPassword", true));
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        authService.login(new LoginRequestDTO("felipe@example.com", "12345678"), IP);

        verify(rateLimiter).recordLoginSuccess("felipe@example.com", IP);
        verify(rateLimiter, never()).recordLoginFailure(anyString(), anyString());
    }

    @Test
    void login_WithValidCredentials_ReturnsTokenAndUserSummary() {
        User user = new User(1L, "Felipe Schwartz", "felipe@example.com", "encodedPassword", true);
        user.setRoles(Set.of(UserRole.ADMIN));
        CustomUserDetails principal = new CustomUserDetails(user);

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        when(jwtService.generateToken("felipe@example.com")).thenReturn("jwt-token");

        LoginResponseDTO result = authService.login(new LoginRequestDTO("felipe@example.com", "123456"), IP);

        assertEquals("jwt-token", result.token());
        assertEquals(1L, result.user().id());
        assertEquals("Felipe Schwartz", result.user().name());
        assertEquals("felipe@example.com", result.user().email());
        assertEquals(Set.of(UserRole.ADMIN), result.user().roles());
    }

    @Test
    void login_PassesSubmittedCredentialsToAuthenticationManager() {
        User user = new User(1L, "Felipe Schwartz", "felipe@example.com", "encodedPassword", true);
        CustomUserDetails principal = new CustomUserDetails(user);
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        authService.login(new LoginRequestDTO("felipe@example.com", "123456"), IP);

        ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("felipe@example.com", captor.getValue().getPrincipal());
        assertEquals("123456", captor.getValue().getCredentials());
    }

    @Test
    void login_WithInvalidCredentials_ThrowsAndDoesNotIssueToken() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequestDTO("felipe@example.com", "senhaErrada"), IP));
        verify(jwtService, never()).generateToken(anyString());
    }
}
