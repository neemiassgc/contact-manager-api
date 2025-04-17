package contact.manager.api.contact;

import contact.manager.api.misc.TestResources;
import contact.manager.api.user.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static contact.manager.api.misc.TestResources.once;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
public class ContactManagerServiceUnitTests {

    @MockBean
    private ContactRepository contactRepository;

    @MockBean
    private UserService userService;

    @Nested
    public class FindAll {

        @Test
        void shouldReturnAllOfTheContactsAvailable() {
            when(contactRepository.findAll()).thenReturn(TestResources.getAFewContacts(10));

            List<Contact> actualContacts = contactRepository.findAll();

            assertThat(actualContacts).hasSize(10);

            verify(contactRepository, once()).findAll();
            verifyNoMoreInteractions(contactRepository);
        }
    }

    @Nested
    public class FindAllByUserId {

    }
}