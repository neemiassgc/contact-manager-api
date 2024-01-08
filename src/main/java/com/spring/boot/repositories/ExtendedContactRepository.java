package com.spring.boot.repositories;

import com.spring.boot.entities.Contact;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ExtendedContactRepository {

    void save(Contact contact);

    @Query("select c from Contact c join fetch c.phoneNumberMap p join fetch c.emailMap e join fetch addressMap a")
    List<Contact> findAll();

    @Query("select c from Contact c where c.user.username = :username")
    List<Contact> findAllByUsername(@Param("username") String username);

    @Modifying
    @Query("delete from Contact c where c.id = :id")
    void deleteById(@Param("id") UUID uuid);

    void deleteAll();
}
