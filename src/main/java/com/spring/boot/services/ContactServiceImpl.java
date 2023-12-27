package com.spring.boot.services;

import com.spring.boot.entities.Contact;
import com.spring.boot.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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
    public List<Contact> fetchAll() {
        return contactRepository.fetchAll();
    }

    @Override
    public Contact fetchById(UUID id) {
       return contactRepository.findById(id)
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
