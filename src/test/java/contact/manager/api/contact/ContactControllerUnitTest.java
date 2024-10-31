package contact.manager.api.contact;

import contact.manager.api.global.GlobalErrorController;
import contact.manager.api.misc.TestResources;
import org.mockito.ArgumentMatchers;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
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

    @Nested
    public class TestCasesForJoe {

        @Test
        @DisplayName("GET /api/contacts -> OK 200")
        void should_response_all_the_contacts_with_OK_200() throws Exception {
            when(contactManagerService.findAllByUserId(ArgumentMatchers.eq(TestResources.idForJoe())))
                .thenReturn(TestResources.getContactsForJoe());

            mockMvc.perform(get("/api/contacts")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("Greg from accounting", "Coworker Fred", "Sister Monica")))
            .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(5)))
            .andExpect(jsonPath("$[*].addresses.*").value(hasSize(4)))
            .andExpect(jsonPath("$[*].emails.*").value(hasSize(3)));

            verify(contactManagerService, TestResources.once()).findAllByUserId(ArgumentMatchers.eq(TestResources.idForJoe()));
            verifyNoMoreInteractions(contactManagerService);
        }

        @Test
        @DisplayName("GET /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> OK 200")
        void should_return_a_contact_with_OK_200() throws Exception {
            final UUID contactId = UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408");
            when(contactManagerService.findByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForJoe())))
                .thenReturn(TestResources.getContactById(contactId));

            mockMvc.perform(get("/api/contacts/" + contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Coworker Fred"))
            .andExpect(jsonPath("$.phoneNumbers.*").value(hasSize(3)))
            .andExpect(jsonPath("$.emails.*").value(hasSize(1)))
            .andExpect(jsonPath("$.addresses.*").value(hasSize(1)));

            verify(contactManagerService, TestResources.once()).findByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForJoe()));
            verifyNoMoreInteractions(contactManagerService);
        }

        @Test
        @DisplayName("POST /api/contacts -> CREATED 201")
        void should_create_a_new_contact_successfully() throws Exception {
            doNothing().when(contactManagerService).saveWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForJoe()));

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
                        "zipcode": "2751356723"
                    }
                }
            }
            """;

            mockMvc.perform(post("/api/contacts")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
            )
            .andExpect(status().isCreated());

            verify(contactManagerService).saveWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForJoe()));
            verifyNoMoreInteractions(contactManagerService);
        }

        @Test
        @DisplayName("PATCH /api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef -> OK 200")
        void should_update_isolated_fields_of_a_contact_successfully() throws Exception {
            when(contactManagerService.updateWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForJoe())))
                .thenReturn(TestResources.getContactById(UUID.fromString("b621650d-4a81-4016-a917-4a8a4992aaef")));

            final String requestBody = """
            {
                "name": "Bill",
                "phoneNumbers": {
                    "cellphone": "+811234567890"
                },
                "addresses": {
                }
            }
            """;

            mockMvc.perform(patch("/api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap());

            verify(contactManagerService, TestResources.once()).updateWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForJoe()));
            verifyNoMoreInteractions(contactManagerService);
        }

        @Test
        @DisplayName("DELETE /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> OK 200")
        void should_delete_a_contact_successfully() throws Exception {
            final UUID contactId = UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408");
            doNothing().when(contactManagerService).deleteByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForJoe()));

            mockMvc.perform(delete("/api/contacts/"+contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isOk());

            verify(contactManagerService, TestResources.once()).deleteByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForJoe()));
            verifyNoMoreInteractions(contactManagerService);
        }
    }

    @Nested
    public class TestCasesForRobert {

        @Test
        @DisplayName("GET /api/contacts -> OK 200")
        void should_return_all_the_contacts_with_OK_200() throws Exception {
            when(contactManagerService.findAllByUserId(ArgumentMatchers.eq(TestResources.idForRobert())))
                .thenReturn(TestResources.getContactsForRobert());

            mockMvc.perform(get("/api/contacts")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForRobert()))
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[*].name").value(containsInAnyOrder("Best friend Julia", "Mom", "Pizza and burgers", "Uncle Jeff")))
            .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(7)))
            .andExpect(jsonPath("$[*].addresses.*").value(hasSize(7)))
            .andExpect(jsonPath("$[*].emails.*").value(hasSize(7)));

            verify(contactManagerService, TestResources.once()).findAllByUserId(ArgumentMatchers.eq(TestResources.idForRobert()));
            verifyNoMoreInteractions(contactManagerService);
        }

        @Test
        @DisplayName("GET /api/contacts/84edd1b9-89a5-4107-a84d-435676c2b8f5 -> 200 OK")
        void should_return_a_contact_with_OK_200() throws Exception {
            final UUID contactId = UUID.fromString("84edd1b9-89a5-4107-a84d-435676c2b8f5");
            when(contactManagerService.findByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForRobert())))
                .thenReturn(TestResources.getContactById(contactId));

            mockMvc.perform(get("/api/contacts/"+contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForRobert()))
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Mom"))
            .andExpect(jsonPath("$.phoneNumbers.*").value(hasSize(1)))
            .andExpect(jsonPath("$.emails.*").value(hasSize(1)))
            .andExpect(jsonPath("$.addresses.*").value(hasSize(1)));

            verify(contactManagerService, TestResources.once()).findByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForRobert()));
            verifyNoMoreInteractions(contactManagerService);
        }

        @Test
        @DisplayName("POST /api/contacts -> CREATED 201")
        void should_create_a_new_contact_successfully() throws Exception {
            doNothing().when(contactManagerService).saveWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForRobert()));

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
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForRobert()))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
            )
            .andExpect(status().isCreated());

            verify(contactManagerService).saveWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForRobert()));
            verifyNoMoreInteractions(contactManagerService);
        }

        @Test
        @DisplayName("PATCH /api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef -> OK 200")
        void should_update_isolated_fields_of_a_contact_successfully() throws Exception {
            when(contactManagerService.updateWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForRobert())))
                .thenReturn(TestResources.getContactById(UUID.fromString("b621650d-4a81-4016-a917-4a8a4992aaef")));

            final String requestBody = """
            {
                "name": "Bill",
                "phoneNumbers": {
                    "cellphone": "+811234567890"
                },
                "addresses": {
                }
            }
            """;

            mockMvc.perform(patch("/api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef")
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForRobert()))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isMap());

            verify(contactManagerService, TestResources.once()).updateWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForRobert()));
            verifyNoMoreInteractions(contactManagerService);
        }

        @Test
        @DisplayName("DELETE /api/contacts/7f23057f-77bd-4568-ac64-e933abae9a09 -> OK 200")
        void should_delete_a_contact_successfully() throws Exception {
            final UUID contactId = UUID.fromString("7f23057f-77bd-4568-ac64-e933abae9a09");
            doNothing().when(contactManagerService).deleteByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForRobert()));

            mockMvc.perform(delete("/api/contacts/"+contactId)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForRobert()))
                .accept(MediaType.ALL)
            )
            .andExpect(status().isOk());

            verify(contactManagerService, TestResources.once()).deleteByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForRobert()));
            verifyNoMoreInteractions(contactManagerService);
        }
    }

    @Nested
    public class TestCasesForFieldValidations {

        @Test
        @DisplayName("POST /api/contacts -> 400 BAD REQUEST")
        public void should_return_violations_when_adding_a_new_contact_with_missing_fields() throws Exception {
            final String requestBody = "{}";

            mockMvc.perform(post("/api/contacts")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldViolations[*][*]").value(containsInAnyOrder(
                "phoneNumbers must not be missing",
                "name must not be missing"
            )));

            verifyNoInteractions(contactManagerService);
        }

        @Test
        @DisplayName("POST /api/contacts -> 400 BAD REQUEST")
        public void should_return_field_violations_when_creating_a_new_contact_with_invalid_data() throws Exception {
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
                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
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

    @Test
    @DisplayName("GET /api/contacts -> 404 NOT FOUND")
    void should_respond_404_when_requesting_for_all_of_the_contacts_for_a_non_existing_user() throws Exception {
        when(contactManagerService.findAllByUserId(ArgumentMatchers.eq(TestResources.idForJulia())))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        mockMvc.perform(get("/api/contacts")
            .accept(MediaType.ALL)
            .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJulia()))
        )
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string("User not found"));

        verify(contactManagerService, TestResources.once()).findAllByUserId(ArgumentMatchers.eq(TestResources.idForJulia()));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("GET /api/contacts/c97775aa-b7f3-49c0-a586-d0466ba592bf -> 404 NOT FOUND")
    void should_respond_404_when_requesting_for_a_contact_that_does_not_exist() throws Exception {
        final UUID contactId = UUID.fromString("c97775aa-b7f3-49c0-a586-d0466ba592bf");
        when(contactManagerService.findByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForJoe())))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        mockMvc.perform(get("/api/contacts/"+contactId)
            .accept(MediaType.ALL)
            .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
        )
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Contact not found"));

        verify(contactManagerService, TestResources.once()).findByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForJoe()));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("GET /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> 400 BAD_REQUEST")
    void should_respond_400_BAD_REQUEST_when_requesting_for_a_contact_that_does_not_belong_to_the_current_user() throws Exception {
        final UUID contactId = UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408");
        final String errorMessage = "Contact belongs to another user";
        when(contactManagerService.findByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForRobert())))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage));

        mockMvc.perform(get("/api/contacts/"+contactId)
            .accept(MediaType.ALL)
            .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForRobert()))
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string(errorMessage));

        verify(contactManagerService, TestResources.once()).findByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForRobert()));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("PATCH /api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef -> 400 BAD_REQUEST")
    void should_respond_400_when_trying_to_update_a_contact_whose_user_is_not_the_owner() throws Exception {
        final UUID contactId = UUID.fromString("b621650d-4a81-4016-a917-4a8a4992aaef");
        final String errorMessage = "Contact belongs to another user";
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage))
            .when(contactManagerService).updateWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForJoe()));

        mockMvc.perform(patch("/api/contacts/"+contactId)
            .accept(MediaType.ALL)
            .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
            .content("{\"name\": \"Billy\"}")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andExpect(content().string(errorMessage));

        verify(contactManagerService, TestResources.once()).updateWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForJoe()));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("PATCH /api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef -> 404 NOT FOUND")
    void should_respond_404_when_trying_to_update_a_contact_that_does_not_exist() throws Exception {
        final UUID contactId = UUID.fromString("b621650d-4a81-4016-a917-4a8a4992aaef");
        final String errorMessage = "Contact not found";
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage))
            .when(contactManagerService).updateWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForJoe()));

        mockMvc.perform(patch("/api/contacts/"+contactId)
            .accept(MediaType.ALL)
            .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"Billy\"}")
        )
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string(errorMessage));

        verify(contactManagerService, TestResources.once()).updateWithUser(any(Contact.class), ArgumentMatchers.eq(TestResources.idForJoe()));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("DELETE /api/contacts/8fb2bd75-9aec-4cc5-b77b-a95f06081388 -> 400 BAD_REQUEST")
    public void should_respond_400_when_deleting_a_concat_whose_user_does_not_own_it() throws Exception {
        final UUID contactId = UUID.fromString("8fb2bd75-9aec-4cc5-b77b-a95f06081388");
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contact belongs to another user"))
            .when(contactManagerService).deleteByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForJoe()));

        mockMvc.perform(delete("/api/contacts/"+contactId)
            .accept(MediaType.ALL)
            .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Contact belongs to another user"));

        verify(contactManagerService, TestResources.once()).deleteByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForJoe()));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("DELETE /api/contacts/35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7 -> 404 NOT_FOUND")
    void should_respond_404_when_trying_to_delete_a_contact_that_does_not_exist() throws Exception {
        final UUID contactId = UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7");
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"))
            .when(contactManagerService).deleteByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForJoe()));

        mockMvc.perform(delete("/api/contacts/"+contactId)
            .accept(MediaType.ALL)
            .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe()))
        )
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string("Contact not found"));

        verify(contactManagerService, TestResources.once()).deleteByIdWithUser(eq(contactId), ArgumentMatchers.eq(TestResources.idForJoe()));
        verifyNoMoreInteractions(contactManagerService);
    }
}
