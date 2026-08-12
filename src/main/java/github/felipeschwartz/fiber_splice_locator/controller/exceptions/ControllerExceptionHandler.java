package github.felipeschwartz.fiber_splice_locator.controller.exceptions;

import com.github.felipeschwartz.parkingsystem.service.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardError> objectNotFound(ObjectNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND; // Define o status HTTP como 404 Not Found
        StandardError err = new StandardError(
                System.currentTimeMillis(),
                status.value(),
                "Not Found", // Mensagem de erro genérica para o tipo de erro
                e.getMessage(), // Mensagem específica da exceção (e.g., "User not found: 123")
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}
