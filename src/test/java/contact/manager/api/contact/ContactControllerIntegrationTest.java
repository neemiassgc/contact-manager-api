package contact.manager.api.contact;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static contact.manager.api.misc.TestResources.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
public class ContactControllerIntegrationTest {

    @Autowired
    private ContactManagerService contactManagerService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("/api/contacts")
    public class GetAll {

        private static List<Arguments> args() {
            return methodSourceArgs().jwtFor(Users.ROBERT).jwtFor(Users.JOE).done();
        }

        @ParameterizedTest(name = "jwt = {0} GET /api/contacts -> 200")
        @MethodSource("args")
        @DisplayName("Should respond with all of the contacts for a user")
        public void shouldRespondWithAllOfTheContactsForAUser(Jwt jwt) throws Exception {
            ResultActions ra =  mockMvc.perform(get("/api/contacts")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            switch (jwt.getClaimAsString("username")) {
                case "joe" -> {
                    ra
                    .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("Greg from accounting", "Coworker Fred", "Sister Monica")))
                    .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(5)))
                    .andExpect(jsonPath("$[*].addresses.*").value(hasSize(4)))
                    .andExpect(jsonPath("$[*].emails.*").value(hasSize(3)));
                }
                case "robert" -> {
                    ra
                    .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("Best friend Julia", "Mom", "Pizza and burgers", "Uncle Jeff")))
                    .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(7)))
                    .andExpect(jsonPath("$[*].addresses.*").value(hasSize(7)))
                    .andExpect(jsonPath("$[*].emails.*").value(hasSize(7)));
                }
                case "julia" -> {
                    ra.andExpect(content().string("User not found"));
                }
            }
        }

        @Test
        @DisplayName("When fetching all contacts for a non-existing user then should respond 404")
        public void whenFetchingAllContactsForANonExistingUser_thenShouldRespond404() throws Exception {
            mockMvc.perform(get("/api/contacts")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JULIA.jwt()))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().string("User not found"));
        }

    }

    @Nested
    public class GetById {

        private static List<Arguments> args() {
            return methodSourceArgs()
                .jwtWithUUIDFor(Users.ROBERT, "7f23057f-77bd-4568-ac64-e933abae9a09")
                .jwtWithUUIDFor(Users.JOE, "5c21433c-3c70-4253-a4b2-52b157be4167").done();
        }

        @ParameterizedTest(name = "jwt = {0} GET /api/contacts/{1} -> 200")
        @MethodSource("args")
        @DisplayName("Should return a contact for a user successfully")
        public void shouldReturnAContactForAUserSuccessfully(Jwt jwt, String contactId) throws Exception {
            ResultActions ra = mockMvc.perform(get("/api/contacts/" + contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

            switch (jwt.getClaimAsString("username")) {
                case "robert" -> {
                    ra
                    .andExpect(jsonPath("$.name").value("Best friend Julia"))
                    .andExpect(jsonPath("$.phoneNumbers.*").value(hasSize(3)))
                    .andExpect(jsonPath("$.emails.*").value(hasSize(3)))
                    .andExpect(jsonPath("$.addresses.*").value(hasSize(1)));
                }
                case "joe" -> {
                    ra
                    .andExpect(jsonPath("$.name").value("Greg from accounting"))
                    .andExpect(jsonPath("$.phoneNumbers.*").value(hasSize(1)))
                    .andExpect(jsonPath("$.emails.*").value(hasSize(1)))
                    .andExpect(jsonPath("$.addresses.*").value(hasSize(2)));
                }
            }
        }

        @Test
        @DisplayName("When requesting for a contact that does not exist then should respond 404")
        void whenRequestingForAContactThatDoesNotExist_thenShouldRespond404() throws Exception {
            mockMvc.perform(get("/api/contacts/c97775aa-b7f3-49c0-a586-d0466ba592bf")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.ROBERT.jwt()))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact not found"));
        }

        @Test
        @DisplayName("When requesting for a contact that does not belong to a user then should respond 400")
        void whenRequestingForAContactThatDoesNotBelongToAUser_thenShouldRespond400() throws Exception {
            mockMvc.perform(get("/api/contacts/4fe25947-ecab-489c-a881-e0057124e408")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.ROBERT.jwt()))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact belongs to another user"));
        }


        @Test
        @DisplayName("When requesting for a contact for a non-existing user then should respond 400")
        void whenRequestingForAContactForANonExistingUser_thenShouldRespond400() throws Exception {
            mockMvc.perform(get("/api/contacts/4fe25947-ecab-489c-a881-e0057124e408")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JULIA.jwt()))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact belongs to another user"));
        }

        @Test
        @DisplayName("When requesting for a contact without authentication then should deny with 401")
        void whenRequestingForAContactWithoutAuthentication_thenShouldDenyWith401() throws Exception {
            mockMvc.perform(get("/api/contacts/4fe25947-ecab-489c-a881-e0057124e408")
                .with(SecurityMockMvcRequestPostProcessors.anonymous())
                .accept(MediaType.ALL)
            )
            .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    public class Create {

        private static List<Arguments> args() {
            return methodSourceArgs().jwtFor(Users.ROBERT).jwtFor(Users.JOE).done();
        }

        @ParameterizedTest(name = "jwt = {0} POST /api/contacts  -> 201")
        @MethodSource("args")
        @DisplayName("Should create a new contact for a user successfully")
        void shouldCreateANewContactForAUserSuccessfully(Jwt jwt) throws Exception {
            String newContactJson = newContactJson();

            mockMvc.perform(post("/api/contacts")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newContactJson)
            )
            .andExpect(status().isCreated());

            final int actualContactsCount = contactManagerService.findAll().size();
            assertThat(actualContactsCount).isEqualTo(8);
        }

        @ParameterizedTest(name = "jwt = {0} POST /api/contacts  -> 400")
        @MethodSource("args")
        @DisplayName("When provided data with missing fields then should respond 400 along with field violations")
        public void whenProvidedJsonWithMissingFields_thenShouldRespond400AlongWithFieldViolations(Jwt jwt) throws Exception {
            final String jsonWithMissingFields = "{}";

            mockMvc.perform(post("/api/contacts")
                .content(jsonWithMissingFields)
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
        }

        @ParameterizedTest(name = "jwt = {0} POST /api/contacts  -> 400")
        @MethodSource("args")
        @DisplayName("When provided json with invalid fields then should respond 400 along with field violations")
        public void whenProvidedJsonWithInvalidFields_shouldShouldRespond400AlongWithFieldViolations(Jwt jwt) throws Exception {
            final String jsonWithInvalidFields =
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
                .content(jsonWithInvalidFields)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JOE.jwt()))
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
        }

        @Test
        @DisplayName("When creating a contact for a non-existing user then should respond 404")
        void whenCreatingAContactForANonExistingUser_thenShouldRespond404() throws Exception {
            final String jsonContent = """
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
                        "zipcode": "27513"
                    }
                }
            }
            """;

            mockMvc.perform(post("/api/contacts")
                .content(jsonContent)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JULIA.jwt()))
            )
            .andExpect(status().isNotFound())
            .andExpect(content().string("User not found"));
        }
    }

    @Nested
    public class Update {

        private static List<Arguments> args() {
            return methodSourceArgs()
                .jwtWithUUIDFor(Users.ROBERT, "84edd1b9-89a5-4107-a84d-435676c2b8f5")
                .jwtWithUUIDFor(Users.JOE, "35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7").done();
        }

        @ParameterizedTest(name = "jwt = {0} PUT /api/contacts/{1}  -> 200")
        @MethodSource("args")
        @DisplayName("Should entirely update a contact for a user successfully")
        void shouldEntirelyUpdateAContactForAUserSuccessfully(Jwt jwt, String contactId) throws Exception {
            String newContactJson = newContactJsonWithId(contactId);
            mockMvc.perform(put("/api/contacts/" + contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newContactJson)
            )
            .andExpect(status().isOk());
        }

        @ParameterizedTest(name = "jwt = {0} PUT /api/contacts/{1}  -> 200")
        @MethodSource("args")
        @DisplayName("When provided Json data without id then should update successfully responding 200")
        void whenProvidedJsonDataWithoutId_thenShouldUpdateSuccessfullyResponding200(Jwt jwt, String contactId) throws Exception {
            String newContactJson = newContactJsonWithoutId();
            mockMvc.perform(put("/api/contacts/" + contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newContactJson)
            )
            .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When updating a contact that does not belong to a user then should respond 400")
        void whenUpdatingAContactThatDoesNotBelongToAUser_thenShouldRespond400() throws Exception {
            String contactId = "b621650d-4a81-4016-a917-4a8a4992aaef";
            mockMvc.perform(put("/api/contacts/" + contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JOE.jwt()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(newContactJsonWithId(contactId))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact belongs to another user"));
        }

        @Test
        @DisplayName("When updating a contact that does not exist then should respond 404")
        void whenUpdatingAContactThatDoesNotExist_thenShouldRespond404() throws Exception {
            String nonExistingContactId = "b4f5cda4-9765-4dd2-a4c4-b178770cfd88";
            mockMvc.perform(put("/api/contacts/" + nonExistingContactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.ROBERT.jwt()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(newContactJsonWithId(nonExistingContactId))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact not found"));
        }

        @ParameterizedTest(name = "jwt = {0} PUT /api/contacts/{1} -> 400")
        @MethodSource("args")
        void whenEnteredAMalFormedContactToUpdate_thenShouldRespond400AlongWithFieldViolations(Jwt jwt, String contactId) throws Exception {
            final String malFormedJson = "{\"name\": \"Bill\"}";
            mockMvc.perform(put("/api/contacts/" + contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .contentType(MediaType.APPLICATION_JSON)
                .content(malFormedJson)
                .accept(MediaType.ALL)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldViolations").isMap());
        }
    }

    @Nested
    public class Delete {

        private static List<Arguments> args() {
            return methodSourceArgs()
                .jwtWithUUIDFor(Users.ROBERT, "84edd1b9-89a5-4107-a84d-435676c2b8f5")
                .jwtWithUUIDFor(Users.JOE, "35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7").done();
        }

        @ParameterizedTest(name = "jwt = {0} DELETE /api/contacts/{1}  -> 200")
        @MethodSource("args")
        @DisplayName("Should delete a contact for a user successfully")
        void shouldDeleteAContactForAUserSuccessfully(Jwt jwt, String contactId) throws Exception {
            mockMvc.perform(delete("/api/contacts/" + contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isOk());

            int actualContactsCount = contactManagerService.findAll().size();
            assertThat(actualContactsCount).isEqualTo(6);
        }

        @Test
        @DisplayName("When deleting a contact that does not belong to a user then should respond 400")
        void whenDeletingAContactThatDoesNotBelongToAUser_thenShouldRespond400() throws Exception {
            mockMvc.perform(delete("/api/contacts/84edd1b9-89a5-4107-a84d-435676c2b8f5")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JOE.jwt()))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact belongs to another user"));
        }

        @Test
        @DisplayName("When deleting a non-existing contact then should respond 404")
        void whenDeletingANonExistingContact_thenShouldRespond404() throws Exception {
            mockMvc.perform(delete("/api/contacts/892002b1-0153-465f-b084-31058cfaf1e7")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.ROBERT.jwt()))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact not found"));
        }

        @Test
        @DisplayName("When deleting a non-existing contact then should respond 400")
        void whenDeletingAContactForANonExistingUser_thenShouldRespond400() throws Exception {
            mockMvc.perform(delete("/api/contacts/84edd1b9-89a5-4107-a84d-435676c2b8f5")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JULIA.jwt()))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Contact belongs to another user"));
        }
    }
}
