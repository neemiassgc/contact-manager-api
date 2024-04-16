package spring.manager.api.contact;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ContactController {

    private final ContactManagerService contactManagerService;

    @GetMapping()
    public List<ContactSummary> getAllContacts(@AuthenticationPrincipal Jwt jwt) {
        return Contact.toListOfContactSummary(contactManagerService.findAllByUsername(getCurrentUser(jwt)));
    }

    @GetMapping("/{id}")
    public ContactSummary getById(@PathVariable("id") UUID id, @AuthenticationPrincipal Jwt jwt) {
        return contactManagerService.findByIdWithUser(id, getCurrentUser(jwt)).toContactSummary();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Validated ContactSummary contactSummary, @AuthenticationPrincipal Jwt jwt) {
        contactManagerService.saveWithUser(Contact.toContact(contactSummary), getCurrentUser(jwt));
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") UUID id, @RequestBody ContactSummary contactSummary, @AuthenticationPrincipal Jwt jwt) {
        contactManagerService.updateWithUser(Contact.toContact(contactSummary, id), getCurrentUser(jwt));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id, @AuthenticationPrincipal Jwt jwt) {
        contactManagerService.deleteByIdWithUser(id, getCurrentUser(jwt));
    }

    private String getCurrentUser(final Jwt jwt) {
        return jwt.getClaimAsString("username");
    }
}