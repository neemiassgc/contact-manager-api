package contact.manager.api.contact;

import contact.manager.api.global.GlobalErrorController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static contact.manager.api.misc.TestResources.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {ContactController.class, GlobalErrorController.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ContactControllerUnitTest {

    @MockBean
    private ContactManagerService contactManagerService;

    @Autowired
    private MockMvc mockMvc;

    private final String JSON_BODY = """
    {
        "id": "ff55ef9d-e912-4548-a790-50158470fafa",
       "name": "Isabella Rodriguez",
       "phoneNumbers": {
         "home": "+15551234567",
         "work": "+15559876543",
         "mobile": "+15555555555"
       },
       "emails": {
         "personal": "isabella.rodriguez@example.com",
         "work": "irodriguez@company.com",
         "other": "bella.r@email.net"
       },
       "addresses": {
         "home": {
           "country": "United States",
           "state": "California",
           "city": "Los Angeles",
           "zipcode": "90001",
           "street": "123 Main Street"
         }
       }
    }
    """;

    @Nested
    @DisplayName("GetAll /api/contacts")
    public class GetAll {

        private static List<Arguments> args() {
            return methodSourceArgs()
                .jwtFor(Users.JULIA)
                .jwtFor(Users.JOE)
                .jwtFor(Users.ROBERT).done();
        }

        @ParameterizedTest(name = "{argumentsWithNames}")
        @MethodSource("args")
        @DisplayName("Should return all of the contacts for a user")
        void shouldReturnAllOfTheContactsForAUser(Jwt jwt) throws Exception {
            String userId = jwt.getClaimAsString("id");
            String username = jwt.getClaimAsString("username");
            when(contactManagerService.findAllByUserId(eq(userId)))
                .thenReturn(getContactsByUser(username));

            ResultActions resultActions = mockMvc.perform(get("/api/contacts")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            switch (username) {
                case "joe" -> {
                    resultActions
                        .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("Greg from accounting", "Coworker Fred", "Sister Monica")))
                        .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(5)))
                        .andExpect(jsonPath("$[*].addresses.*").value(hasSize(4)))
                        .andExpect(jsonPath("$[*].emails.*").value(hasSize(3)));
                }
                case "robert" -> {
                    resultActions
                        .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("Best friend Julia", "Mom", "Pizza and burgers", "Uncle Jeff")))
                        .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(7)))
                        .andExpect(jsonPath("$[*].addresses.*").value(hasSize(7)))
                        .andExpect(jsonPath("$[*].emails.*").value(hasSize(7)));
                }
                case "julia" -> {
                    resultActions.andExpect(jsonPath("$").isEmpty());
                }
            }

            verify(contactManagerService, once()).findAllByUserId(eq(userId));
        }

        @Test
        @DisplayName("When requesting for contacts for a non-existing user then should respond 404")
        void whenRequestingForContactsForANonExistingUser_thenShouldRespond404() throws Exception {
            when(contactManagerService.findAllByUserId(eq(Users.JULIA.id())))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            mockMvc.perform(get("/api/contacts")
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JULIA.jwt()))
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("User not found"));

            verify(contactManagerService, once()).findAllByUserId(eq(Users.JULIA.id()));
        }
    }

    @Nested
    public class GetById {

        private static List<Arguments> args() {
            return methodSourceArgs()
                .jwtWithUUIDFor(Users.ROBERT, "4fe25947-ecab-489c-a881-e0057124e408")
                .jwtWithUUIDFor(Users.JOE, "5c21433c-3c70-4253-a4b2-52b157be4167").done();
        }

        @ParameterizedTest(name = "jwt = {0} GET /api/contacts/{1} -> 200")
        @MethodSource("args")
        @DisplayName("Should return a contact for a user successfully")
        void shouldReturnAContactForAUserSuccessfully(Jwt jwt, UUID contactId) throws Exception {
            String userId = jwt.getClaimAsString("id");
            when(contactManagerService.findByIdWithUser(eq(contactId), eq(userId)))
                .thenReturn(getContactById(contactId));

            ResultActions ra = mockMvc.perform(get("/api/contacts/" + contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            switch (jwt.getClaimAsString("username")) {
                case "robert" -> {
                    ra
                    .andExpect(jsonPath("$.name").value("Coworker Fred"))
                    .andExpect(jsonPath("$.phoneNumbers.*").value(hasSize(3)))
                    .andExpect(jsonPath("$.emails.*").value(hasSize(1)))
                    .andExpect(jsonPath("$.addresses.*").value(hasSize(1)));
                }
                case "joe" -> {
                    ra
                    .andExpect(jsonPath("$.name").value("Greg from accounting"))
                    .andExpect(jsonPath("$.phoneNumbers.*").value(hasSize(1)))
                    .andExpect(jsonPath("$.emails.*").value(hasSize(1)))
                    .andExpect(jsonPath("$.addresses.*").value(hasSize(2)));
                }
                case "julia" -> {
                    ra.andExpect(jsonPath("$").isEmpty());
                }
            }

            verify(contactManagerService, once()).findByIdWithUser(eq(contactId), eq(userId));
        }

        @ParameterizedTest(name = "jwt = {0} GET /api/contacts/{1} -> 404")
        @MethodSource("args")
        @DisplayName("When requesting for a non-existing contact then should respond 404")
        void whenRequestingForANonExistingContact_thenShouldRespond404(Jwt jwt, UUID contactId) throws Exception {
            String userId = jwt.getClaimAsString("id");
            when(contactManagerService.findByIdWithUser(eq(contactId), eq(userId)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

            mockMvc.perform(get("/api/contacts/" + contactId)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact not found"));

            verify(contactManagerService, once()).findByIdWithUser(eq(contactId), eq(userId));
        }

        @Test
        @DisplayName("GET /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> 400")
        void whenRequestingForAContactThatDoesNotBelongToAUser_thenShouldRespond400() throws Exception {
            final UUID contactId = UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408");
            final String errorMessage = "Contact belongs to another user";
            when(contactManagerService.findByIdWithUser(eq(contactId), eq(Users.ROBERT.id())))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage));

            mockMvc.perform(get("/api/contacts/"+contactId)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.ROBERT.jwt()))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string(errorMessage));

            verify(contactManagerService, once())
                .findByIdWithUser(eq(contactId), eq(Users.ROBERT.id()));
        }
    }

    @Nested
    public class Create {

        private static List<Arguments> args() {
            return methodSourceArgs().jwtFor(Users.ROBERT).jwtFor(Users.JOE).done();
        }

        private final String JSON_CONTENT = """
        {
            "name": "Steve",
            "phoneNumbers": {
                "personal": "+817283640198"
            },
            "emails": {
                "main": "stevan@mymail.com"
            },
            "addresses": {
                "home": {
                    "country": "United States",
                    "street": "467 Jennifer Lane",
                    "state": "North Carolina",
                    "city": "Cary",
                    "zipcode": "2751356723"
                }
            }
        }
        """;

        @ParameterizedTest(name = "jwt = {0} POST /api/contacts -> 201")
        @MethodSource("args")
        @DisplayName("Should create a new contact for a user successfully")
        void shouldCreateANewContactForAUserSuccessfully(Jwt jwt) throws Exception {
            String contactId = jwt.getClaimAsString("id");
            doNothing().when(contactManagerService).saveWithUser(any(Contact.class), eq(contactId));

            mockMvc.perform(post("/api/contacts")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_CONTENT)
            )
            .andExpect(status().isCreated());

            verify(contactManagerService).saveWithUser(any(Contact.class), eq(contactId));
        }

        @Test
        @DisplayName("When saving a new contact for a non-exiting user then should respond 404")
        public void whenSavingANewContactForANonExistingUser_thenShouldRespond404() throws Exception {
           doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
               .when(contactManagerService).saveWithUser(any(Contact.class), eq(Users.JULIA.id()));

            mockMvc.perform(post("/api/contacts")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JULIA.jwt()))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_CONTENT)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().string("User not found"));

            verify(contactManagerService, once()).saveWithUser(any(Contact.class), eq(Users.JULIA.id()));
        }

        @ParameterizedTest(name = "jwt = {0} POST /api/contacts -> 400")
        @MethodSource("args")
        @DisplayName("When creating a new contact with required fields missing then should respond 400 with field violations")
        public void whenCreatingANewContactWithRequiredFieldsMissing_thenShouldRespond400WithFieldViolations(Jwt jwt) throws Exception {
            final String requestBody = "{}";

            mockMvc.perform(post("/api/contacts")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldViolations[*][*]").value(containsInAnyOrder(
                    "phoneNumbers must not be missing",
                    "name must not be missing"
            )));

            verifyNoInteractions(contactManagerService);
        }

        @ParameterizedTest(name = "jwt = {0} POST /api/contacts -> 400")
        @MethodSource("args")
        @DisplayName("When creating a new contact with invalid data then should respond 400 with field violations")
        public void whenCreatingANewContactWithInvalidData_thenShouldRespond400WithFieldViolations(Jwt jwt) throws Exception {
            final String requestBody =
            """
            {
                "emails": {
                    "main":"roger@gmail."
                },
                "phoneNumbers": {
                    "main": "+287o"
                },
                "addresses": {
                    "home": {
                        "country": "US",
                        "state": "St",
                        "city": "ci",
                        "zipcode": 3745818473234523452346,
                        "street": "cit"
                    }
                }
            }
            """;

            mockMvc.perform(post("/api/contacts")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldViolations[*][*]")
            .value(containsInAnyOrder(
                "street is too short",
                "phone number is too short",
                "city is too short",
                "phone number must be just numbers",
                "zipcode is too long",
                "email must be a well-formed email address",
                "state is too short",
                "name must not be missing",
                "country is too short"
            )));

            verifyNoInteractions(contactManagerService);
        }
    }

    @Nested
    public class Update {

        private static List<Arguments> args() {
            return methodSourceArgs().jwtFor(Users.ROBERT).jwtFor(Users.JOE).done();
        }

        @ParameterizedTest(name = "jwt = {0} PUT /api/contacts -> 200")
        @MethodSource("args")
        @DisplayName("Should entirely update a contact for a user successfully")
        void shouldEntirelyUpdateAContactForAUserSuccessfully(Jwt jwt) throws Exception {
            String userId = jwt.getClaimAsString("id");
            doNothing().when(contactManagerService)
                .updateWithUser(any(Contact.class), eq(userId));

            mockMvc.perform(put("/api/contacts")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_BODY)
            )
            .andExpect(status().isOk());

            verify(contactManagerService, once()).updateWithUser(any(Contact.class), eq(userId));
        }

        @ParameterizedTest(name = "jwt = {0} PUT /api/contacts -> 400")
        @MethodSource("args")
        @DisplayName("When updating a contact that is not structured correctly then should respond 400 with field violations")
        void whenUpdatingAContactThatIsNotStructuredCorrectly_thenShouldRespond400WithFieldViolations(Jwt jwt) throws Exception {
            final String requestBody = "{\"name\": \"Bill\"}";

            mockMvc.perform(post("/api/contacts")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldViolations").isMap());

            verifyNoInteractions(contactManagerService);
        }

        @Test
        @DisplayName("When updating a contact that does not belong to the user then should respond 400")
        void whenUpdatingAContactThatDoesNotBelongToTheUser_thenShouldRespond400() throws Exception {
            final String errorMessage = "Contact belongs to another user";
            doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage))
                .when(contactManagerService).updateWithUser(any(Contact.class), eq(Users.JOE.id()));

            mockMvc.perform(put("/api/contacts")
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JOE.jwt()))
                .content(JSON_BODY)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string(errorMessage));

            verify(contactManagerService, once()).updateWithUser(any(Contact.class), eq(Users.JOE.id()));
        }

        @ParameterizedTest(name = "jwt = {0} PUT /api/contacts/{1} -> 404")
        @MethodSource("args")
        @DisplayName("When updating a contact that does not exist then should respond 404")
        void whenUpdatingAContactThatDoesNotExist_thenShouldRespond404(Jwt jwt) throws Exception {
            final String errorMessage = "Contact not found";
            final String userId = jwt.getClaimAsString("id");
            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage))
                .when(contactManagerService).updateWithUser(any(Contact.class), eq(userId));

            mockMvc.perform(put("/api/contacts")
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_BODY)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().string(errorMessage));

            verify(contactManagerService, once()).updateWithUser(any(Contact.class), eq(userId));
        }
    }

    @Nested
    public class Delete {

        private static List<Arguments> args() {
            return methodSourceArgs()
                .jwtWithUUIDFor(Users.JOE, "5c21433c-3c70-4253-a4b2-52b157be4167")
                .jwtWithUUIDFor(Users.ROBERT, "84edd1b9-89a5-4107-a84d-435676c2b8f5").done();
        }

        @ParameterizedTest(name = "jwt = {0} DELETE /api/contacts/{1} -> 200")
        @MethodSource("args")
        @DisplayName("When provided a contactId then should delete a contact successfully")
        void whenProvidedAContactId_thenShouldDeleteAContactSuccessfully(Jwt jwt, UUID contactId) throws Exception {
            String userId = jwt.getClaimAsString("id");
            doNothing().when(contactManagerService).deleteByIdWithUser(eq(contactId), eq(userId));

            mockMvc.perform(delete("/api/contacts/"+contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isOk());

            verify(contactManagerService, once()).deleteByIdWithUser(eq(contactId), eq(userId));
        }

        @ParameterizedTest(name = "jwt = {0} DELETE /api/contacts/{1} -> 404")
        @MethodSource("args")
        @DisplayName("When provided a non-exiting contactId then should respond 404")
        void whenProvidedANonExistingContactId_thenRespond404(Jwt jwt, UUID contactId) throws Exception {
            String userId = jwt.getClaimAsString("id");
            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"))
                .when(contactManagerService).deleteByIdWithUser(eq(contactId), eq(userId));

            mockMvc.perform(delete("/api/contacts/"+contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact not found"));

            verify(contactManagerService, once()).deleteByIdWithUser(eq(contactId), eq(userId));
        }

        @ParameterizedTest(name = "jwt = {0} DELETE /api/contacts/{1} -> 400")
        @MethodSource("args")
        @DisplayName("When deleting a contact that does not belong to a user then should respond 400")
        public void whenDeletingAContactThatDoesNotBelongToAUser_thenShouldRespond400(Jwt jwt, UUID contactId) throws Exception {
            String userId = jwt.getClaimAsString("id");
            doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contact belongs to another user"))
                .when(contactManagerService).deleteByIdWithUser(eq(contactId), eq(userId));

            mockMvc.perform(delete("/api/contacts/"+contactId)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact belongs to another user"));

            verify(contactManagerService, once()).deleteByIdWithUser(eq(contactId), eq(userId));
        }

        @Test
        @DisplayName("When deleting a contact that does not exist then should respond 404")
        void whenDeletingAContactThatDoesNotExist_thenShouldRespond404() throws Exception {
            final UUID contactId = UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7");
            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"))
                .when(contactManagerService).deleteByIdWithUser(eq(contactId), eq(Users.JOE.id()));

            mockMvc.perform(delete("/api/contacts/"+contactId)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JOE.jwt()))
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact not found"));

            verify(contactManagerService, once()).deleteByIdWithUser(eq(contactId), eq(Users.JOE.id()));
        }
    }
}