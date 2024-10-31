package contact.manager.api.user;

public interface UserService {

    void create(User user);

    User findById(String id);

    User findByUsername(String username);
}