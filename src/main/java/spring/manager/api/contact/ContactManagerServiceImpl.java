package spring.manager.api.contact;

import spring.manager.api.user.User;
import spring.manager.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ContactManagerServiceImpl implements ContactManagerService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserService userService;

    @Override
    public List<Contact> findAllByUserId(String userId) {
        return contactRepository.findAllByUserId(userId);
    }

    @Override
    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    @Override
    public Contact findByIdWithUser(UUID contactId, String userId) {
        final Contact contact = findById(contactId);
        final User user = contact.getUser();
        if (!user.getId().equals(userId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contact belongs to another user");
        return contact;
    }

    @Override
    public void saveWithUser(final Contact contact, final String userId) {
        final User user = userService.findById(userId);
        contact.setUser(user);
        save(contact);
    }

    @Override
    public void updateWithUser(Contact freshContact, String userId) {
        final Contact contactFromStorage = findByIdWithUser(freshContact.getId(), userId);
        contactFromStorage.merge(freshContact);
    }

    @Override
    public Contact findById(final UUID id) {
        return contactRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
    }

    @Override
    public void deleteByIdWithUser(UUID contactId, String userId) {
        findByIdWithUser(contactId, userId);
        contactRepository.deleteById(contactId);
    }

    @Override
    public void save(Contact contact) {
        contactRepository.save(contact);
    }

    @Override
    public void deleteAll() {
        contactRepository.deleteAll();
    }
}
