package github.felipeschwartz.fiber_splice_locator.config;

import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.model.enums.UserRole;
import github.felipeschwartz.fiber_splice_locator.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitialAdminCreatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void run_WhenUsersAlreadyExist_DoesNothing() {
        when(userRepository.count()).thenReturn(3L);

        creator("admin@example.com", "supersecret").run(null);

        verify(userRepository, never()).save(any());
    }

    @Test
    void run_WhenDatabaseIsEmptyButNotConfigured_DoesNothing() {
        when(userRepository.count()).thenReturn(0L);

        creator("", "").run(null);

        verify(userRepository, never()).save(any());
    }

    @Test
    void run_WhenDatabaseIsEmptyAndConfigured_CreatesSuperAdmin() {
        when(userRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("supersecret")).thenReturn("encoded");

        creator(" admin@example.com ", "supersecret").run(null);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User admin = captor.getValue();
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("encoded", admin.getPassword());
        assertEquals(Set.of(UserRole.SUPER_ADMIN), admin.getRoles());
        assertTrue(admin.getActive());
    }

    @Test
    void run_WhenPasswordIsTooShort_FailsStartup() {
        when(userRepository.count()).thenReturn(0L);

        InitialAdminCreator creator = creator("admin@example.com", "short");

        assertThrows(IllegalStateException.class, () -> creator.run(null));
        verify(userRepository, never()).save(any());
    }

    private InitialAdminCreator creator(String email, String password) {
        return new InitialAdminCreator(userRepository, passwordEncoder, email, password, "Administrador");
    }
}
