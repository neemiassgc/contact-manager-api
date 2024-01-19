package com.spring.boot.repositories;

import com.spring.boot.entities.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExtendedContactRepository {

    void save(Contact contact);

    @Query("select c from Contact c join fetch c.phoneNumberMap join fetch c.emailMap join fetch addressMap join fetch c.user")
    List<Contact> findAll();

    @Query("select c from Contact c join fetch c.phoneNumberMap join fetch c.emailMap join fetch addressMap where c.user.username = :username")
    List<Contact> findAllByUsername(@Param("username") String username);

    void deleteAll();
}
