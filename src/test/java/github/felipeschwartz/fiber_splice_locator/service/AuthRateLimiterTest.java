package github.felipeschwartz.fiber_splice_locator.service;

import github.felipeschwartz.fiber_splice_locator.service.exceptions.TooManyAttemptsException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class AuthRateLimiterTest {

    private static final String EMAIL = "felipe@example.com";
    private static final String IP = "203.0.113.10";

    private final MutableClock clock = new MutableClock(Instant.parse("2026-09-29T12:00:00Z"));
    private final AuthRateLimiter limiter = new AuthRateLimiter(clock);

    @Test
    void login_IsBlockedAfterMaxFailures() {
        failLogins(AuthRateLimiter.MAX_LOGIN_FAILURES - 1);
        assertDoesNotThrow(() -> limiter.checkLoginAllowed(EMAIL, IP));

        failLogins(1);
        TooManyAttemptsException exception =
                assertThrows(TooManyAttemptsException.class, () -> limiter.checkLoginAllowed(EMAIL, IP));
        assertEquals(AuthRateLimiter.WINDOW.toSeconds(), exception.getRetryAfterSeconds());
    }

    @Test
    void login_IsAllowedAgainAfterTheWindowEnds() {
        failLogins(AuthRateLimiter.MAX_LOGIN_FAILURES);

        clock.advance(AuthRateLimiter.WINDOW);

        assertDoesNotThrow(() -> limiter.checkLoginAllowed(EMAIL, IP));
    }

    @Test
    void login_SuccessResetsTheFailureCounter() {
        failLogins(AuthRateLimiter.MAX_LOGIN_FAILURES - 1);
        limiter.recordLoginSuccess(EMAIL, IP);
        failLogins(AuthRateLimiter.MAX_LOGIN_FAILURES - 1);

        assertDoesNotThrow(() -> limiter.checkLoginAllowed(EMAIL, IP));
    }

    @Test
    void login_CounterIsPerEmailAndIpAndIgnoresEmailCase() {
        failLogins(AuthRateLimiter.MAX_LOGIN_FAILURES);

        assertThrows(TooManyAttemptsException.class, () -> limiter.checkLoginAllowed("  FELIPE@example.com ", IP));
        assertDoesNotThrow(() -> limiter.checkLoginAllowed(EMAIL, "198.51.100.7"));
        assertDoesNotThrow(() -> limiter.checkLoginAllowed("other@example.com", IP));
    }

    @Test
    void passwordReset_IsLimitedPerEmail() {
        for (int i = 0; i < AuthRateLimiter.MAX_RESET_REQUESTS_PER_EMAIL; i++) {
            limiter.checkAndRecordPasswordResetRequest(EMAIL, "198.51.100." + i);
        }

        assertThrows(TooManyAttemptsException.class,
                () -> limiter.checkAndRecordPasswordResetRequest(EMAIL, "198.51.100.99"));
    }

    @Test
    void passwordReset_IsLimitedPerIp() {
        for (int i = 0; i < AuthRateLimiter.MAX_RESET_REQUESTS_PER_IP; i++) {
            limiter.checkAndRecordPasswordResetRequest("user" + i + "@example.com", IP);
        }

        assertThrows(TooManyAttemptsException.class,
                () -> limiter.checkAndRecordPasswordResetRequest("another@example.com", IP));
    }

    private void failLogins(int times) {
        for (int i = 0; i < times; i++) {
            limiter.recordLoginFailure(EMAIL, IP);
        }
    }

    private static final class MutableClock extends Clock {
        private Instant now;

        private MutableClock(Instant start) {
            this.now = start;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }
    }
}
