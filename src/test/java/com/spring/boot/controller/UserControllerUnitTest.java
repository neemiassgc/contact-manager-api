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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
