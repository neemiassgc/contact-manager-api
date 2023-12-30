package com.spring.boot.repositories;

import com.spring.boot.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    @Query("select c from Contact c join fetch c.phoneNumberMap p join fetch c.emailMap e join fetch addressMap a")
    List<Contact> fetchAll();

    @Query("select c from Contact c join fetch c.phoneNumberMap p join fetch c.emailMap e join fetch addressMap a where c.id = :id")
    Optional<Contact> fetchById(@Param("id") UUID id);

    @Query(
        "select c from Contact c where c.user.username = :username"
    )
    List<Contact> findAllByUsername(@Param("username") String username);
}
