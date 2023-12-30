package com.spring.boot.services;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.User;
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
public class ContactManagerServiceImpl implements ContactManagerService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserService userService;

    @Override
    public List<Contact> fetchAllByUsername(String username) {
        return contactRepository.findAllByUsername(username);
    }

    @Override
    public List<Contact> fetchAll() {
        return contactRepository.fetchAll();
    }

    @Override
    public Contact fetchById(UUID id) {
       return contactRepository.fetchById(id)
           .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
    }

    @Override
    public void saveWithUser(final Contact contact, final String username) {
        final User user = userService.findByUsername(username);
        contact.setUser(user);
        contactRepository.save(contact);
    }

    @Override
    public void update(Contact contact) {
        contactRepository.save(contact);
    }

    @Override
    public void deleteById(UUID id) {
        contactRepository.deleteById(id);
    }
}
