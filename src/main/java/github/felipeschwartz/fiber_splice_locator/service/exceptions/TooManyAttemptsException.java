package github.felipeschwartz.fiber_splice_locator.service.exceptions;

public class TooManyAttemptsException extends RuntimeException {

    private final long retryAfterSeconds;

    public TooManyAttemptsException(long retryAfterSeconds) {
        super("Muitas tentativas. Tente novamente em " + Math.max(1, (retryAfterSeconds + 59) / 60) + " minuto(s).");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
