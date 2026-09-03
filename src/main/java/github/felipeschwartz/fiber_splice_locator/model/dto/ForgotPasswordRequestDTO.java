package github.felipeschwartz.fiber_splice_locator.model.dto;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO(@NotBlank String email) {}
