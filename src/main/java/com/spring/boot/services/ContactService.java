package com.spring.boot.services;

import com.spring.boot.entities.Contact;
import com.spring.boot.entities.projections.ContactSummary;

import java.util.List;
import java.util.UUID;

public interface ContactService {

    List<ContactSummary> getAll();

    ContactSummary getById(UUID id);

    void save(Contact contact);

    void update(Contact contact);

    void deleteById(UUID id);
}
