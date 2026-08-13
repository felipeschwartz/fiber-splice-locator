package github.felipeschwartz.fiber_splice_locator.controller.docs;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ServiceOrderControllerDocs {

    @Operation(
            summary = "Finds all Service Orders",
            description = "Finds all service orders on database.",
            tags = {"Service Orders"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = ServiceOrderDTO.class)))}),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<List<ServiceOrderDTO>> findAll();

    @Operation(
            summary = "Finds a service order",
            description = "Finds a service order by its Id.",
            tags = {"Service Orders"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ServiceOrderDTO.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ServiceOrderDTO> findById(@PathVariable("id") Long id);

    @Operation(
            summary = "Creates a new service order",
            description = "Creates a new service order with the provided details.",
            tags = {"Service Orders"},
            requestBody = @RequestBody(
                    description = "Service order details for creation",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceOrderDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ServiceOrderDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ServiceOrderDTO> create(@org.springframework.web.bind.annotation.RequestBody @Valid ServiceOrderDTO serviceOrderDTO);

    @Operation(
            summary = "Updates an existing service order",
            description = "Updates an existing service order identified by its ID.",
            tags = {"Service Orders"},
            requestBody = @RequestBody(
                    description = "Updated service order details",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceOrderDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ServiceOrderDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ServiceOrderDTO> update(@PathVariable("id") Long id, @org.springframework.web.bind.annotation.RequestBody @Valid ServiceOrderDTO serviceOrderDTO);

    @Operation(
            summary = "Deletes a service order",
            description = "Deletes a service order identified by its ID.",
            tags = {"Service Orders"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> delete(@PathVariable("id") Long id);
}