package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.model.dto.ResetPasswordRequestDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.PasswordResetToken;
import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import github.felipeschwartz.fiber_splice_locator.repository.PasswordResetTokenRepository;
import github.felipeschwartz.fiber_splice_locator.repository.UserRepository;
import github.felipeschwartz.fiber_splice_locator.service.exceptions.InvalidResetTokenException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PasswordResetService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TOKEN_LENGTH = 8;
    private static final long TOKEN_TTL_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void requestReset(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            tokenRepository.deleteByUser(user);
            String code = generateCode();
            tokenRepository.save(new PasswordResetToken(
                    code, user, Instant.now().plus(TOKEN_TTL_MINUTES, ChronoUnit.MINUTES)
            ));
            emailService.sendPasswordResetCode(user.getEmail(), code);
        });
        // E-mail não encontrado: não faz nada. O controller responde sucesso do mesmo jeito.
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {
        PasswordResetToken token = tokenRepository.findByTokenAndUsedFalse(dto.token())
                .filter(t -> t.getUser().getEmail().equalsIgnoreCase(dto.email()))
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(InvalidResetTokenException::new);

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}