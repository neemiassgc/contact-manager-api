package spring.manager.api.global;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import spring.manager.api.contact.Address;
import spring.manager.api.contact.Contact;
import spring.manager.api.contact.ContactManagerService;
import spring.manager.api.user.UserRepository;

import java.util.List;

@CommonsLog
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WarmupHandler {

    private final ContactManagerService contactManagerService;
    private final UserRepository userRepository;

    @CrossOrigin("*")
    @GetMapping("/warmup")
    @ResponseStatus(HttpStatus.OK)
    public String warmup() {
        return "ok";
    }

    @CrossOrigin
    @GetMapping("/warmup-db")
    public void warmupDatabase() {
        log.info("Initializing interaction with the database...");
        userRepository.findByUsername("me@gmail.com")
            .ifPresent(user -> {
                log.info("User "+user.getUsername()+" has been found!");

                log.info("Finding contacts by the user id: "+user.getId());
                final List<Contact> contactList = contactManagerService.findAllByUserId(user.getId());

                for (Contact contact : contactList) {
                    if (contact.getName().equals("Roger")) {
                        log.info("Contact with the name Roger has been found!");
                        log.info("Deleting the contact...");
                        contactManagerService.deleteByIdWithUser(contact.getId(), user.getId());
                        return;
                    }
                }

                log.info("Contact with the name Roger has not been found!");
                final Contact contact = new Contact("Roger");
                contact.putPhoneNumber("phone", "+12937864324");
                contact.putEmail("main email", "tertiary@hotmail.com");
                final Address address = Address.builder()
                    .street("385 Renwick Drive")
                    .city("Dubberly")
                    .state("Louisiana")
                    .country("US")
                    .zipcode("71024").build();
                contact.putAddress("home", address);

                log.info("Inserting a new contact...");
                contactManagerService.saveWithUser(contact, user.getId());
            });
    }
}