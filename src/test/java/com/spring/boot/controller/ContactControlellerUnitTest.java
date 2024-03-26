package com.spring.boot.controller;

import com.spring.boot.TestResources;
import com.spring.boot.controller.types.AssertHttpErrorType;
import com.spring.boot.controller.types.ShouldRespondWithAllTheContactsType;
import com.spring.boot.controller.types.ShouldReturnAContactType;
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
import org.springframework.http.HttpMethod;
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
        shouldRespondWithAllTheContacts(
            ShouldRespondWithAllTheContactsType.builder()
                .username("joe")
                .phoneNumberSize(5)
                .addressesSize(4)
                .emailsSize(3)
                .names(new String[]{"Greg from accounting", "Coworker Fred", "Sister Monica"})
                .build()
        );
    }

    @Test
    @DisplayName("GET /api/contacts -> OK 200")
    void should_return_all_the_contacts_for_Robert_with_OK_200() throws Exception {
        shouldRespondWithAllTheContacts(
            ShouldRespondWithAllTheContactsType.builder()
                .username("robert")
                .phoneNumberSize(7)
                .addressesSize(7)
                .emailsSize(7)
                .names(new String[]{"Best friend Julia", "Mom", "Pizza and burgers", "Uncle Jeff"})
                .build()
        );
    }

    private void shouldRespondWithAllTheContacts(ShouldRespondWithAllTheContactsType input) throws Exception {
        when(contactManagerService.findAllByUsername(eq(input.getUsername())))
            .thenReturn(input.getUsername().equals("joe") ? TestResources.getContactsForJoe() : TestResources.getContactsForRobert());

        mockMvc.perform(get("/api/contacts")
            .header("Authorization", "Bearer "+(input.getUsername().equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[*].name").value(containsInAnyOrder(input.getNames())))
        .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(input.getPhoneNumberSize())))
        .andExpect(jsonPath("$[*].addresses.*").value(hasSize(input.getAddressesSize())))
        .andExpect(jsonPath("$[*].emails.*").value(hasSize(input.getEmailsSize())));

        verify(contactManagerService, times(1)).findAllByUsername(eq(input.getUsername()));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("GET /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> OK 200")
    void should_return_a_contact_for_the_user_Joe_with_OK_200() throws Exception {
        shouldReturnAContact(
            ShouldReturnAContactType.builder()
                .user("joe")
                .phoneNumberSize(3)
                .addressesSize(1)
                .contactId(UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408"))
                .contactName("Coworker Fred")
                .build()
        );
    }

    @Test
    @DisplayName("GET /api/contacts/84edd1b9-89a5-4107-a84d-435676c2b8f5 -> 200 OK")
    void should_return_a_contact_for_the_user_Robert_with_OK_200() throws Exception {
        shouldReturnAContact(
            ShouldReturnAContactType.builder()
                .user("robert")
                .phoneNumberSize(1)
                .addressesSize(1)
                .contactId(UUID.fromString("84edd1b9-89a5-4107-a84d-435676c2b8f5"))
                .contactName("Mom")
                .build()
        );
    }

    private void shouldReturnAContact(ShouldReturnAContactType input) throws Exception {
        when(contactManagerService.findByIdWithUser(eq(input.getContactId()), eq(input.getUser())))
                .thenReturn(TestResources.getContactById(input.getContactId()));

        mockMvc.perform(get("/api/contacts/"+input.getContactId())
            .header("Authorization", "Bearer "+(input.getUser().equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(input.getContactName()))
        .andExpect(jsonPath("$.phoneNumbers.*").value(hasSize(input.getPhoneNumberSize())))
        .andExpect(jsonPath("$.emails.*").value(hasSize(1)))
        .andExpect(jsonPath("$.addresses.*").value(hasSize(1)));

        verify(contactManagerService, times(1)).findByIdWithUser(eq(input.getContactId()), eq(input.getUser()));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("GET /api/contacts/c97775aa-b7f3-49c0-a586-d0466ba592bf -> 404 NOT FOUND")
    void should_respond_404_when_requesting_for_a_contact_that_does_not_exist() throws Exception {
        final UUID contactId = UUID.fromString("c97775aa-b7f3-49c0-a586-d0466ba592bf");
        when(contactManagerService.findByIdWithUser(eq(contactId), eq("joe")))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        assertHttpError(
            AssertHttpErrorType.builder()
                .user("joe")
                .httpStatus(HttpStatus.NOT_FOUND)
                .contactId(contactId)
                .httpMethod(HttpMethod.GET)
                .errorMessage("Contact not found")
                .build()
        );
    }

    @Test
    @DisplayName("GET /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> 404 NOT FOUND")
    void should_respond_404_NOT_FOUND_when_requesting_for_a_contact_that_does_not_belong_to_the_current_user() throws Exception {
        final UUID contactId = UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408");
        final String errorMessage = "Contact does not belong to the user: robert";
        when(contactManagerService.findByIdWithUser(eq(contactId), eq("robert")))
            .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage));

        assertHttpError(
            AssertHttpErrorType.builder()
                .user("robert")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .contactId(contactId)
                .httpMethod(HttpMethod.GET)
                .errorMessage(errorMessage)
                .build()
        );
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
            .header("Authorization", "Bearer "+(user.equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
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
            .header("Authorization", "Bearer "+TestResources.jwtTokenForRobert())
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
        shouldUpdateCustomFieldsOfAContact("robert");
    }

    @Test
    @DisplayName("PUT /api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef -> OK 200")
    void should_update_custom_fields_of_a_contact_for_the_Joe_successfully() throws Exception {
        shouldUpdateCustomFieldsOfAContact("joe");
    }

    private void shouldUpdateCustomFieldsOfAContact(String user) throws Exception {
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
            .header("Authorization", "Bearer "+(user.equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
        .andExpect(status().isOk());

        verify(contactManagerService, times(1)).updateWithUser(any(Contact.class), eq(user));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("PUT /api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef -> 400 BAD_REQUEST")
    void should_respond_400_when_trying_to_update_a_contact_whose_user_is_not_the_owner() throws Exception {
        final UUID contactId = UUID.fromString("b621650d-4a81-4016-a917-4a8a4992aaef");
        final String errorMessage = "Contact does not belong to the user: joe";
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage))
            .when(contactManagerService).updateWithUser(any(Contact.class), eq("joe"));

        assertHttpError(AssertHttpErrorType.builder()
            .contactId(contactId)
            .user("joe")
            .httpStatus(HttpStatus.BAD_REQUEST)
            .httpMethod(HttpMethod.PUT)
            .errorMessage(errorMessage)
            .build()
        );
    }

    @Test
    @DisplayName("PUT /api/contacts/b621650d-4a81-4016-a917-4a8a4992aaef -> 404 NOT FOUND")
    void should_respond_404_when_trying_to_update_a_contact_that_does_not_exist() throws Exception {
        final UUID contactId = UUID.fromString("b621650d-4a81-4016-a917-4a8a4992aaef");
        final String errorMessage = "Contact not found";
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage))
            .when(contactManagerService).updateWithUser(any(Contact.class), eq("joe"));

        assertHttpError(AssertHttpErrorType.builder()
            .contactId(contactId)
            .user("joe")
            .httpStatus(HttpStatus.NOT_FOUND)
            .httpMethod(HttpMethod.PUT)
            .errorMessage(errorMessage)
            .build()
        );
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
            .header("Authorization", "Bearer "+(user.equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
            .accept(MediaType.ALL)
        )
        .andExpect(status().isOk());

        verify(contactManagerService, times(1)).deleteByIdWithUser(eq(contactId), eq(user));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    @DisplayName("DELETE /api/contacts/8fb2bd75-9aec-4cc5-b77b-a95f06081388 -> 400 BAD_REQUEST")
    public void should_respond_400_when_deleting_a_concat_whose_user_does_not_own_it() throws Exception {
        final UUID contactId = UUID.fromString("8fb2bd75-9aec-4cc5-b77b-a95f06081388");

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contact does not belong to the user: joe"))
                .when(contactManagerService).deleteByIdWithUser(eq(contactId), eq("joe"));

        assertHttpError(
            AssertHttpErrorType.builder()
                .contactId(contactId)
                .httpMethod(HttpMethod.DELETE)
                .errorMessage("Contact does not belong to the user: joe")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .user("joe")
                .build()
        );

        verify(contactManagerService, times(1)).deleteByIdWithUser(eq(contactId), eq("joe"));
        verifyNoMoreInteractions(contactManagerService);
    }


    @Test
    @DisplayName("DELETE /api/contacts/35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7 -> 404 NOT_FOUND")
    void should_respond_404_when_trying_to_delete_a_contact_that_does_not_exist() throws Exception {
        final UUID contactId = UUID.fromString("35b175ba-0a27-43e9-bc3f-cf23e1ca2ea7");
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"))
            .when(contactManagerService).deleteByIdWithUser(eq(contactId), eq("joe"));

        assertHttpError(
            AssertHttpErrorType.builder()
                .contactId(contactId)
                .httpMethod(HttpMethod.DELETE)
                .errorMessage("Contact not found")
                .httpStatus(HttpStatus.NOT_FOUND)
                .user("joe")
                .build()
        );
    }

    private void assertHttpError(AssertHttpErrorType input) throws Exception {
        final Map<HttpMethod, Function<String, MockHttpServletRequestBuilder>> httpMethodPicker = new HashMap<>();
        httpMethodPicker.put(HttpMethod.GET, MockMvcRequestBuilders::get);
        httpMethodPicker.put(HttpMethod.DELETE, MockMvcRequestBuilders::delete);
        httpMethodPicker.put(HttpMethod.PUT, MockMvcRequestBuilders::put);

        final MockHttpServletRequestBuilder mockHttp =
            httpMethodPicker.get(input.getHttpMethod()).apply("/api/contacts/"+input.getContactId())
                .header("Authorization", "Bearer "+(input.getUser().equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
                .accept(MediaType.ALL);

        if (input.getHttpMethod().matches("PUT"))
            mockHttp.content("{\"name\": \"Billy\"}").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockHttp)
            .andExpect(status().is(input.getHttpStatus().value()))
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string(input.getErrorMessage()));

        if (input.getHttpMethod().matches("DELETE"))
            verify(contactManagerService, times(1)).deleteByIdWithUser(eq(input.getContactId()), eq(input.getUser()));
        if (input.getHttpMethod().matches("GET"))
            verify(contactManagerService, times(1)).findByIdWithUser(eq(input.getContactId()), eq(input.getUser()));
        if (input.getHttpMethod().matches("PUT"))
            verify(contactManagerService, times(1)).updateWithUser(any(Contact.class), eq(input.getUser()));

        verifyNoMoreInteractions(contactManagerService);
    }
}
