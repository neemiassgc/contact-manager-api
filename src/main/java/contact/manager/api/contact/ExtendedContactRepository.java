package contact.manager.api.contact;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExtendedContactRepository {

    void save(Contact contact);

    @Query(
        "select c from Contact c join fetch c.phoneNumberMap join fetch c.emailMap " +
        "join fetch c.addressMap join fetch c.user order by c.name asc"
    )
    List<Contact> findAll();

    @Query(
        "select c from Contact c left join fetch c.phoneNumberMap left join fetch c.emailMap " +
        "left join fetch c.addressMap where c.user.id = :userId order by c.name asc"
    )
    List<Contact> findAllByUserId(@Param("userId") String userId);

    void deleteAll();
}