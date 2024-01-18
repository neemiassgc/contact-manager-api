package com.spring.boot.controllers;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.projections.ContactSummary;
import com.spring.boot.services.ContactManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ContactController {

    private final ContactManagerService contactManagerService;

    @GetMapping("/contacts")
    public List<ContactSummary> getAllContacts(@AuthenticationPrincipal Jwt jwt) {
        final String currentUsername = jwt.getClaimAsString("username");
        return Contact.toListOfContactSummary(contactManagerService.findAllByUsername(currentUsername));
    }

    @GetMapping("/contacts/{id}")
    public ContactSummary getById(@PathVariable("id") UUID id) {
        return contactManagerService.findById(id).toContactSummary();
    }
}