package contact.manager.api.global;

import contact.manager.api.contact.Contact;
import contact.manager.api.contact.ContactManagerService;
import contact.manager.api.misc.TestResources;
import contact.manager.api.user.User;
import contact.manager.api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        verify(userRepository, TestResources.once()).findByUsername(eq("me@gmail.com"));
        verify(contactManagerService, TestResources.once()).findAllByUserId(eq("id"));
        verify(contactManagerService, TestResources.once()).saveWithUser(any(Contact.class), eq("id"));
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

        verify(userRepository, TestResources.once()).findByUsername(eq("me@gmail.com"));
        verify(contactManagerService, TestResources.once()).findAllByUserId(eq("id"));
        verify(contactManagerService, TestResources.once()).deleteByIdWithUser(eq(contactId), eq("id"));
        verifyNoMoreInteractions(contactManagerService);
        verifyNoMoreInteractions(userRepository);
    }
}
