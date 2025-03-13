package contact.manager.api.contact;

import java.util.UUID;

public interface ContactManagerService extends ExtendedContactRepository {

    Contact findById(UUID id);

    Contact findByIdWithUser(UUID contactId, String userId);

    void saveWithUser(Contact contact, String userId);

    void updateWithUser(Contact contact, String userId);

    void deleteByIdWithUser(UUID contactId, String userId);
}