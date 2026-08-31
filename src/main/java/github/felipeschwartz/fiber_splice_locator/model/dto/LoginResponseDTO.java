package github.felipeschwartz.fiber_splice_locator.model.dto;

public record LoginResponseDTO(
        String token,
        UserSummaryDTO user
) {}
