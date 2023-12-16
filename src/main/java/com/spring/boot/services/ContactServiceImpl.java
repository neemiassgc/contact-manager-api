package com.spring.boot.services;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.projections.ContactSummary;
import com.spring.boot.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    @Transactional
    public List<ContactSummary> getAll() {
        return contactRepository
            .findAll()
            .stream()
            .map(Contact::toContactSummary)
            .collect(Collectors.toList());
    }
}
