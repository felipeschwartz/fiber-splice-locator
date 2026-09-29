package github.felipeschwartz.fiber_splice_locator.config;

import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.model.enums.UserRole;
import github.felipeschwartz.fiber_splice_locator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

// Substitui o DevDatabaseSeeder fora do perfil dev: cria só o primeiro SUPER_ADMIN, e só com o banco vazio.
@Component
@Profile("!dev")
public class InitialAdminCreator implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitialAdminCreator.class);
    static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private final String name;

    public InitialAdminCreator(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.initial-admin.email:}") String email,
            @Value("${app.initial-admin.password:}") String password,
            @Value("${app.initial-admin.name:Administrador}") String name
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            return;
        }

        if (email.isBlank() || password.isBlank()) {
            logger.warn("No users in the database and INITIAL_ADMIN_EMAIL/INITIAL_ADMIN_PASSWORD are not set: nobody can log in.");
            return;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalStateException("INITIAL_ADMIN_PASSWORD must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }

        User admin = new User();
        admin.setName(name);
        admin.setEmail(email.trim());
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRoles(Set.of(UserRole.SUPER_ADMIN));
        admin.setActive(true);
        userRepository.save(admin);

        logger.info("Created initial SUPER_ADMIN {}. Remove INITIAL_ADMIN_PASSWORD from the environment now.", admin.getEmail());
    }
}
