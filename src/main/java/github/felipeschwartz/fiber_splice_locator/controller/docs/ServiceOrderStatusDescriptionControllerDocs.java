package github.felipeschwartz.fiber_splice_locator.controller.docs;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderStatusDescriptionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ServiceOrderStatusDescriptionControllerDocs {

    @Operation(
            summary = "Finds all Service Order Status Descriptions",
            description = "Finds all service order status descriptions on database. The status history is append-only: entries are created by opening or attending a service order, never through this endpoint.",
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
            summary = "Finds the status history of a service order",
            description = "Finds the status descriptions of a service order, oldest first.",
            tags = {"Service Orders Status Descriptions"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = ServiceOrderStatusDescriptionDTO.class)))}),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<List<ServiceOrderStatusDescriptionDTO>> findByServiceOrder(@PathVariable Long serviceOrderId);

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
            summary = "Deletes a service order status description",
            description = "Deletes a service order status description identified by its ID. Only a SUPER_ADMIN can delete entries, for moderation.",
            tags = {"Service Orders Status Descriptions"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> delete(@PathVariable("id") Long id);
}
