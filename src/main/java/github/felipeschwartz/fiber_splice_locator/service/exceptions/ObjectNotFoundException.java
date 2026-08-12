package github.felipeschwartz.fiber_splice_locator.service.exceptions;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String entityName, Long id) {
        super(entityName + " not found by ID: " + id);
    }

    public ObjectNotFoundException(String entityName, String licensePlate) {
        super(entityName + " not found: " + licensePlate);
    }

    public ObjectNotFoundException(String s) {

    }
}