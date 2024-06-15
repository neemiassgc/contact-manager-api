package spring.manager.api.contact;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExtendedContactRepository {

    void save(Contact contact);

    @Query("select c from Contact c join fetch c.phoneNumberMap join fetch c.emailMap join fetch addressMap join fetch c.user")
    List<Contact> findAll();

    @Query("select c from Contact c left join fetch c.phoneNumberMap left join fetch c.emailMap left join fetch addressMap where c.user.id = :userId")
    List<Contact> findAllByUserId(@Param("userId") String userId);

    void deleteAll();
}