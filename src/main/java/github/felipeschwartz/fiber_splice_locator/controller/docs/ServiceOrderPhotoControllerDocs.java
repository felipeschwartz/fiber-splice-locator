package github.felipeschwartz.fiber_splice_locator.controller.docs;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderPhotoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ServiceOrderPhotoControllerDocs {

    @Operation(
            summary = "Finds all photos of a Service Order",
            description = "Finds all photos linked to a given service order.",
            tags = {"Service Order Photos"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = ServiceOrderPhotoDTO.class)))}),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<List<ServiceOrderPhotoDTO>> findAllByServiceOrder(@PathVariable("serviceOrderId") Long serviceOrderId);

    @Operation(
            summary = "Finds a service order photo",
            description = "Finds a service order photo by its Id.",
            tags = {"Service Order Photos"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ServiceOrderPhotoDTO.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ServiceOrderPhotoDTO> findById(@PathVariable("id") Long id);

    @Operation(
            summary = "Downloads a service order photo's binary content",
            description = "Streams the raw image bytes for a given photo, with the original content type (image/jpeg, image/png or image/webp).",
            tags = {"Service Order Photos"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "image/*")
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Resource> content(@PathVariable("id") Long id);

    @Operation(
            summary = "Uploads a new photo for a service order",
            description = "Uploads a photo file and links it to the given service order.",
            tags = {"Service Order Photos"},
            requestBody = @RequestBody(
                    description = "Image file to be uploaded",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ServiceOrderPhotoDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ServiceOrderPhotoDTO> upload(
            @PathVariable("serviceOrderId") Long serviceOrderId,
            MultipartFile file
    ) throws IOException;

    @Operation(
            summary = "Updates an existing service order photo",
            description = "Updates metadata of an existing service order photo identified by its ID.",
            tags = {"Service Order Photos"},
            requestBody = @RequestBody(
                    description = "Updated service order photo details",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServiceOrderPhotoDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ServiceOrderPhotoDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<ServiceOrderPhotoDTO> update(
            @PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.RequestBody @Valid ServiceOrderPhotoDTO serviceOrderPhotoDTO
    );

    @Operation(
            summary = "Deletes a service order photo",
            description = "Deletes a service order photo identified by its ID.",
            tags = {"Service Order Photos"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> delete(@PathVariable("id") Long id);
}