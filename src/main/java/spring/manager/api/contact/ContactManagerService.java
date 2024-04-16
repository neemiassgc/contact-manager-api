package spring.manager.api.contact;

import java.util.UUID;

public interface ContactManagerService extends ExtendedContactRepository {

    Contact findById(UUID uuid);

    Contact findByIdWithUser(UUID uuid, String username);

    void saveWithUser(Contact contact, String username);

    void updateWithUser(Contact contact, String username);

    void deleteByIdWithUser(UUID id, String usename);
}