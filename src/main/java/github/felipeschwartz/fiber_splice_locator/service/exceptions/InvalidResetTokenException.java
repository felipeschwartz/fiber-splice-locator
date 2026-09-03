package github.felipeschwartz.fiber_splice_locator.service.exceptions;

public class InvalidResetTokenException extends RuntimeException {
    public InvalidResetTokenException() {
        super("Reset token is invalid or expired");
    }
}
