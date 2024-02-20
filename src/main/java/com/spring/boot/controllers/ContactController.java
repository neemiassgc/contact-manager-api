package com.spring.boot.controllers;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.projections.ContactSummary;
import com.spring.boot.services.ContactManagerService;
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
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ContactController {

    private final ContactManagerService contactManagerService;

    @GetMapping("/contacts")
    public List<ContactSummary> getAllContacts(@AuthenticationPrincipal Jwt jwt) {
        final String currentUser = jwt.getClaimAsString("username");
        return Contact.toListOfContactSummary(contactManagerService.findAllByUsername(currentUser));
    }

    @GetMapping("/contacts/{id}")
    public ContactSummary getById(@PathVariable("id") UUID id, @AuthenticationPrincipal Jwt jwt) {
        final String currentUser = jwt.getClaimAsString("username");
        return contactManagerService.findByIdWithUser(id, currentUser).toContactSummary();
    }

    @PostMapping("/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Validated ContactSummary contactSummary, @AuthenticationPrincipal Jwt jwt) {
        final String currentUser = jwt.getClaimAsString("username");
        contactManagerService.saveWithUser(Contact.toContact(contactSummary), currentUser);
    }

    @PutMapping("/contacts/{id}")
    public void update(@PathVariable("id") UUID id, @RequestBody ContactSummary contactSummary, @AuthenticationPrincipal Jwt jwt) {
        final String currentUser = jwt.getClaimAsString("username");
        contactManagerService.updateWithUser(Contact.toContact(contactSummary, id), currentUser);
    }

    @DeleteMapping("/contacts/{id}")
    public void delete(@PathVariable("id") UUID id, @AuthenticationPrincipal Jwt jwt) {
        final String currentUser = jwt.getClaimAsString("username");
        contactManagerService.deleteByIdWithUser(id, currentUser);
    }
}