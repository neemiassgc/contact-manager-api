package spring.manager.api.global;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import spring.manager.api.contact.Contact;
import spring.manager.api.contact.ContactManagerService;
import spring.manager.api.user.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class WarmupHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactManagerService contactManagerService;

    @Autowired
    private UserService userService;

    @Test
    public void should_insert_an_entry_to_the_database() throws Exception {
        mockMvc.perform(get("/warmup-db")
            .with(SecurityMockMvcRequestPostProcessors.opaqueToken())
            .header("X-Appengine-Cron", "True")
            .accept(MediaType.ALL)
        )
        .andExpect(status().isOk());

        final String userId = userService.findByUsername("me@gmail.com").getId();
        final List<Contact> listOfContacts = contactManagerService.findAllByUserId(userId);

        assertThat(listOfContacts).extracting(Contact::getName).contains("Roger");
    }

    @Test
    public void should_delete_an_entry_from_the_database() throws Exception {
        mockMvc.perform(get("/warmup-db")
            .with(SecurityMockMvcRequestPostProcessors.opaqueToken())
            .header("X-Appengine-Cron", "True")
            .accept(MediaType.ALL)
        )
        .andExpect(status().isOk());

        final String userId = userService.findByUsername("me@gmail.com").getId();
        final List<Contact> listOfContacts = contactManagerService.findAllByUserId(userId);

        assertThat(listOfContacts).extracting(Contact::getName).doesNotContain("Roger");
    }
}
