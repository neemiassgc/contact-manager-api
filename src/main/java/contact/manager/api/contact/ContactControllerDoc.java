package contact.manager.api.contact;

import contact.manager.api.global.ViolationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

@Tag(name = "Contacts")
interface ContactControllerDoc {

    @Operation(
        description = "Get all of a user's contacts",
        responses = @ApiResponse(
            description = "A list of contacts in JSON format",
            responseCode = "200",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(minItems = 2, schema = @Schema(implementation = ContactData.class))
            )
        ),
        security = @SecurityRequirement(name = "oauth2")
    )
    List<ContactData> getAllContacts(Jwt jwt);

    @Operation(
        description = "Get a user's contact by its id",
        responses = {
            @ApiResponse(
                description = "The contact",
                responseCode = "200",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ContactData.class)
                )
            ),
            @ApiResponse(
                description = "Not found",
                responseCode = "404",
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    examples = @ExampleObject(value = "Contact not found")
                )
            ),
            @ApiResponse(
                description = "Bad request",
                responseCode = "400",
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    examples = @ExampleObject(value = "Contact belongs to another user")
                )
            ),
        },
        security = @SecurityRequirement(name = "oauth2")
    )
    ContactData getById(UUID id, Jwt jwt);

    @Operation(
        description = "Create a new contact",
        responses = {
            @ApiResponse(responseCode = "201"),
            @ApiResponse(
                description = "Bad request",
                responseCode = "400",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ViolationResponse.class)
                )
            )
        },
        security = @SecurityRequirement(name = "oauth2")
    )
    void create(ContactData contactData, Jwt jwt);

    @Operation(
        description = "Update partially a user's contact by its id",
        responses = {
            @ApiResponse(
                description = "The contact",
                responseCode = "200",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ContactData.class)
                )
            ),
            @ApiResponse(
                description = "Not found",
                responseCode = "404",
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    examples = @ExampleObject(value = "Contact not found")
                )
            ),
            @ApiResponse(
                description = "Bad request",
                responseCode = "400",
                content = {
                    @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ViolationResponse.class)
                    ),
                    @Content(
                        mediaType = MediaType.TEXT_PLAIN_VALUE,
                        examples = @ExampleObject(value = "Contact belongs to another user")
                    )
                }
            )
        },
        security = @SecurityRequirement(name = "oauth2")
    )
    ContactData update(UUID id, ConstrainedContact constrainedContact, Jwt jwt);

    @Operation(
        description = "Delete a user's contact by its id",
        responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(
                description = "Not found",
                responseCode = "404",
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    examples = @ExampleObject(value = "Contact not found")
                )
            ),
            @ApiResponse(
                description = "Bad request",
                responseCode = "400",
                content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    examples = @ExampleObject(value = "Contact belongs to another user")
                )
            )
        },
        security = @SecurityRequirement(name = "oauth2")
    )
    void delete(UUID id, Jwt jwt);
}