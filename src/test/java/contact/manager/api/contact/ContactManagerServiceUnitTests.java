package contact.manager.api.contact;

import contact.manager.api.user.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ContactManagerServiceUnitTests {

    @MockBean
    private ContactManagerService contactManagerService;

    @MockBean
    private UserService userService;
}