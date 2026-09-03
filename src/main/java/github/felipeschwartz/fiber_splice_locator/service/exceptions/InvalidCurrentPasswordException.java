package github.felipeschwartz.fiber_splice_locator.service.exceptions;

public class InvalidCurrentPasswordException extends RuntimeException {
    public InvalidCurrentPasswordException() {
        super("Current password is invalid");
    }
}
