package github.felipeschwartz.fiber_splice_locator.model.dto;

import github.felipeschwartz.fiber_splice_locator.model.enums.UserRole;

import java.util.Set;

public record UserSummaryDTO(
        Long id,
        String name,
        String email,
        Set<UserRole> roles
) {}
