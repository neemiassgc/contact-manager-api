package com.spring.boot.controller;

import com.spring.boot.TestResources;
import com.spring.boot.controllers.ContactController;
import com.spring.boot.controllers.GlobalErrorController;
import com.spring.boot.entities.Contact;
import com.spring.boot.services.ContactManagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {ContactController.class, GlobalErrorController.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ContactControlellerUnitTest {

    @MockBean
    private ContactManagerService contactManagerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/contacts -> OK 200")
    void should_response_all_the_contacts_for_Joe_with_OK_200() throws Exception {
        shouldReturnAllContacts("joe", 5, 4, 3, "Greg from accounting", "Coworker Fred", "Sister Monica");
    }

    @Test
    @DisplayName("GET /api/contacts -> OK 200")
    void should_return_all_the_contacts_for_Robert_with_OK_200() throws Exception {
        shouldReturnAllContacts("robert", 7, 7, 7, "Best friend Julia", "Mom", "Pizza and burgers", "Uncle Jeff");
    }

    private void shouldReturnAllContacts(String user, int phoneNumberSize, int addressesSize, int emailsSize, String... names) throws Exception {
        when(contactManagerService.findAllByUsername(eq(user)))
            .thenReturn(user.equals("joe") ? TestResources.getContactsForJoe() : TestResources.getContactsForRobert());

        mockMvc.perform(get("/api/contacts")
            .header("Authorization", "Bearer "+(user.equals("joe") ? jwtTokenForJoe() : jwtTokenForRobert()))
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[*].name").value(containsInAnyOrder(names)))
        .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(phoneNumberSize)))
        .andExpect(jsonPath("$[*].addresses.*").value(hasSize(addressesSize)))
        .andExpect(jsonPath("$[*].emails.*").value(hasSize(emailsSize)));

        verify(contactManagerService, times(1)).findAllByUsername(eq(user));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("GET /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> OK 200")
    void should_return_a_contact_for_the_user_Joe_with_OK_200() throws Exception {
        final UUID contactId = UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408");
        shouldReturnAContact("joe", 3, contactId, "Coworker Fred");
    }

    @Test
    @DisplayName("GET /api/contacts/84edd1b9-89a5-4107-a84d-435676c2b8f5 -> 200 OK")
    void should_return_a_contact_for_the_user_Robert_with_OK_200() throws Exception {
        final UUID contactId = UUID.fromString("84edd1b9-89a5-4107-a84d-435676c2b8f5");
        shouldReturnAContact("robert", 1, contactId, "Mom");
    }

    private void shouldReturnAContact(String user, int phoneNumberSize, UUID contactId, String contactName) throws Exception {
        when(contactManagerService.findByIdWithUser(eq(contactId), eq(user)))
                .thenReturn(TestResources.getContactById(contactId));

        mockMvc.perform(get("/api/contacts/"+contactId)
            .header("Authorization", "Bearer "+(user.equals("joe") ? jwtTokenForJoe() : jwtTokenForRobert()))
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(contactName))
        .andExpect(jsonPath("$.phoneNumbers.*").value(hasSize(phoneNumberSize)))
        .andExpect(jsonPath("$.emails.*").value(hasSize(1)))
        .andExpect(jsonPath("$.addresses.*").value(hasSize(1)));

        verify(contactManagerService, times(1)).findByIdWithUser(eq(contactId), eq(user));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("GET /api/contacts/c97775aa-b7f3-49c0-a586-d0466ba592bf -> 404 NOT FOUND")
    void should_respond_404_NOT_FOUND_when_requesting_for_a_contact_that_does_not_exist() throws Exception {
        final UUID contactId = UUID.fromString("c97775aa-b7f3-49c0-a586-d0466ba592bf");
        when(contactManagerService.findByIdWithUser(eq(contactId), eq("robert")))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        assertNotFound("robert", "GET", contactId);
    }

    @Test
    @DisplayName("GET /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> 404 NOT FOUND")
    void should_respond_404_NOT_FOUND_when_requesting_for_a_contact_that_does_not_belong_to_the_current_user() throws Exception {
        final UUID contactId = UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408");
        final String errorMessage = "Contact does not belong to the user: robert";
        when(contactManagerService.findByIdWithUser(eq(contactId), eq("robert")))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage));

        assertNotFound("robert", "GET", contactId, errorMessage);
    }

    @Test
    @DisplayName("POST /api/contacts -> CREATED 201")
    void should_create_a_contact_for_the_user_Robert_successfully() throws Exception {
        shouldCreateAContact("robert");
    }

    @Test
    @DisplayName("POST /api/contacts -> CREATED 201")
    void should_create_a_contact_for_the_user_Joe_successfully() throws Exception {
        shouldCreateAContact("joe");
    }

    private void shouldCreateAContact(String user) throws Exception {
        doNothing().when(contactManagerService).saveWithUser(any(Contact.class), eq(user));

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
            .header("Authorization", "Bearer "+(user.equals("joe") ? jwtTokenForJoe() : jwtTokenForRobert()))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonContent)
        )
        .andExpect(status().isCreated());

        verify(contactManagerService).saveWithUser(any(Contact.class), eq(user));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("POST /api/contacts -> 400 BAD_REQUEST")
    void should_respond_400_with_field_violation_errors_when_posting_a_bad_formatted_json() throws Exception {
        final String jsonContent = """
        {
            "phoneNumbers": {
            },
            "emails": {
            }
        }
        """;

        mockMvc.perform(post("/api/contacts")
            .header("Authorization", "Bearer "+jwtTokenForRobert())
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonContent)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fieldViolations").value(hasSize(3)))
        .andExpect(jsonPath("$.fieldViolations[*]").value(containsInAnyOrder(
            "'phoneNumbers' must have at least 1 item",
            "'name' must not be null",
            "'phoneNumbers' must have between 1 and 50 items"
        )));

        verifyNoInteractions(contactManagerService);
    }

    @Test
    @DisplayName("PUT /api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef -> OK 200")
    void should_update_custom_fields_of_a_contact_for_the_Robert_successfully() throws Exception {
        shouldUpdateCustomFields("robert");
    }

    @Test
    @DisplayName("PUT /api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef -> OK 200")
    void should_update_custom_fields_of_a_contact_for_the_Joe_successfully() throws Exception {
        shouldUpdateCustomFields("joe");
    }

    private void shouldUpdateCustomFields(String user) throws Exception {
        doNothing().when(contactManagerService).updateWithUser(any(Contact.class), eq(user));

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

        mockMvc.perform(put("/api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef")
            .header("Authorization", "Bearer "+(user.equals("joe") ? jwtTokenForJoe() : jwtTokenForRobert()))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
        .andExpect(status().isOk());

        verify(contactManagerService, times(1)).updateWithUser(any(Contact.class), eq(user));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("DELETE /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> OK 200")
    void should_delete_a_contact_for_the_user_Joe_without_any_problems() throws Exception {
        final UUID contactId = UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408");
        shouldDeleteSuccessfully("joe", contactId);
    }

    @Test
    @DisplayName("DELETE /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> OK 200")
    void should_delete_a_contact_for_the_user_Robert_without_any_problems() throws Exception {
        final UUID contactId = UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408");
        shouldDeleteSuccessfully("robert", contactId);
    }

    private void shouldDeleteSuccessfully(String user, UUID contactId) throws Exception {
        doNothing().when(contactManagerService).deleteByIdWithUser(eq(contactId), eq(user));

        mockMvc.perform(delete("/api/contacts/"+contactId)
            .header("Authorization", "Bearer "+(user.equals("joe") ? jwtTokenForJoe() : jwtTokenForRobert()))
            .accept(MediaType.ALL)
        )
        .andExpect(status().isOk());

        verify(contactManagerService, times(1)).deleteByIdWithUser(eq(contactId), eq(user));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("DELETE /api/contacts/35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7 -> 404 NOT_FOUND")
    void should_respond_404_when_trying_to_delete_a_contact_that_does_not_exist() throws Exception {
        final UUID contactId = UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7");
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"))
            .when(contactManagerService).deleteByIdWithUser(eq(contactId), eq("joe"));

        assertNotFound("joe", "DELETE", contactId);
    }

    private void assertNotFound(String user, String httpMethod, UUID contactId) throws Exception {
        assertNotFound(user, httpMethod, contactId, "Contact not found");
    }

    private void assertNotFound(String user, String httpMethod, UUID contactId, String errorMessage) throws Exception {
        final Map<String, Function<String, MockHttpServletRequestBuilder>> httpMethodPicker = new HashMap<>();
        httpMethodPicker.put("GET", MockMvcRequestBuilders::get);
        httpMethodPicker.put("DELETE", MockMvcRequestBuilders::delete);

        mockMvc.perform(httpMethodPicker.get(httpMethod).apply("/api/contacts/"+contactId)
            .header("Authorization", "Bearer "+(user.equals("joe") ? jwtTokenForJoe() : jwtTokenForRobert()))
            .accept(MediaType.ALL)
        )
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string(errorMessage));

        if (httpMethod.equals("DELETE"))
            verify(contactManagerService, times(1)).deleteByIdWithUser(eq(contactId), eq(user));
        if (httpMethod.equals("GET"))
            verify(contactManagerService, times(1)).findByIdWithUser(eq(contactId), eq("robert"));

        verifyNoMoreInteractions(contactManagerService);
    }

    private String jwtTokenForJoe() {
        return """
            eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJuNmpaamhHcmtpd2xnT0hmVDB1dEJ5cV
            Q4aXBSSG5Edzh3cVpDV1RDa2VZIn0.eyJleHAiOjIwMjE3NzQ1MTYsImlhdCI6MTcwNjQxNDUxNiwianRp
            IjoiMDY0Yzg0NjMtMTk5Mi00M2YzLTgzZTAtZDZlOWU2NDU1YjgzIiwiaXNzIjoiaHR0cDovL2xvY2FsaG
            9zdDo4MDAwL3JlYWxtcy9tYWluIiwic3ViIjoiOWRjNjc4YmYtY2UwZi00NGI3LWEyNGQtNjUyMThhMGZl
            ZGM4IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY29udGFjdC1tYW5hZ2VyIiwic2Vzc2lvbl9zdGF0ZSI6Ij
            VhNTA4NjQzLTZiMWYtNDBkOS1hNGNiLTUzY2VjNmJkZWVmZCIsInNjb3BlIjoiY29udGFjdC1tYW5hZ2Vy
            Iiwic2lkIjoiNWE1MDg2NDMtNmIxZi00MGQ5LWE0Y2ItNTNjZWM2YmRlZWZkIiwidXNlcm5hbWUiOiJqb2
            UifQ.yMED1qj_q8IzvHBGo4xWkOJ443kISwQp4w2cr-VHjCx2DqzisyyHxavvq5GhZMx5PHOINi_PIZgP0
            weFV84g9xpm1jjkiuhyrVfwRfaq3z6svwEZcGDWU-d-wy_58zC_ZrpRrm4CRAeNg-SzKLNUwJ1imK24HCG
            R2yOCdb-rn79az_xkhp8J0-D8KmKiRqeOLNFDyGmMTmcYAP2HOowYQsvIXbGaaNMgG4gEZLBXkspzkLqvm
            ZrH3nWzioBiqDqJnZQ-5DDIcJ-UbcY1FtRIZv1VYbX9Kqm1z0S7k5Q8dj3IzMRaYJ4l0_hIMgiye-vlNFg
            izWTT-9WVM2GTjZ_-gw
        """.trim().replaceAll("\\s", "");
    }

    private String jwtTokenForRobert() {
        return """
            eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJuNmpaamhHcmtpd2xnT0hmVDB1dEJ5cVQ4aXBS
            SG5Edzh3cVpDV1RDa2VZIn0.eyJleHAiOjIwMjE5NDMyOTAsImlhdCI6MTcwNjU4MzI5MCwianRpIjoiMjU3ZDVi
            M2YtNTRiNS00YjIyLWI2ODYtOWExN2VlNDg4ZDA3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDAwL3JlYWxt
            cy9tYWluIiwic3ViIjoiNmE0YmI1M2QtZGRkZi00MGQzLWFiYzQtNDljZDg0NjE4NjY3IiwidHlwIjoiQmVhcmVy
            IiwiYXpwIjoiY29udGFjdC1tYW5hZ2VyIiwic2Vzc2lvbl9zdGF0ZSI6IjU4NGE3NDQzLTUxMjctNGYzZC04ZDgy
            LTFkNDljNmVjMDA3MyIsInNjb3BlIjoiY29udGFjdC1tYW5hZ2VyIiwic2lkIjoiNTg0YTc0NDMtNTEyNy00ZjNk
            LThkODItMWQ0OWM2ZWMwMDczIiwidXNlcm5hbWUiOiJyb2JlcnQifQ.unY-8y94sSTUl5bTSVqu1_sjRThI6wR7t
            lZypH3WTFhlZ5gSjgC9DfyMEt8ZdT19ue_RPpZJQLSa3zt5u6KmV-g7yQJpeUhn_6blaHp8JLj7sLjDA4N5PtZwR
            rFtJnV7oliRb4cVW7j6DaH19SEPrQ6Xmyq_6e8OevzoNkCijiFPTR0nrSw9rWm81UiN7YeqdRGUOnmWgkSFQhIUP
            9NfEUgAvetZOJvMX4nrNZNKFgEvjZoleRLIORIf2-nBmXy2XJ8f68-a_Anb4k__SQFsroLQdrU7LnUR_qxTArPEf
            DOoykEHQK2w003DePt_JT3uhVRMZIFLAMuyZfTrQ1_JvQ
        """.trim().replaceAll("\\s", "");
    }
}
