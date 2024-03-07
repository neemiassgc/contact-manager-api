package com.spring.boot.controller;

import com.spring.boot.TestResources;
import com.spring.boot.services.ContactManagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ContactControllerIT {

    @Autowired
    private ContactManagerService contactManagerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/contacts -> 200 OK")
    public void should_respond_with_all_the_contacts_from_the_user_Joe_with_OK() throws Exception {
        shouldRespondWithAllTheContacts("joe", 5, 4, 3, "Greg from accounting", "Coworker Fred", "Sister Monica");
    }

    @Test
    @DisplayName("GET /api/contacts -> 200 OK")
    public void should_respond_with_all_the_contacts_from_the_user_Robert_with_OK() throws Exception {
        shouldRespondWithAllTheContacts("robert", 7, 7, 7, "Best friend Julia", "Mom", "Pizza and burgers", "Uncle Jeff");
    }

    private void shouldRespondWithAllTheContacts(
        final String username, int phoneNumberSize,
        int addressesSize, int emailsSize, String... names
    ) throws Exception {
        final String userToken = username.equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert();

        mockMvc.perform(get("/api/contacts/")
            .accept(MediaType.ALL)
            .header("Authorization", "Bearer "+userToken)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[*].name").value(containsInAnyOrder(names)))
        .andExpect(jsonPath("$[*].phoneNumbers.*").value(hasSize(phoneNumberSize)))
        .andExpect(jsonPath("$[*].addresses.*").value(hasSize(addressesSize)))
        .andExpect(jsonPath("$[*].emails.*").value(hasSize(emailsSize)));
    }

    @Test
    @DisplayName("GET /api/contacts/5c21433c-3c70-4253-a4b2-52b157be4167")
    public void should_respond_with_a_contact_for_the_user_John_successfully() throws Exception {
        shouldReturnAContact("joe", UUID.fromString("5c21433c-3c70-4253-a4b2-52b157be4167"), 1, 2, "Greg from accounting");
    }

    @Test
    @DisplayName("GET /api/contacts/5c21433c-3c70-4253-a4b2-52b157be4167")
    public void should_respond_with_a_contact_for_the_user_Robert_successfully() throws Exception {
        shouldReturnAContact("robert", UUID.fromString("b621650d-4a81-4016-a917-4a8a4992aaef"), 2, 2, "Uncle Jeff");
    }

    private void shouldReturnAContact(String user, UUID contactId, int phoneNumberSize, int addressesSize, String contactName) throws Exception {
        mockMvc.perform(get("/api/contacts/"+contactId)
            .header("Authorization", "Bearer "+(user.equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(contactName))
        .andExpect(jsonPath("$.phoneNumbers.*").value(hasSize(phoneNumberSize)))
        .andExpect(jsonPath("$.emails.*").value(hasSize(1)))
        .andExpect(jsonPath("$.addresses.*").value(hasSize(addressesSize)));
    }

    private void assertNotFound(String user, String httpMethod, UUID contactId, String errorMessage) throws Exception {
        final Map<String, Function<String, MockHttpServletRequestBuilder>> httpMethodPicker = new HashMap<>();
        httpMethodPicker.put("GET", MockMvcRequestBuilders::get);
        httpMethodPicker.put("DELETE", MockMvcRequestBuilders::delete);

        mockMvc.perform(httpMethodPicker.get(httpMethod).apply("/api/contacts/"+contactId)
                .header("Authorization", "Bearer "+(user.equals("joe") ? TestResources.jwtTokenForJoe() : TestResources.jwtTokenForRobert()))
                .accept(MediaType.ALL)
        )
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string(errorMessage));
    }
}
