package com.spring.boot.controllers;

import com.spring.boot.entities.User;
import com.spring.boot.entities.projections.SimpleUser;
import com.spring.boot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public void create(@RequestBody @Validated SimpleUser simpleUser) {
        userService.create(User.toUser(simpleUser));
    }
}
