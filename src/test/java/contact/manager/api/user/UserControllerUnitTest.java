package contact.manager.api.user;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import contact.manager.api.global.GlobalErrorController;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static contact.manager.api.misc.TestResources.*;

@WebMvcTest(value = {UserController.class, GlobalErrorController.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final Jwt DEFAULT_JWT = Users.JOE.jwt();

    @Nested
    public class Create {

        @Test
        @DisplayName("Should create a new user successfully")
        public void shouldCreateANewUserSuccessfully() throws Exception {
            String newUserJson = "{\"username\": \"julia\"}";
            doNothing().when(userService).create(any(User.class));

            mockMvc.perform(
                post("/api/users").with(
                    SecurityMockMvcRequestPostProcessors.jwt().jwt(Users.JULIA.jwt())
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(newUserJson)
            )
            .andExpect(status().isCreated());

            verify(userService, once()).create(any(User.class));
        }


        @Test
        @DisplayName("When a user already exists then should respond 400")
        public void whenAUserAlreadyExists_thenShouldRespond400() throws Exception {
            doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists"))
                .when(userService).create(any(User.class));

            final String jsonBody = "{\"username\": \"joe\"}";
            mockMvc.perform(
                post("/api/users").with(
                    SecurityMockMvcRequestPostProcessors.jwt().jwt(DEFAULT_JWT)
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(jsonBody)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN))
            .andExpect(content().string("User already exists"));

            verify(userService, once()).create(any(User.class));
        }

        @Test
        @DisplayName("When there is no body in request body then should respond 400")
        public void whenThereIsNoBodyInRequest_thenShouldRespond400() throws Exception {
            final String jsonBodyWithoutUsername = "";
            mockMvc.perform(
                post("/api/users").with(
                    SecurityMockMvcRequestPostProcessors.jwt().jwt(DEFAULT_JWT)
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(jsonBodyWithoutUsername)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string(Matchers.containsString("Required request body is missing")));

            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("When provided a malformed then should respond 400")
        public void whenProvidedAMalformedJson_thenShouldRespond400() throws Exception {
            final String malformedJson = "{\"username\":}";
            mockMvc.perform(
                post("/api/users").with(
                    SecurityMockMvcRequestPostProcessors.jwt().jwt(DEFAULT_JWT)
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(malformedJson)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string(Matchers.containsString("JSON parse error")));

            verifyNoInteractions(userService);
        }

        @Test
        @DisplayName("When provided an invalid username then should respond 400 along with feedback")
        public void whenProvidedAnInvalidUsername_thenShouldRespond400AlongWithFeedback() throws Exception {
            final String invalidUsername = "{\"username\":\"@-094kja+=el!a'd;url*\"}";
            mockMvc.perform(
                post("/api/users").with(
                    SecurityMockMvcRequestPostProcessors.jwt().jwt(DEFAULT_JWT)
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .content(invalidUsername)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fieldViolations.value[*]", hasSize(2)))
            .andExpect(jsonPath("$.fieldViolations.value[*]", containsInAnyOrder(
                "username needs to be between 3 and 20 characters long",
                "username must not have special characters"
            )));

            verifyNoInteractions(userService);
        }
    }
}
