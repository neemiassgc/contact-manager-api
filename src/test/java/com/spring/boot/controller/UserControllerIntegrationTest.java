package com.spring.boot.controller;

import com.spring.boot.TestResources;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
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

    @Test
    @DisplayName("POST /api/users -> 403 FORBIDDEN")
    public void access_should_be_denied_when_the_user_does_not_own_admin_role() throws Exception {
        final String jsonBody = "{\"username\": \"julia\", \"avatarUri\": \"https://example.com/my-avatar.png\"}";
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
            .header("Authorization", "Bearer "+TestResources.jwtTokenForRobert())
        )
        .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/users -> 403 FORBIDDEN")
    public void access_should_be_denied_when_there_is_no_authentication() throws Exception {
        final String jsonBody = "{\"username\": \"julia\", \"avatarUri\": \"https://example.com/my-avatar.png\"}";
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
        )
        .andExpect(status().isForbidden());
    }
}