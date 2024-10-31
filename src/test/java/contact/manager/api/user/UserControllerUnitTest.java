package contact.manager.api.user;

import contact.manager.api.misc.TestResources;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import contact.manager.api.global.GlobalErrorController;

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

        final String jsonBody = "{\"username\": \"julia\"}";
        mockMvc.perform(
            post("/api/users").with(
                SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJulia())
            )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
        )
        .andExpect(status().isCreated());

        verify(userService, TestResources.once()).create(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users -> 400 BAD REQUEST")
    public void should_respond_400_with_feedback_when_sending_an_invalid_json() throws Exception {
        final String jsonBody = "{}";
        mockMvc.perform(
            post("/api/users").with(
                SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForRobert())
            )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.fieldViolations[*][*]",
            Matchers.contains("username is required"))
        );

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("POST /api/users -> 400 BAD REQUEST")
    public void should_respond_400_when_trying_to_create_a_user_that_already_exists() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists"))
            .when(userService).create(any(User.class));

        final String jsonBody = "{\"username\": \"joe\"}";
        mockMvc.perform(
            post("/api/users").with(
                SecurityMockMvcRequestPostProcessors.jwt().jwt(TestResources.jwtForJoe())
            )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string("User already exists"));

        verify(userService, TestResources.once()).create(any(User.class));
        verifyNoMoreInteractions(userService);
    }
}
