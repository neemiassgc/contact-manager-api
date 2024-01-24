package com.spring.boot.services;

import com.spring.boot.entities.User;

public interface UserService {

    void create(User user);

    User findByUsername(String username);
}
