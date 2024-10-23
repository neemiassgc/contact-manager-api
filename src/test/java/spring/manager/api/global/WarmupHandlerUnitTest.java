package spring.manager.api.global;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import spring.manager.api.contact.Contact;
import spring.manager.api.contact.ContactManagerService;
import spring.manager.api.user.User;
import spring.manager.api.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.manager.api.misc.TestResources.once;

@WebMvcTest(value = {WarmupHandler.class, GlobalErrorController.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class WarmupHandlerUnitTest {

    @MockBean
    private ContactManagerService contactManagerService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_insert_an_entry_to_the_database() throws Exception {
        when(userRepository.findByUsername(eq("me@gmail.com"))).thenReturn(Optional.of(new User("id", "me")));
        when(contactManagerService.findAllByUserId(eq("id"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/warmup-db")
            .with(SecurityMockMvcRequestPostProcessors.opaqueToken())
            .header("X-Appengine-Cron", "True")
            .accept(MediaType.ALL)
        )
        .andExpect(status().isOk());

        verify(userRepository, once()).findByUsername(eq("me@gmail.com"));
        verify(contactManagerService, once()).findAllByUserId(eq("id"));
        verify(contactManagerService, once()).saveWithUser(any(Contact.class), eq("id"));
        verifyNoMoreInteractions(contactManagerService);
    }

    @Test
    public void should_delete_an_entry_from_the_database() throws Exception {
        final UUID contactId = UUID.fromString("4955eecf-60fe-4f5b-97c7-d54027ae78a3");
        when(userRepository.findByUsername(eq("me@gmail.com"))).thenReturn(Optional.of(new User("id", "me")));
        when(contactManagerService.findAllByUserId(eq("id")))
            .thenReturn(List.of(new Contact("Roger", contactId)));
        doNothing().when(contactManagerService).deleteByIdWithUser(eq(contactId), eq("id"));

        mockMvc.perform(get("/warmup-db")
            .with(SecurityMockMvcRequestPostProcessors.opaqueToken())
            .header("X-Appengine-Cron", "True")
            .accept(MediaType.ALL)
        )
            .andExpect(status().isOk());

        verify(userRepository, once()).findByUsername(eq("me@gmail.com"));
        verify(contactManagerService, once()).findAllByUserId(eq("id"));
        verify(contactManagerService, once()).deleteByIdWithUser(eq(contactId), eq("id"));
        verifyNoMoreInteractions(contactManagerService);
        verifyNoMoreInteractions(userRepository);
    }
}
