package com.spring.boot.controllers;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.projections.ContactSummary;
import com.spring.boot.repositories.ContactRepository;
import com.spring.boot.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/contacts")
    public List<ContactSummary> getAllContacts() {
        return contactService.getAll();
    }
}