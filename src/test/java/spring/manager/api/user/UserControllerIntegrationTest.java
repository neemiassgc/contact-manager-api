package spring.manager.api.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static spring.manager.api.misc.TestResources.jwtForJoe;
import static spring.manager.api.misc.TestResources.jwtForJulia;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/users -> 201 CREATED")
    public void should_create_a_new_user_successfully() throws Exception {
        final String jsonBody = "{\"username\": \"julia\"}";
        mockMvc.perform(
            post("/api/users").with(
                SecurityMockMvcRequestPostProcessors.jwt().jwt(jwtForJulia())
            )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
        )
        .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/users -> 400 BAD REQUEST")
    public void should_respond_400_with_feedback_when_sending_an_invalid_json() throws Exception {
        final String jsonBody = "{}";
        mockMvc.perform(
            post("/api/users").with(
                SecurityMockMvcRequestPostProcessors.jwt().jwt(jwtForJulia())
            )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.fieldViolations[*]",
            containsInAnyOrder("username is required"))
        );
    }

    @Test
    @DisplayName("POST /api/users -> 400 BAD REQUEST")
    public void should_respond_400_when_trying_to_create_a_user_that_already_exists() throws Exception {
        final String jsonBody = "{\"username\": \"joe\"}";
        mockMvc.perform(
            post("/api/users").with(
                SecurityMockMvcRequestPostProcessors.jwt().jwt(jwtForJoe())
            )
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.ALL)
            .content(jsonBody)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN))
        .andExpect(content().string("User already exists"));
    }
}