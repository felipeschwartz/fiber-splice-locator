package github.felipeschwartz.fiber_splice_locator.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

// Em memória: suficiente para uma única instância do backend; com várias réplicas, cada uma contaria separado.
public class FixedWindowRateLimiter {

    private static final int CLEANUP_THRESHOLD = 10_000;

    private record Window(Instant start, int count) {}

    private final int maxAttempts;
    private final Duration windowLength;
    private final Clock clock;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxAttempts, Duration windowLength, Clock clock) {
        this.maxAttempts = maxAttempts;
        this.windowLength = windowLength;
        this.clock = clock;
    }

    /** Seconds until the key may try again, or 0 if it is not blocked. */
    public long secondsUntilAllowed(String key) {
        Window window = windows.get(key);
        Instant now = clock.instant();
        if (window == null || isExpired(window, now) || window.count() < maxAttempts) {
            return 0;
        }
        return Math.max(1, Duration.between(now, window.start().plus(windowLength)).toSeconds());
    }

    public void recordAttempt(String key) {
        Instant now = clock.instant();
        windows.compute(key, (k, window) ->
                window == null || isExpired(window, now) ? new Window(now, 1) : new Window(window.start(), window.count() + 1));
        if (windows.size() > CLEANUP_THRESHOLD) {
            windows.values().removeIf(window -> isExpired(window, now));
        }
    }

    public void reset(String key) {
        windows.remove(key);
    }

    private boolean isExpired(Window window, Instant now) {
        return !now.isBefore(window.start().plus(windowLength));
    }
}
