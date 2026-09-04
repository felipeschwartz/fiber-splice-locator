package github.felipeschwartz.fiber_splice_locator.controller.docs;

import github.felipeschwartz.fiber_splice_locator.model.dto.ForgotPasswordRequestDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.LoginRequestDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.LoginResponseDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.ResetPasswordRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface AuthControllerDocs {

    @Operation(
            summary = "Logs in",
            description = "Authenticates with email and password, returning a JWT bearer token and a summary of the authenticated user.",
            tags = {"Auth"},
            requestBody = @RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = LoginRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))
                    ),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request);

    @Operation(
            summary = "Requests a password reset code",
            description = "If the given email belongs to a registered user, an 8-character reset code is generated, valid for 15 minutes, and sent by email. Always responds successfully regardless of whether the email exists, to avoid revealing registered accounts.",
            tags = {"Auth"},
            requestBody = @RequestBody(
                    description = "Account email",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ForgotPasswordRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDTO request);

    @Operation(
            summary = "Resets the password using a reset code",
            description = "Validates the reset code sent by email (must match the given email, not be expired and not have been used yet) and updates the account password.",
            tags = {"Auth"},
            requestBody = @RequestBody(
                    description = "Email, reset code and new password",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResetPasswordRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            }
    )
    ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request);
}
