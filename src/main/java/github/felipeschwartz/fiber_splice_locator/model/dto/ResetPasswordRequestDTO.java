package github.felipeschwartz.fiber_splice_locator.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank String email,
        @NotBlank String token,
        @NotBlank @Size(min = 6, message = "New password must be at least 6 characters long") String newPassword
) {}