package spring.manager.api.contact;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;

interface ContactControllerDoc {

    List<ContactInOut> getAllContact(Jwt jwt);
    
    ContactInOut getById(UUID id, Jwt jwt);

    void create(ContactInOut contactInOut, Jwt jwt);

    ContactInOut update(UUID id, ConstrainedContact constrainedContact, Jwt jwt);

    void delete(UUID id, Jwt jwt);
}