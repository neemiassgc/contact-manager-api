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
}
