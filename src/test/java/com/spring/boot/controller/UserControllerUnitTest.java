package com.spring.boot.controller;

import com.spring.boot.TestResources;
import com.spring.boot.controllers.GlobalErrorController;
import com.spring.boot.controllers.UserController;
import com.spring.boot.entities.User;
import com.spring.boot.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.spring.boot.TestResources.once;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {UserController.class, GlobalErrorController.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users -> 201 CREATED")
    public void should_create_a_new_user_successfully() throws Exception {
        doNothing().when(userService).create(any(User.class));

        final String jsonBody = "{\"username\": \"julia\", \"avatarUri\": \"https://example.com/my-avatar.png\"}";
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
            .header("Authorization", "Bearer "+ TestResources.jwtTokenWithAdminAuthority())
        )
        .andExpect(status().isCreated());

        verify(userService, once()).create(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users -> 400 BAD REQUEST")
    public void should_respond_400_with_feedback_when_sending_an_invalid_json() throws Exception {
        final String jsonBody = "{\"avatarUri\": \"ftp://example.com/my-avatar.png\"}";
        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
            .header("Authorization", "Bearer "+ TestResources.jwtTokenWithAdminAuthority())
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.fieldViolations[*]",
            containsInAnyOrder("'avatarUri' must be a valid Url", "'username' must not be null"))
        );

        verifyNoInteractions(userService);
    }
}
