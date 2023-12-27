package com.spring.boot.services;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.projections.ContactSummary;
import com.spring.boot.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public List<Contact> findAllByUserId(UUID id) {
        return contactRepository.findAllByUserId(id);
    }

    @Override
    public List<ContactSummary> getAll() {
        return contactRepository
            .getAll()
            .stream()
            .map(Contact::toContactSummary)
            .collect(Collectors.toList());
    }

    @Override
    public ContactSummary getById(UUID id) {
       return contactRepository.findById(id)
           .map(Contact::toContactSummary)
           .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "contact not found"));
    }

    @Override
    public void save(Contact contact) {
        contactRepository.save(contact);
    }

    @Override
    public void update(Contact contact) {
        save(contact);
    }

    @Override
    public void deleteById(UUID id) {
        contactRepository.deleteById(id);
    }
}
