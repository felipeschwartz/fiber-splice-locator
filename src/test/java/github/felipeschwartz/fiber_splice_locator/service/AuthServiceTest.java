package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Felipe Schwartz", "felipe@example.com", "encodedPassword", true);
    }

    @Test
    void authenticate_WhenCredentialsAreValid_ReturnsTrue() {
        when(userRepository.findByEmail("felipe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "encodedPassword")).thenReturn(true);

        boolean result = authService.authenticate("felipe@example.com", "123456");

        assertTrue(result);
    }

    @Test
    void authenticate_WhenPasswordIsInvalid_ReturnsFalse() {
        when(userRepository.findByEmail("felipe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senhaErrada", "encodedPassword")).thenReturn(false);

        boolean result = authService.authenticate("felipe@example.com", "senhaErrada");

        assertFalse(result);
    }

    @Test
    void authenticate_WhenUserDoesNotExist_ThrowsException() {
        when(userRepository.findByEmail("naoexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.authenticate("naoexiste@example.com", "123456"));
    }
}