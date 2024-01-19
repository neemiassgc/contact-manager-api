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
    public Contact findByIdWithUser(UUID id, String username) {
        final Contact contact = findById(id);
        if (!contact.getUser().getUsername().equals(username))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contact does not belong to the user: "+username);
        return contact;
    }

    @Override
    public void saveWithUser(final Contact contact, final String username) {
        final User user = userService.findByUsername(username);
        contact.setUser(user);
        save(contact);
    }

    @Override
    public void updateWithUser(Contact freshContact, String username) {
        final Contact contactFromStorage = findByIdWithUser(freshContact.getId(), username);
        contactFromStorage.merge(freshContact);
    }

    @Override
    public Contact findById(final UUID id) {
        return contactRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
    }

    @Override
    public void deleteByIdWithUser(UUID id, String username) {
        findByIdWithUser(id, username);
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
