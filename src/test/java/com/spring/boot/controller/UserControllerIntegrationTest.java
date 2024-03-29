package com.spring.boot.controller;

import com.spring.boot.TestResources;
import com.spring.boot.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static com.spring.boot.TestResources.once;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/users -> 201 CREATED")
    public void should_create_a_new_user_successfully() throws Exception {
        final String jsonBody = "{\"username\": \"julia\", \"avatarUri\": \"https://example.com/my-avatar.png\"}";
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
            .header("Authorization", "Bearer "+ TestResources.jwtTokenWithAdminAuthority())
        )
        .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/users -> 400 BAD REQUEST")
    public void should_respond_400_with_feedback_when_sending_an_invalid_json() throws Exception {
        final String jsonBody = "{\"avatarUri\": \"ftp://example.com/my-avatar.png\"}";
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
            .header("Authorization", "Bearer "+TestResources.jwtTokenWithAdminAuthority())
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.fieldViolations[*]",
            containsInAnyOrder("'avatarUri' must be a valid Url", "'username' must not be null"))
        );
    }

    @Test
    @DisplayName("POST /api/users -> 400 BAD REQUEST")
    public void should_respond_400_when_trying_to_create_a_user_that_already_exists() throws Exception {
        final String jsonBody = "{\"username\": \"joe\", \"avatarUri\": \"https://example.com/my-avatar.png\"}";
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
            .header("Authorization", "Bearer "+TestResources.jwtTokenWithAdminAuthority())
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string("User already exists"));
    }
}
