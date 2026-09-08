package github.felipeschwartz.fiber_splice_locator.controller.docs;

import github.felipeschwartz.fiber_splice_locator.model.dto.CeoRecurrenceDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.TechnicianServiceOrderCountDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface ReportControllerDocs {

    @Operation(
            summary = "Counts service orders per technician in a period",
            description = "Groups service orders created between the given dates by the technician (user) responsible, ordered by count descending. Useful to track productivity.",
            tags = {"Reports"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = TechnicianServiceOrderCountDTO.class)))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<List<TechnicianServiceOrderCountDTO>> serviceOrdersByTechnician(
            @Parameter(description = "Start date (inclusive), format yyyy-MM-dd") LocalDate from,
            @Parameter(description = "End date (inclusive), format yyyy-MM-dd") LocalDate to
    );

    @Operation(
            summary = "Counts service orders per CEO in a period",
            description = "Groups service orders created between the given dates by CEO, ordered by count descending — a high count for the same CEO in a short period may indicate a recurring/unresolved issue.",
            tags = {"Reports"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CeoRecurrenceDTO.class)))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<List<CeoRecurrenceDTO>> ceoRecurrence(
            @Parameter(description = "Start date (inclusive), format yyyy-MM-dd") LocalDate from,
            @Parameter(description = "End date (inclusive), format yyyy-MM-dd") LocalDate to
    );
}