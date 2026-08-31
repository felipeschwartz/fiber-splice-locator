package github.felipeschwartz.fiber_splice_locator.model.dto;

import java.util.Set;

public record UserSummaryDTO(
        Long id,
        String name,
        String email,
        Set<String> roles
) {}
