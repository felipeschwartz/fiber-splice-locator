package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.service.exceptions.TooManyAttemptsException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.util.Locale;

@Component
public class AuthRateLimiter {

    static final int MAX_LOGIN_FAILURES = 5;
    static final int MAX_RESET_REQUESTS_PER_EMAIL = 3;
    static final int MAX_RESET_REQUESTS_PER_IP = 10;
    static final Duration WINDOW = Duration.ofMinutes(15);

    private final FixedWindowRateLimiter loginFailures;
    private final FixedWindowRateLimiter resetRequestsByEmail;
    private final FixedWindowRateLimiter resetRequestsByIp;

    public AuthRateLimiter() {
        this(Clock.systemUTC());
    }

    AuthRateLimiter(Clock clock) {
        this.loginFailures = new FixedWindowRateLimiter(MAX_LOGIN_FAILURES, WINDOW, clock);
        this.resetRequestsByEmail = new FixedWindowRateLimiter(MAX_RESET_REQUESTS_PER_EMAIL, WINDOW, clock);
        this.resetRequestsByIp = new FixedWindowRateLimiter(MAX_RESET_REQUESTS_PER_IP, WINDOW, clock);
    }

    public void checkLoginAllowed(String email, String clientIp) {
        throwIfBlocked(loginFailures.secondsUntilAllowed(loginKey(email, clientIp)));
    }

    public void recordLoginFailure(String email, String clientIp) {
        loginFailures.recordAttempt(loginKey(email, clientIp));
    }

    public void recordLoginSuccess(String email, String clientIp) {
        loginFailures.reset(loginKey(email, clientIp));
    }

    // Limita antes de consultar o usuário, então a resposta não revela se o e-mail existe.
    public void checkAndRecordPasswordResetRequest(String email, String clientIp) {
        String emailKey = normalize(email);
        String ipKey = String.valueOf(clientIp);
        throwIfBlocked(Math.max(
                resetRequestsByEmail.secondsUntilAllowed(emailKey),
                resetRequestsByIp.secondsUntilAllowed(ipKey)));
        resetRequestsByEmail.recordAttempt(emailKey);
        resetRequestsByIp.recordAttempt(ipKey);
    }

    private static void throwIfBlocked(long secondsUntilAllowed) {
        if (secondsUntilAllowed > 0) {
            throw new TooManyAttemptsException(secondsUntilAllowed);
        }
    }

    private static String loginKey(String email, String clientIp) {
        return normalize(email) + "|" + clientIp;
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
