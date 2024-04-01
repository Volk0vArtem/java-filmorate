package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        return service.addUser(user);
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) throws ValidationException {
        return service.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return service.getUsers();
    }

}
