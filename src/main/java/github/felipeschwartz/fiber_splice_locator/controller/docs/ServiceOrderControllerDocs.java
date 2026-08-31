package github.felipeschwartz.fiber_splice_locator.controller.docs;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderAttendanceDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ServiceOrderControllerDocs {

    @Operation(summary = "Finds all Service Orders", description = "Finds all service orders on database.", tags = {"Service Orders"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ServiceOrderDTO.class)))),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    ResponseEntity<List<ServiceOrderDTO>> findAll();

    @Operation(summary = "Finds a service order", description = "Finds a service order by its Id.", tags = {"Service Orders"}, responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ServiceOrderDTO.class))),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not found", responseCode = "404", content = @Content)
    })
    ResponseEntity<ServiceOrderDTO> findById(@PathVariable("id") Long id);

    @Operation(summary = "Creates a service order", description = "Creates a service order without changing the CEO status.", tags = {"Service Orders"}, requestBody = @RequestBody(description = "Service order details for creation", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServiceOrderDTO.class))), responses = {
            @ApiResponse(description = "Created", responseCode = "201", content = @Content(schema = @Schema(implementation = ServiceOrderDTO.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
    })
    ResponseEntity<EntityModel<ServiceOrderDTO>> create(@org.springframework.web.bind.annotation.RequestBody @Valid ServiceOrderDTO request);

    @Operation(summary = "Opens a service order and changes CEO status", description = "Atomically changes the CEO status, creates a service order with status OPEN and stores the initial description.", tags = {"Service Orders"}, requestBody = @RequestBody(description = "CEO ID, CEO status, user ID and initial problem description", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServiceOrderDTO.class))), responses = {
            @ApiResponse(description = "Created", responseCode = "201", content = @Content(schema = @Schema(implementation = ServiceOrderDTO.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "CEO or user not found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content)
    })
    ResponseEntity<EntityModel<ServiceOrderDTO>> open(@org.springframework.web.bind.annotation.RequestBody @Valid ServiceOrderDTO request);

    @Operation(summary = "Updates an existing service order", description = "Updates an existing service order identified by its ID.", tags = {"Service Orders"}, requestBody = @RequestBody(description = "Updated service order details", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServiceOrderDTO.class))), responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ServiceOrderDTO.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Not found", responseCode = "404", content = @Content)
    })
    ResponseEntity<ServiceOrderDTO> update(@PathVariable("id") Long id, @org.springframework.web.bind.annotation.RequestBody @Valid ServiceOrderDTO request);

    @Operation(summary = "Updates attendance information", description = "Updates the attendance information for an existing service order.", tags = {"Service Orders"}, requestBody = @RequestBody(description = "Attendance details", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ServiceOrderAttendanceDTO.class))), responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = ServiceOrderDTO.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Not found", responseCode = "404", content = @Content)
    })
    ResponseEntity<ServiceOrderDTO> attend(@PathVariable("id") Long id, @org.springframework.web.bind.annotation.RequestBody @Valid ServiceOrderAttendanceDTO request);

    @Operation(summary = "Deletes a service order", description = "Deletes a service order identified by its ID.", tags = {"Service Orders"}, responses = {
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not found", responseCode = "404", content = @Content)
    })
    ResponseEntity<Void> delete(@PathVariable("id") Long id);
}
