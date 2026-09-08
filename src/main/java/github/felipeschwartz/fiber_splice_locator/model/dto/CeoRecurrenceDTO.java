package github.felipeschwartz.fiber_splice_locator.model.dto;

import java.time.LocalDateTime;

public record CeoRecurrenceDTO(Long ceoId, String boxNumber, Long total, LocalDateTime lastServiceOrderAt) {
}