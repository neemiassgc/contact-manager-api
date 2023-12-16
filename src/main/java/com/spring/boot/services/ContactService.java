package com.spring.boot.services;

import com.spring.boot.entities.projections.ContactSummary;

import java.util.List;

public interface ContactService {

    List<ContactSummary> getAll();
}
