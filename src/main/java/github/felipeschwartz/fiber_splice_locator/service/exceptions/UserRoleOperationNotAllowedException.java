package github.felipeschwartz.fiber_splice_locator.service.exceptions;

public class UserRoleOperationNotAllowedException extends RuntimeException {
    public UserRoleOperationNotAllowedException(String message) {
        super(message);
    }
}