package github.felipeschwartz.fiber_splice_locator.controller.exceptions;

import java.util.Date;

public record ExceptionResponse(Date timestamp, String message, String details) {
}
