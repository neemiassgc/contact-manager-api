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
    public List<Contact> findAllByUsername(String username) {
        return contactRepository.findAllByUsername(username);
    }

    @Override
    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    @Override
    public Contact findById(UUID id) {
       return contactRepository.findById(id)
           .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
    }

    @Override
    public void saveWithUser(final Contact contact, final String username) {
        final User user = userService.findByUsername(username);
        contact.setUser(user);
        save(contact);
    }

    @Override
    public void update(final Contact contact) {
        final Contact contactFromStorage = findById(contact.getId());
        contactFromStorage.merge(contact);
    }

    @Override
    public void deleteById(UUID id) {
        contactRepository.deleteById(id);
    }

    @Override
    public void save(Contact contact) {
        contactRepository.save(contact);
    }

    @Override
    public void deleteAll() {
        contactRepository.deleteAll();
    }
}
