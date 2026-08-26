package github.felipeschwartz.fiber_splice_locator.controller.docs;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderStatusDescriptionDTO;
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

public interface ServiceOrderStatusDescriptionControllerDocs {

    @Operation(
            summary = "Finds all Service Order Status Descriptions",
            description = "Finds all service order status descriptions on database.",
            tags = {"Service Orders Status Descriptions"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = ServiceOrderStatusDescriptionDTO.class)))}),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<List<ServiceOrderStatusDescriptionDTO>> findAll();

    @Operation(
            summary = "Finds a service order status description",
            description = "Finds a service order status description by its Id.",
            tags = {"Service Orders Status Descriptions"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ServiceOrderStatusDescriptionDTO.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ServiceOrderStatusDescriptionDTO> findById(@PathVariable("id") Long id);

    @Operation(
            summary = "Creates a new service order status description",
            description = "Creates a new service order status description with the provided details.",
            tags = {"Service Orders Status Descriptions"},
            requestBody = @RequestBody(
                    description = "Service order status description details for creation",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceOrderStatusDescriptionDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ServiceOrderStatusDescriptionDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ServiceOrderStatusDescriptionDTO> create(@org.springframework.web.bind.annotation.RequestBody @Valid ServiceOrderStatusDescriptionDTO serviceOrderStatusDescriptionDTO);

    @Operation(
            summary = "Updates an existing service order status description",
            description = "Updates an existing service order status description identified by its ID.",
            tags = {"Service Orders Status Descriptions"},
            requestBody = @RequestBody(
                    description = "Updated service order status description details",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceOrderStatusDescriptionDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ServiceOrderStatusDescriptionDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ServiceOrderStatusDescriptionDTO> update(@PathVariable("id") Long id, @org.springframework.web.bind.annotation.RequestBody @Valid ServiceOrderStatusDescriptionDTO serviceOrderStatusDescriptionDTO);

    @Operation(
            summary = "Deletes a service order status description",
            description = "Deletes a service order status description identified by its ID.",
            tags = {"Service Orders Status Descriptions"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> delete(@PathVariable("id") Long id);
}