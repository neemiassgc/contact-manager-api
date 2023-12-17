package com.spring.boot.services;

import com.spring.boot.entities.User;

public interface UserService {

    User findByUsername(String username);
}
