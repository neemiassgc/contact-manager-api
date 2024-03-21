package com.spring.boot.controller;

import com.spring.boot.TestResources;
import com.spring.boot.controller.types.AssertHttpErrorType;
import com.spring.boot.controller.types.ShouldRespondWithAllTheContactsType;
import com.spring.boot.controller.types.ShouldReturnAContactType;
import com.spring.boot.services.ContactManagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
public class ContactControllerIT {

    @Autowired
    private ContactManagerService contactManagerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/contacts -> 200 OK")
    public void should_respond_with_all_the_contacts_from_the_user_Joe_with_OK() throws Exception {
        shouldRespondWithAllTheContacts(
            ShouldRespondWithAllTheContactsType.builder()
                .username("joe")
                .phoneNumberSize(5)
                .emailsSize(3)
                .addressesSize(4)
                .names(new String[]{"Greg from accounting", "Coworker Fred", "Sister Monica"})
                .build()
        );
    }

    @Test
    @DisplayName("GET /api/contacts -> 200 OK")
    public void should_respond_with_all_the_contacts_from_the_user_Robert_with_OK() throws Exception {
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
        final String userToken = input.getUsername().equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert();

        mockMvc.perform(get("/api/contacts")
            .accept(MediaType.ALL)
            .header("Authorization", "Bearer "+userToken)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[*].name").value(containsInAnyOrder(input.getNames())))
        .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(input.getPhoneNumberSize())))
        .andExpect(jsonPath("$[*].addresses.*").value(hasSize(input.getAddressesSize())))
        .andExpect(jsonPath("$[*].emails.*").value(hasSize(input.getEmailsSize())));
    }

    @Test
    @DisplayName("GET /api/contacts/5c21433c-3c70-4253-a4b2-52b157be4167")
    public void should_respond_with_a_contact_for_the_user_John_successfully() throws Exception {
        shouldReturnAContact(
            ShouldReturnAContactType.builder()
                .user("joe")
                .contactId(UUID.fromString("5c21433c-3c70-4253-a4b2-52b157be4167"))
                .phoneNumberSize(1)
                .addressesSize(2)
                .contactName("Greg from accounting")
                .build()
        );
    }

    @Test
    @DisplayName("GET /api/contacts/5c21433c-3c70-4253-a4b2-52b157be4167")
    public void should_respond_with_a_contact_for_the_user_Robert_successfully() throws Exception {
        shouldReturnAContact(
            ShouldReturnAContactType.builder()
                .user("robert")
                .contactId(UUID.fromString("b621650d-4a81-4016-a917-4a8a4992aaef"))
                .phoneNumberSize(2)
                .addressesSize(2)
                .contactName("Uncle Jeff")
                .build()
        );
    }

    private void shouldReturnAContact(ShouldReturnAContactType input) throws Exception {
        mockMvc.perform(get("/api/contacts/"+input.getContactId())
            .header("Authorization", "Bearer "+(input.getUser().equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(input.getContactName()))
        .andExpect(jsonPath("$.phoneNumbers.*").value(hasSize(input.getPhoneNumberSize())))
        .andExpect(jsonPath("$.emails.*").value(hasSize(1)))
        .andExpect(jsonPath("$.addresses.*").value(hasSize(input.getAddressesSize())));
    }

    @Test
    @DisplayName("GET /api/contacts/c97775aa-b7f3-49c0-a586-d0466ba592bf -> 404 NOT FOUND")
    void should_respond_404_NOT_FOUND_when_requesting_for_a_contact_that_does_not_exist() throws Exception {
        final UUID contactId = UUID.fromString("c97775aa-b7f3-49c0-a586-d0466ba592bf");

        assertHttpError(
            AssertHttpErrorType.builder()
                .contactId(contactId)
                .user("robert")
                .errorMessage("Contact not found")
                .httpMethod(HttpMethod.GET)
                .httpStatus(HttpStatus.NOT_FOUND)
                .build()
        );
    }

    @Test
    @DisplayName("GET /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> 404 NOT FOUND")
    void should_respond_404_NOT_FOUND_when_requesting_for_a_contact_that_does_not_belong_to_the_current_user() throws Exception {
        final UUID contactId = UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408");

        assertHttpError(
            AssertHttpErrorType.builder()
                .contactId(contactId)
                .user("robert")
                .errorMessage("Contact does not belong to the user: robert")
                .httpMethod(HttpMethod.GET)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build()
        );
    }

    @Test
    @DisplayName("POST /api/contacts -> 201 CREATED")
    void should_create_a_contact_for_the_user_Joe_successfully() throws Exception {
        shouldCreateAContact("joe");
    }

    @Test
    @DisplayName("POST /api/contacts -> 201 CREATED")
    void should_create_a_contact_for_the_user_Robert_successfully() throws Exception {
        shouldCreateAContact("robert");
    }

    private void shouldCreateAContact(String user) throws Exception {
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
    }

    @Test
    @DisplayName("PUT /api/contacts/4fe25947-ecab-489c-a881-e0057124e408 -> 200 OK")
    void should_update_some_fields_of_a_contacts_for_the_user_Joe() throws Exception {
        shouldUpdateSomeFieldsOfAContact(UUID.fromString("4fe25947-ecab-489c-a881-e0057124e408"), "joe");
    }

    private void shouldUpdateSomeFieldsOfAContact(UUID contactId, String user) throws Exception {
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

        mockMvc.perform(put("/api/contacts/"+contactId)
            .header("Authorization", "Bearer "+(user.equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
        )
        .andExpect(status().isOk());
    }

    private void assertHttpError(AssertHttpErrorType input) throws Exception {
        final Map<HttpMethod, Function<String, MockHttpServletRequestBuilder>> httpMethodPicker = new HashMap<>();
        httpMethodPicker.put(HttpMethod.GET, MockMvcRequestBuilders::get);
        httpMethodPicker.put(HttpMethod.DELETE, MockMvcRequestBuilders::delete);
        httpMethodPicker.put(HttpMethod.PUT, MockMvcRequestBuilders::delete);

        mockMvc.perform(httpMethodPicker.get(input.getHttpMethod()).apply("/api/contacts/"+input.getContactId())
            .header("Authorization", "Bearer "+(input.getUser().equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
            .accept(MediaType.ALL)
        )
        .andExpect(status().is(input.getHttpStatus().value()))
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string(input.getErrorMessage()));
    }
}
