package com.spring.boot.services;

import com.spring.boot.entities.Contact;
import com.spring.boot.repositories.ExtendedContactRepository;

import java.util.UUID;

public interface ContactManagerService extends ExtendedContactRepository {

    Contact findById(UUID uuid);

    Contact findByIdWithUser(UUID uuid, String username);

    void saveWithUser(Contact contact, String username);

    void updateWithUser(Contact contact, String username);

    void deleteByIdWithUser(UUID id, String usename);
}