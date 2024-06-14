package spring.manager.api.user;

public interface UserService {

    void create(User user);

    User findById(String id);
}