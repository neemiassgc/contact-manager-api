package com.spring.boot.repositories;

import com.spring.boot.entities.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends org.springframework.data.repository.Repository<Contact, UUID>, ExtendedContactRepository {

    @Query("select c from Contact c join fetch c.phoneNumberMap p join fetch c.emailMap e join fetch addressMap a where c.id = :id")
    Optional<Contact> findById(@Param("id") UUID id);

    @Query("select count(c.name) = 1 from Contact c where c.id = :id and c.user.username = :username")
    boolean existsByIdAndUser(@Param("id") UUID id, @Param("username") String username);
}